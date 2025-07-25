package io.lolyay.events.listeners.lavalink;

import io.lolyay.LavMusicBot;
import io.lolyay.eventbus.EventListener;
import io.lolyay.lavaboth.events.track.TrackEndEvent;
import io.lolyay.musicbot.GuildMusicManager;
import io.lolyay.utils.Logger;

public class OnTrackEnd {
    @EventListener
    public void onTrackEnd(TrackEndEvent event) {
        Logger.debug("TrackEndEvent for guild " + event.getPlayer().getGuildId());
        GuildMusicManager musicManager = LavMusicBot.getGuildMusicManager(event.getPlayer().getGuildId());

        if (musicManager.getQueManager().isEmpty()) return;

        if (event.getEndReason().mayStartNext) {
            musicManager.onTrackEnd();
        }
    }
}
