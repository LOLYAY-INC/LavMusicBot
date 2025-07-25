package io.lolyay;

import io.lolyay.config.ConfigLoader;
import io.lolyay.config.guildconfig.GuildConfigLoader;
import io.lolyay.config.guildconfig.GuildConfigManager;
import io.lolyay.events.EventRegistrer;
import io.lolyay.events.lifecycle.PreInitEvent;
import io.lolyay.utils.KVPair;
import io.lolyay.utils.Logger;

import java.io.FileNotFoundException;

import static io.lolyay.utils.update.UpdateChecker.checkForUpdates;

public class Main {
    public static void main(String[] args) {
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(Main::onShutdown));

        parseArgs(args);
        Logger.log("Starting bot...");

        try {
            ConfigLoader.load();
        } catch (FileNotFoundException e) {
            Logger.err("Error creating / reading config file.");
            System.exit(1);
        }
        try {
            GuildConfigLoader.init();
        } catch (Exception e) {
            Logger.err("Error with Guild Config.");
            System.exit(1);
        }

        Logger.debug("Checking for updates...");
        checkForUpdates();

        Logger.debug("Loading JDA...");
        try {
            EventRegistrer.register();

            LavMusicBot.eventBus.post(new PreInitEvent());
            LavMusicBot.init();
        } catch (Exception e) {
            Logger.err("Error while starting bot: ");
            e.printStackTrace();
        }
    }

    private static void parseArgs(String[] args) {
        for(String arg : args) {
            if (!arg.contains("=")) {
                useArg(new KVPair<>(arg, ""));
                continue;
            }
            String[] dta = arg.split("=");
            KVPair<String, String> pair = new KVPair<>(dta[0], dta[1]);
            useArg(pair);
        }
    }

    private static void useArg(KVPair<String, String> pair) {
        switch (pair.getKey()) {
            case "-DEBUG":
            {
                Logger.log("Debug mode enabled");
                LavMusicBot.debug = true;
                break;
            }

            case "-OVERWRITE_CONFIG":
            {
                Logger.warn("Overwriting Config...");
                ConfigLoader.overWriteConfig = true;
                break;
            }
            case "-NO_REGISTER_COMMANDS":
            {
                Logger.warn("Registering commands...");
                LavMusicBot.shouldRegisterCommands = false;
                break;
            }
        }
    }

    private static void onShutdown() {
        try {
            Logger.log("Shutting down bot...");
            // Only save if GuildConfigManager is initialized
            GuildConfigManager.saveGuildConfigCacheToFiles();

            Logger.log("Shutdown complete");
        } catch (Exception e) {
            Logger.err("Error during shutdown: " + e.getMessage());
        }
    }
}
