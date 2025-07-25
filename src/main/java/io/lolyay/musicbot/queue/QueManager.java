package io.lolyay.musicbot.queue;

import io.lolyay.config.guildconfig.GuildConfig;
import io.lolyay.lavaboth.tracks.MusicAudioTrack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages the music queue, including adding, removing, skipping, and repeating tracks.
 * <p>
 * This implementation uses a "head-of-the-line" approach. The track at index 0
 * of the queue is always considered the currently playing track.
 */
public class QueManager {

    private final List<MusicAudioTrack> queue = new LinkedList<>();
    private RepeatMode repeatMode = RepeatMode.OFF;
    private final GuildConfig config;
    public long lastSongStartTimeMillis = System.currentTimeMillis();

    public QueManager(GuildConfig config) {
        this.config = config;
    }

    /**
     * Initializes the QueManager, loading the repeat mode from settings.
     * This should be called once when the bot or GuildMusicManager is created.
     */
    public void init() {
        this.repeatMode = config.repeatMode();
    }
    /**
     * Called when the current track finishes playing.
     * It advances the queue based on the current RepeatMode and returns the next track to play.
     *
     * @return The next MusicAudioTrack to play, or null if the queue is finished.
     */
    @Nullable
    public MusicAudioTrack nextTrack() {
        if (queue.isEmpty()) {
            return null;
        }

        // Handle the logic based on the repeat mode
        switch (repeatMode) {
            case SINGLE:
                // Do not advance the queue, just return the current track to play again.
                return queue.getFirst();

            case ALL:
                // Move the current track to the end of the queue.
                MusicAudioTrack trackToMove = queue.removeFirst();
                queue.add(trackToMove);
                break;

            case OFF:
            default:
                // Simply remove the track that just finished.
                queue.removeFirst();
                break;
        }

        // After advancing, if the queue is not empty, return the new track at the head.
        if (!queue.isEmpty()) {
            return queue.getFirst();
        }

        // The queue is now empty.
        return null;
    }

    /**
     * Forces a skip to the next track, ignoring RepeatMode.SINGLE.
     *
     * @return The next MusicAudioTrack to play, or null if the queue is empty.
     */
    @Nullable
    public MusicAudioTrack skip() {
        if (queue.isEmpty()) {
            return null;
        }

        // Move the current track to the end of the queue
        MusicAudioTrack currentTrack = queue.removeFirst();
        queue.add(currentTrack);

        // Return the new track at the head of the queue
        return queue.getFirst();
    }

    /**
     * Adds a track to the end of the queue.
     *
     * @param track The track to add.
     * @return true if the queue was empty before adding this track (i.e., playback should start).
     */
    public boolean queueTrack(MusicAudioTrack track) {
        boolean shouldStartPlaying = queue.isEmpty();
        queue.add(track);
        return shouldStartPlaying;
    }

    /**
     * Adds a track to a specific position in the queue.
     * Useful for a "play next" command (position 1).
     *
     * @param position The 0-based index to insert the track at.
     * @param track    The track to add.
     */
    public void addTrackAt(int position, MusicAudioTrack track) {
        if (position < 0 || position > queue.size()) {
            throw new IndexOutOfBoundsException("Position must be between 0 and " + queue.size());
        }
        queue.add(position, track);
    }


    /**
     * Moves a track from one position to another. This is the "boost" feature.
     *
     * @param from The 0-based index of the track to move.
     * @param to   The 0-based index of the new position for the track.
     * @return The track that was moved.
     */
    public MusicAudioTrack moveTrack(int from, int to) {
        if (from < 0 || from >= queue.size() || to < 0 || to >= queue.size()) {
            throw new IndexOutOfBoundsException("Positions must be valid indices in the queue.");
        }

        // Remove the track from its original position and add it to the new one.
        MusicAudioTrack track = queue.remove(from);
        queue.add(to, track);
        return track;
    }

    /**
     * Shuffles the current queue randomly. The currently playing track (at index 0)
     * will remain in place.
     */
    public void shuffle() {
        if (queue.size() > 1) {
            // Separate the currently playing track
            MusicAudioTrack currentTrack = queue.removeFirst();
            // Shuffle the rest of the queue
            Collections.shuffle(queue);
            // Add the current track back to the front
            queue.addFirst(currentTrack);
        }
    }

    /**
     * Clears all tracks from the queue.
     */
    public void clear() {
        queue.clear();
    }

    /**
     * Gets an unmodifiable list of the tracks currently in the queue.
     * Safe to use for displaying the queue without risk of modification.
     *
     * @return An unmodifiable List of MusicAudioTracks.
     */
    public List<MusicAudioTrack> getQueue() {
        return Collections.unmodifiableList(queue);
    }

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue has no tracks.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Returns the current size of the queue.
     */
    public int size() {
        return queue.size();
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
        this.config.repeatMode(repeatMode);
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }
}