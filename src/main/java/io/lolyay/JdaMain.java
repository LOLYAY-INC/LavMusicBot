package io.lolyay;

import io.lolyay.commands.manager.CommandRegistrer;
import io.lolyay.config.ConfigManager;
import io.lolyay.customevents.EventBus;
import io.lolyay.customevents.events.commands.CommandsRegistredEvent;
import io.lolyay.customevents.events.lifecycle.BotReadyEvent;
import io.lolyay.customevents.events.lifecycle.PreJdaBuildEvent;
import io.lolyay.events.JdaEventsToBusEvents;
import io.lolyay.musicbot.backendswapper.AbstractPlayerManager;
import io.lolyay.musicbot.backendswapper.client.ClientInitializer;
import io.lolyay.musicbot.backendswapper.lavaplayer.LavaInitializer;
import io.lolyay.musicbot.backendswapper.structs.ENVIRONMENT;
import io.lolyay.utils.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class JdaMain {
    public static JDABuilder builder;
    public static JDA jda;
    public static boolean debug = false;
    public static boolean shouldRegisterCommands = true;

    public static AbstractPlayerManager playerManager;
    public static ENVIRONMENT environment;
    public static Scheduler scheduledTasksManager = new Scheduler();
    public static EventBus eventBus = new EventBus();

    public static void init() throws InterruptedException {

        builder = JDABuilder.create(ConfigManager.getConfig("discord-bot-token"), GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
        builder.disableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS);
        Logger.debug("Created Builder, Setting up...");


        builder.setStatus(OnlineStatus.ONLINE);

        //Deprecated now Eventbus: EventRegistrer.register();
        builder.addEventListeners(new JdaEventsToBusEvents(eventBus));
        Logger.debug("Registering events...");

        if (ConfigManager.getConfig("operating-mode").equalsIgnoreCase("lavaplayer")) {
            environment = ENVIRONMENT.LAVALINK;
            Logger.debug("Using LavaPlayer Backend...");
            new LavaInitializer().init();
        } else if (ConfigManager.getConfig("operating-mode").equalsIgnoreCase("nodes")) {
            environment = ENVIRONMENT.CLIENT;
            Logger.debug("Using BackendSwapper Backend...");
            new ClientInitializer().init();
        } else {
            Logger.err("Unknown Operating Mode: " + ConfigManager.getConfig("operating-mode"));
            System.exit(1);
        }

        Logger.log("Music Bot Setup Complete");

        eventBus.post(new PreJdaBuildEvent(builder));
        jda = builder.build().awaitReady();

        jdaBuilt(jda);

    }

    private static void jdaBuilt(JDA jda) {

        eventBus.post(new BotReadyEvent(jda));

        Logger.debug("JDA Built, Registering Commands...");

        if (shouldRegisterCommands)
            CommandRegistrer.registerUnregisteredCommands();
        else
            CommandRegistrer.registerCommandsToRun();

        eventBus.post(new CommandsRegistredEvent(shouldRegisterCommands));

    }


}
