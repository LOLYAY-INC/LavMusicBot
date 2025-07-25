package io.lolyay.embedmakers;

import io.lolyay.lavaboth.tracks.MusicAudioTrack;
import io.lolyay.musicbot.GuildMusicManager;
import io.lolyay.utils.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;

public class PlayCommandEmbedGenerator {
    public static EmbedBuilder generate(MusicAudioTrack track, boolean isNow, GuildMusicManager musicManager, String source) {
        EmbedBuilder builder = new EmbedBuilder();
        if (isNow) {
            builder.setTitle(Emoji.PLAY.getCode() + " Now playing: **" + track.trackInfo().title() + "** by " + track.trackInfo().author());
        } else {
            builder.setTitle(Emoji.CD.getCode() + " Added to queue: **" + track.trackInfo().title() + "** by " + track.trackInfo().author());
            builder.addField("Queue Position", "#" + (musicManager.getQueue().indexOf(track) + 1), true);

        }

        builder.addField("Loaded from:", source, true);
        builder.addField("Requested by:", track.userData().userName(), true);

        builder.setThumbnail(track.trackInfo().artWorkUrl());

        return builder;
    }

}
