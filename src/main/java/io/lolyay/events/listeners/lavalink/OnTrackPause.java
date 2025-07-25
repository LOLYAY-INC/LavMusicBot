package io.lolyay.events.listeners.lavalink;

import io.lolyay.eventbus.EventListener;
import io.lolyay.lavaboth.events.track.TrackPausedEvent;
import io.lolyay.musicbot.lyrics.SyncedLyricsPlayer;
import io.lolyay.utils.Logger;

public class OnTrackPause {
    @EventListener
    public void onTrackEnd(TrackPausedEvent event) {
        Logger.debug("TrackPauseEvent for guild " + event.getPlayer().getGuildId());
        SyncedLyricsPlayer.setPaused(event.getPlayer().getGuildId(), true, System.currentTimeMillis());


    }
}
