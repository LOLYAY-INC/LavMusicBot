package io.lolyay.commands.slash.music;


import io.lolyay.JdaMain;
import io.lolyay.commands.manager.Command;
import io.lolyay.commands.manager.CommandContext;
import io.lolyay.commands.manager.CommandOption;
import io.lolyay.config.ConfigManager;
import io.lolyay.embedmakers.LyricsEmbedGenerator;
import io.lolyay.musicbot.GuildMusicManager;
import io.lolyay.musicbot.lyrics.getters.MusixMatchGetter;
import io.lolyay.musicbot.lyrics.live.SyncedLyricsPlayer;
import io.lolyay.utils.Emoji;
import io.lolyay.utils.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.util.Collections;

public class GetLyricsCommand extends Command {

    @Override
    public String getName() {
        return "lyrics";
    }

    @Override
    public String getDescription() {
        return "Gets the lyrics of the currently playing song";
    }

    @Override
    public CommandOption[] getOptions() {
        return null;
    }

    @Override
    public boolean requiresPermission() {
        return false;
    }


    @Override
    public void execute(CommandContext event) {
        GuildMusicManager musicManager = JdaMain.playerManager.getGuildMusicManager(event.getGuild().getIdLong());

        // DOESNT WORK (FIXED?)
        if (!ConfigManager.getConfigBool("lyrics-enabled")) {
            event.reply(Emoji.ERROR.getCode() + " Lyrics are currently disabled").queue();
            return;
        }

        if (musicManager.getQueue().isEmpty()) {
            event.reply(Emoji.ERROR.getCode() + " Queue is empty").queue();
            return;
        }


        event.deferReply(false);
        new MusixMatchGetter().getLyrics(musicManager.getQueue().getFirst().trackInfo().title()).thenAcceptAsync(
                lyrics -> {
                    if (lyrics == null) {
                        event.reply(Emoji.ERROR.getCode() + " No Lyrics found for this song").queue();
                        return;
                    }

                    event.replyEmbeds(Collections.singleton(LyricsEmbedGenerator.generate(lyrics).build())).queue(message -> {
                        Message sentMessage = (Message) message;
                        if (ConfigManager.getConfigBool("live-lyrics-enabled")) {
                            try {
                                SyncedLyricsPlayer.start(event.getGuild().getIdLong(), sentMessage);
                                SyncedLyricsPlayer.nextSong(sentMessage.getGuildIdLong(), musicManager.getQueue().getFirst().trackInfo().title(), musicManager.getQueue().getFirst().startTime());
                            } catch (Exception e) {
                                Logger.err("Error starting synced lyrics: " + e.getMessage());
                                event.reply(Emoji.ERROR.getCode() + " Error starting synced lyrics: " + e.getMessage()).queue();
                            }
                        }
                    });
                }
        ).exceptionally((e) -> {
            Logger.err("Error getting lyrics: " + e.getMessage());
            event.reply(Emoji.ERROR.getCode() + " Error getting lyrics: " + e.getMessage()).queue();
            e.printStackTrace();
            return null;
        });

    }


}