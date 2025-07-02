package io.lolyay.musicbot.backendswapper.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.arbjerg.lavalink.client.Helpers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.*;
import dev.lavalink.youtube.clients.skeleton.Client;
import io.lolyay.JdaMain;
import io.lolyay.config.ConfigManager;
import io.lolyay.utils.Logger;
import net.dv8tion.jda.api.JDABuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.lolyay.JdaMain.builder;

public class LavaInitializer extends io.lolyay.musicbot.backendswapper.Initializer {
    @Override
    public void init() {
        setup(Helpers.getUserIdFromToken(ConfigManager.getConfig("discord-bot-token")), builder);
    }

    private void setup(long botId, JDABuilder jdaBuilder) {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager() {
        };
        AudioSourceManagers.registerRemoteSources(playerManager);
        YoutubeAudioSourceManager source = new YoutubeAudioSourceManager(true, new Music(), new Tv(), new TvHtml5Embedded(), new Web(), new WebEmbedded(),
                new MWeb(), new AndroidMusic(), new AndroidVr(), new Ios());


        //OAUTH2
        String token = RefreshTokenStore.load() == null ? ConfigManager.getConfig("youtube-oauth-refresh-token") : RefreshTokenStore.load();
        if (token == null || token.isBlank()) {
            Logger.err("No refresh token found, will need to log in again");
            source.useOauth2(null, false);
        } else {
            Logger.success("Loaded refresh token for youtube oauth2 : " + token);
            source.useOauth2(token, true);
            RefreshTokenStore.store(token);
        }
        if (source.getOauth2RefreshToken() != null && !source.getOauth2RefreshToken().isBlank()) {
            RefreshTokenStore.store(source.getOauth2RefreshToken());
            Logger.success("Saved refresh token for youtube oauth2");
        }


        playerManager.registerSourceManager(source);

        JdaMain.playerManager = new LavaLinkPlayerManager(playerManager, new LavaPlayerFactory(playerManager));
    }

    private class RefreshTokenStore {
        public static void store(String token) {
            try {
                Files.writeString(Path.of("refresh_token.txt"), token);
            } catch (IOException e) {
                Logger.err("Could not write refresh token to file, will need to log in again");
            }
        }

        public static String load() {
            try {
                return Files.readString(Path.of("refresh_token.txt"));
            } catch (IOException e) {
                Logger.err("Could not read refresh token from file, will need to log in again");
                return null;
            }
        }
    }
}
