package io.lolyay;


import io.lolyay.config.ConfigManager;
import io.lolyay.lavaboth.backends.lavaplayer.player.LavaPlayerPlayerManager;
import io.lolyay.lavaboth.backends.lavaplayer.sourceshelper.SourcesBuilder;
import io.lolyay.utils.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AdditionalSourcesManager {
    public static final String REFRESH_TOKEN = RefreshTokenStore.load() == null ? ConfigManager.getConfig("youtube-oauth-refresh-token") : RefreshTokenStore.load();

    public void setup(LavaPlayerPlayerManager playerManager) {
        SourcesBuilder sourcesBuilder = new SourcesBuilder(playerManager).addDefault()
                .addYoutube(REFRESH_TOKEN);
        if (ConfigManager.getConfigBool("enable-deezer"))
            sourcesBuilder.setupDeezer(ConfigManager.getConfig("deezer-decryption-key"),
                    ConfigManager.getConfig("deezer-arl-cookie"));
        if (ConfigManager.getConfigBool("enable-apple-music"))
            sourcesBuilder.setupAppleMusic(ConfigManager.getConfig("apple-music-token"), ConfigManager.getConfig("country-code"));
        if (ConfigManager.getConfigBool("enable-spotify"))
            sourcesBuilder.setupSpotify(ConfigManager.getConfig("spotify-client-id"), ConfigManager.getConfig("spotify-client-secret"), null, ConfigManager.getConfig("country-code"));
        if (ConfigManager.getConfigBool("enable-tidal"))
            sourcesBuilder.setupTidal(ConfigManager.getConfig("country-code"), ConfigManager.getConfig("tidal-token"));

        sourcesBuilder.buildAndRegister();

    }

    private static class RefreshTokenStore {
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
