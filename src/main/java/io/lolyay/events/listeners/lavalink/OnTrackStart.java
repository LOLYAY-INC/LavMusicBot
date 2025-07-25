package io.lolyay.events.listeners.lavalink;

import io.lolyay.LavMusicBot;
import io.lolyay.eventbus.EventListener;
import io.lolyay.lavaboth.events.track.TrackStartedEvent;
import io.lolyay.musicbot.GuildMusicManager;
import io.lolyay.musicbot.lyrics.SyncedLyricsPlayer;
import io.lolyay.utils.Logger;

public class OnTrackStart {
    @EventListener
    public void onTrackEnd(TrackStartedEvent event) {
        Logger.debug("TrackStartEvent for guild " + event.getPlayer().getGuildId());
        GuildMusicManager musicManager = LavMusicBot.getGuildMusicManager(event.getPlayer().getGuildId());
        musicManager.getQueManager().lastSongStartTimeMillis = System.currentTimeMillis();
        if (SyncedLyricsPlayer.isLive(event.getPlayer().getGuildId()))
            SyncedLyricsPlayer.nextSong(event.getPlayer().getGuildId(), event.getPlayer().getCurrentTrack().trackInfo().title(), musicManager.getQueManager().lastSongStartTimeMillis);


    }
}
