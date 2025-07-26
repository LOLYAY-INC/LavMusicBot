package io.lolyay.musicbot.lyrics;

import io.lolyay.lavaboth.backends.common.AbstractPlayer;
import io.lolyay.lavaboth.backends.lavaplayer.player.LavaPlayerPlayer;
import io.lolyay.lavaboth.backends.lavaplayer.player.LavaPlayerPlayerManager;
import io.lolyay.utils.Logger;

public class FetchInfoFromLavaPlayer {
    private final LavaPlayerPlayer player;

    public FetchInfoFromLavaPlayer(LavaPlayerPlayer player) {
        this.player = player;
    }
    public void runFetch(){
        long postion = player.getAudioPlayer().getPlayingTrack().getPosition();
        SyncedLyricsPlayer.adjustCurrentTimeMs(player.getGuildId(),postion);
        Logger.debug("Updated Player position for guild " + player.getGuildId());
    }
}
