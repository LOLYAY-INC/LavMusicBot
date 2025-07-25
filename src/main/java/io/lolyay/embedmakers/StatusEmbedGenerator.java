package io.lolyay.embedmakers;

import io.lolyay.LavMusicBot;
import io.lolyay.config.ConfigManager;
import io.lolyay.config.guildconfig.GuildConfig;
import io.lolyay.config.guildconfig.GuildConfigManager;
import io.lolyay.lavaboth.Environment;
import io.lolyay.musicbot.GuildMusicManager;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class StatusEmbedGenerator {
    public static EmbedBuilder generate(GuildMusicManager musicManager) {
        EmbedBuilder builder = new EmbedBuilder();
        GuildConfig guildConfig = GuildConfigManager.getGuildConfig(musicManager.getGuildId().toString());
        builder.setTitle(genTitle(musicManager));
        if (!musicManager.getQueManager().getQueue().isEmpty())
            builder.addField("", "**" + getTitle(musicManager) + "** by **" + getArtist(musicManager) + "**", false);
        else
            builder.addField("**Queue is empty**", "", true);
        builder.addField("**Queue:**", musicManager.getQueManager().getQueue().size() + " tracks left", true);
        builder.addField("**Repeat:**", musicManager.getRepeatMode().getEmoji() + " - " + musicManager.getRepeatMode().getUserFriendlyName(), true);
        builder.addField("**Volume:**", musicManager.getVolume() + " / 100 (Default: " + guildConfig.volume() + ")", true);
        builder.addField("**Tracks played:**", String.valueOf(guildConfig.plays()), true);
        builder.addField("**Live lyrics:**", ConfigManager.getConfigBool("live-lyrics-enabled") ? "Enabled" : "Disabled", true);
        builder.addField("**Bot type (Integrated Lavaplayer / Lavalink Nodes):**", LavMusicBot.environment == Environment.LAVAPLAYER ? "Integrated Lavaplayer" : "Lavalink Nodes", false);
        if (LavMusicBot.environment == Environment.LAVAPLAYER) {
            builder.addField("**Country code:**", ConfigManager.getConfig("country-code"), false);
            builder.addField("**Spotify Enabled:**", ConfigManager.getConfigBool("enable-spotify") ? "Enabled" : "Disabled", false);
            builder.addField("**Apple Music Enabled:**", ConfigManager.getConfigBool("enable-apple-music") ? "Enabled" : "Disabled", false);
            builder.addField("**Deezer Enabled:**", ConfigManager.getConfigBool("enable-deezer") ? "Enabled" : "Disabled", false);
            builder.addField("**Tidal Enabled:**", ConfigManager.getConfigBool("enable-tidal") ? "Enabled" : "Disabled", false);
        }
        if(!musicManager.getQueManager().getQueue().isEmpty())
            builder.setThumbnail(getImageURL(musicManager));
        builder.setColor(genColor(musicManager.isPlaying(),musicManager.isPaused()));
        return builder;
    }


    private static String genPreTitle(boolean isPlaying, boolean isPaused) {
        if(isPaused)
            return "Paused";
        else if(isPlaying)
            return "Playing";
        else
            return "Stopped";
    }

    private static Color genColor(boolean isPlaying,boolean isPaused) {
        if(isPaused)
            return Color.YELLOW;
        else if(isPlaying)
            return Color.GREEN;
        else
            return Color.RED;
    }

    private static String genTitle(GuildMusicManager musicManager) {
        if(musicManager.getQueManager().getQueue().isEmpty())
            return genPreTitle(false,false);
        return genPreTitle(true, musicManager.isPaused()) + ": " + musicManager.getQueManager().getQueue().getFirst().trackInfo().title();
    }

    private static String getImageURL(GuildMusicManager musicManager) {
        return musicManager.getQueManager().getQueue().getFirst().trackInfo().artWorkUrl();
    }

    private static String getArtist(GuildMusicManager musicManager) {
        return musicManager.getQueManager().getQueue().getFirst().trackInfo().author();
    }

    private static String getTitle(GuildMusicManager musicManager) {
        return musicManager.getQueManager().getQueue().getFirst().trackInfo().title();
    }


}
