package io.lolyay.events.listeners.lavalink;

import io.lolyay.eventbus.EventListener;
import io.lolyay.lavaboth.events.track.TrackUnPausedEvent;
import io.lolyay.musicbot.lyrics.SyncedLyricsPlayer;
import io.lolyay.utils.Logger;

public class OnTrackResume {
    @EventListener
    public void onTrackEnd(TrackUnPausedEvent event) {
        Logger.debug("TrackUnPausedEvent for guild " + event.getPlayer().getGuildId());
        SyncedLyricsPlayer.setPaused(event.getPlayer().getGuildId(), false, System.currentTimeMillis());


    }
}
