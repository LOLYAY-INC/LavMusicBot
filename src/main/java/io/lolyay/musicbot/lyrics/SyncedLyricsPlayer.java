package io.lolyay.musicbot.lyrics;


import io.lolyay.config.ConfigManager;
import io.lolyay.lavaboth.backends.common.AbstractPlayer;
import io.lolyay.lavaboth.backends.lavaplayer.player.LavaPlayerPlayer;
import io.lolyay.lyrics.LyricsNotFoundException;
import io.lolyay.lyrics.getters.MusixMatchGetter;
import io.lolyay.lyrics.records.live.LiveData;
import io.lolyay.lyrics.records.live.LiveLyrics;
import io.lolyay.utils.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.*;
import java.util.concurrent.*;

/**
 * This class is responsible for playing live lyrics in a Discord guild.
 */
public class SyncedLyricsPlayer {

    private static final Map<Long, GuildLyricsPlayer> activePlayers = new ConcurrentHashMap<>();
    private static final Map<String, LiveData> globalLyricsCache = new ConcurrentHashMap<>();
    private static final Set<String> globalPendingSongs = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void start(long guildId, Message message, AbstractPlayer aPlayer) {
        GuildLyricsPlayer player = activePlayers.computeIfAbsent(guildId, id -> new GuildLyricsPlayer());
        Logger.debug("Starting lyrics session for guild " + guildId);
        player.setMessage(message);
        if (aPlayer instanceof LavaPlayerPlayer)
            scheduler.schedule(
                    new FetchInfoFromLavaPlayer((LavaPlayerPlayer) aPlayer)::runFetch,
                    3000,
                    TimeUnit.MILLISECONDS
            );
    }

    public static void willBeAvailable(String songName) {
        globalPendingSongs.add(songName);
    }

    public static void precacheSong(String songName, String songAuthor) {
        if (globalLyricsCache.containsKey(songName)) {
            CompletableFuture.completedFuture(null);
            return;
        }

        Logger.debug("Precaching lyrics for: " + songName);
        CompletableFuture<LiveLyrics> lyricsFuture = null;
        try {
            lyricsFuture = new MusixMatchGetter(ConfigManager.getConfig("musixmatch-user-cookie")).getLiveLyrics(songName, songAuthor);
        } catch (LyricsNotFoundException e) {
            throw new RuntimeException(e);
        }

        lyricsFuture.thenAccept(lyrics -> {
            if (lyrics != null && lyrics.liveData() != null) {
                try {
                    LiveData liveData = lyrics.liveData();
                    globalLyricsCache.put(songName, liveData);
                    globalPendingSongs.remove(songName);
                    Logger.debug("Successfully cached and parsed lyrics for: " + songName);
                } catch (Exception e) {
                    Logger.err("Failed to parse live lyrics for " + songName + ": " + e.getMessage());
                    globalPendingSongs.remove(songName);
                }
            } else {
                Logger.warn("Could not retrieve live lyrics for caching: " + songName);
                globalPendingSongs.remove(songName);
            }
        }).exceptionally(ex -> {
            Logger.err("Exception during lyrics precaching for " + songName + ": " + ex.getMessage());
            globalPendingSongs.remove(songName);
            return null;
        });
    }

    public static void nextSong(long guildId, String songName, long songStartTimeMillis) {
        GuildLyricsPlayer player = activePlayers.get(guildId);
        if (player == null) {
            Logger.warn("nextSong called for guild " + guildId + " without an active session. Call start() first.");
            return;
        }
        player.playSong(songName, songStartTimeMillis);
    }

    public static void adjustStartTime(long guildId, long newStartTimeMillis) {
        GuildLyricsPlayer player = activePlayers.get(guildId);
        if (player != null) {
            player.adjustStartTime(newStartTimeMillis);
        }
    }

    /**
     * Adjusts the playback position to a specific point in the song.
     *
     * @param guildId          The ID of the guild where the lyrics are playing.
     * @param newCurrentTimeMs The new elapsed time in the song, in milliseconds.
     */
    public static void adjustCurrentTimeMs(long guildId, long newCurrentTimeMs) {
        GuildLyricsPlayer player = activePlayers.get(guildId);
        if (player != null) {
            player.adjustCurrentTimeMs(newCurrentTimeMs);
        }
    }

    public static void setPaused(long guildId, boolean isPaused, long timestamp) {
        GuildLyricsPlayer player = activePlayers.get(guildId);
        if (player != null) {
            player.setPausedState(isPaused, timestamp);
        }
    }

    public static void stop(long guildId) {
        GuildLyricsPlayer player = activePlayers.remove(guildId);
        if (player != null) {
            player.performStop();
        }
    }

    public static boolean isLive(long guildId) {
        return activePlayers.containsKey(guildId);
    }

    private static class GuildLyricsPlayer {
        private final List<String> allLinesText = new ArrayList<>();
        private final List<LiveData.TimedLyric> timedLines = new ArrayList<>();
        private Message message;
        private ScheduledExecutorService scheduler;
        private boolean isPaused = false;
        private long songStartTimeMillis;
        private long pauseTimeMillis;
        private String title;
        private String author;
        private String source;

        public void setMessage(Message message) {
            this.message = message;
        }

        public void playSong(String songName, long songStartTimeMillis) {
            if (this.message == null) {
                Logger.err("Cannot play song: Message object is not set. Call start() first.");
                return;
            }

            LiveData liveData = globalLyricsCache.get(songName);

            if (liveData != null) {
                globalPendingSongs.remove(songName);
                proceedWithPlayback(liveData, songStartTimeMillis);
            } else if (globalPendingSongs.contains(songName)) {
                Logger.debug("Lyrics for '" + songName + "' not cached yet, waiting...");
                schedulePlayAttempt(songName, songStartTimeMillis, 5);
            } else {
                Logger.err("Error: Lyrics for '" + songName + "' are not cached and were not marked as pending.");
                message.editMessageEmbeds(new EmbedBuilder().setDescription("Could not find lyrics for this track.").build()).queue();
            }
        }

        private void schedulePlayAttempt(String songName, long startTime, int retriesLeft) {
            if (retriesLeft <= 0) {
                Logger.err("Gave up waiting for lyrics for '" + songName + "'.");
                globalPendingSongs.remove(songName);
                if (message != null) {
                    message.editMessageEmbeds(new EmbedBuilder().setDescription("Could not find lyrics for this track (timeout).").build()).queue();
                }
                return;
            }

            ScheduledExecutorService waiter = Executors.newSingleThreadScheduledExecutor();
            waiter.schedule(() -> {
                LiveData liveData = globalLyricsCache.get(songName);
                if (liveData != null) {
                    Logger.debug("Lyrics for '" + songName + "' became available. Playing now.");
                    globalPendingSongs.remove(songName);
                    proceedWithPlayback(liveData, startTime);
                } else {
                    schedulePlayAttempt(songName, startTime, retriesLeft - 1);
                }
                waiter.shutdown();
            }, 1, TimeUnit.SECONDS);
        }

        private void proceedWithPlayback(LiveData liveData, long songStartTimeMillis) {
            if (this.scheduler != null && !this.scheduler.isShutdown()) {
                this.scheduler.shutdownNow();
            }

            this.songStartTimeMillis = songStartTimeMillis;
            this.isPaused = false;

            this.title = liveData.title();
            this.author = liveData.author();
            this.source = liveData.source();

            this.allLinesText.clear();
            this.allLinesText.addAll(liveData.allLinesText());

            this.timedLines.clear();
            this.timedLines.addAll(liveData.timedLines());

            beginScheduling();
        }

        public void setPausedState(boolean shouldBePaused, long timestamp) {
            if (this.isPaused == shouldBePaused) return;

            this.isPaused = shouldBePaused;

            if (shouldBePaused) {
                this.pauseTimeMillis = timestamp;
                if (this.scheduler != null && !this.scheduler.isShutdown()) {
                    this.scheduler.shutdownNow();
                }
                double elapsedTimeSeconds = (this.pauseTimeMillis - this.songStartTimeMillis) / 1000.0;
                message.editMessageEmbeds(generateEmbedForLine(findLastHighlightedIndex(elapsedTimeSeconds))).queue();
                Logger.debug("Lyrics paused for guild " + message.getGuild().getIdLong());
            } else {
                long pauseDurationMillis = timestamp - this.pauseTimeMillis;
                this.songStartTimeMillis += pauseDurationMillis;
                beginScheduling();
                Logger.debug("Lyrics resumed for guild " + message.getGuild().getIdLong());
            }
        }

        public void adjustStartTime(long newStartTimeMillis) {
            if (this.songStartTimeMillis == newStartTimeMillis) return;
            this.songStartTimeMillis = newStartTimeMillis;
            if (!isPaused) {
                if (this.scheduler != null && !this.scheduler.isShutdown()) {
                    this.scheduler.shutdownNow();
                }
                beginScheduling();
            }
        }

        /**
         * Calculates the new song start time based on the desired elapsed time
         * and calls the main adjustment logic.
         *
         * @param newCurrentTimeMs The new elapsed time in the song, in milliseconds.
         */
        public void adjustCurrentTimeMs(long newCurrentTimeMs) {
            long newStartTimeMillis = System.currentTimeMillis() - newCurrentTimeMs;
            adjustStartTime(newStartTimeMillis);
            Logger.debug("Adjusting lyrics position for guild " + message.getGuild().getIdLong() + " to " + newCurrentTimeMs + "ms");
        }

        public void performStop() {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdownNow();
            }
            if (message != null) {
                message.delete().queue(null, (error) -> Logger.warn("Failed to delete lyrics message, it might have been deleted already."));
            }
            Logger.debug("Lyrics playback stopped for guild " + message.getGuild().getIdLong());
        }

        private void beginScheduling() {
            this.scheduler = Executors.newSingleThreadScheduledExecutor();
            long currentTimeMillis = System.currentTimeMillis();
            double elapsedTimeSeconds = (currentTimeMillis - this.songStartTimeMillis) / 1000.0;

            int initialHighlightIndex = findLastHighlightedIndex(elapsedTimeSeconds);
            message.editMessageEmbeds(generateEmbedForLine(initialHighlightIndex)).queue();

            for (LiveData.TimedLyric timedLine : timedLines) {
                long lyricTargetMillis = (long) (timedLine.timestamp() * 1000);
                long delayMillis = (this.songStartTimeMillis + lyricTargetMillis) - currentTimeMillis;

                if (delayMillis > 0) {
                    scheduler.schedule(() -> {
                        if (!isPaused) {
                            message.editMessageEmbeds(generateEmbedForLine(timedLine.globalIndex())).queue(null, (error) -> performStop());
                        }
                    }, delayMillis, TimeUnit.MILLISECONDS);
                }
            }
        }

        private int findLastHighlightedIndex(double elapsedTimeSeconds) {
            int lastHighlightIndex = -1;
            for (LiveData.TimedLyric timedLine : timedLines) {
                if (timedLine.timestamp() <= elapsedTimeSeconds) {
                    lastHighlightIndex = timedLine.globalIndex();
                } else {
                    break;
                }
            }
            return lastHighlightIndex;
        }

        private MessageEmbed generateEmbedForLine(int highlightedIndex) {
            EmbedBuilder builder = new EmbedBuilder();
            String currentTitle = this.title + " by " + this.author;
            if (this.isPaused) {
                currentTitle += " [PAUSED]";
            }
            builder.setTitle(currentTitle);
            builder.setFooter(this.source);

            StringBuilder contentBuilder = new StringBuilder();
            for (int i = 0; i < allLinesText.size(); i++) {
                String line = allLinesText.get(i);
                if (i == highlightedIndex) {
                    contentBuilder.append("**").append(line.isEmpty() ? " " : line).append("  <-**").append("\n");
                } else {
                    contentBuilder.append(line.isEmpty() ? " " : line).append("\n");
                }
            }

            String fullContent = contentBuilder.toString();
            if (fullContent.length() > 4000) {
                fullContent = fullContent.substring(0, 4000) + "...";
            }
            builder.setDescription(fullContent);

            return builder.build();
        }
    }
}