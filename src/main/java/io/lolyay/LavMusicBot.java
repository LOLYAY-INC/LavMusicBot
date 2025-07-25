package io.lolyay;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import io.lolyay.commands.manager.CommandRegistrer;
import io.lolyay.config.ConfigManager;
import io.lolyay.config.guildconfig.GuildConfigManager;
import io.lolyay.events.CustomEventBus;
import io.lolyay.events.JdaEventsToBusEvents;
import io.lolyay.events.commands.CommandsRegistredEvent;
import io.lolyay.events.lifecycle.BotReadyEvent;
import io.lolyay.events.lifecycle.PreJdaBuildEvent;
import io.lolyay.jlavalink.data.ConnectionInfo;
import io.lolyay.lavaboth.Environment;
import io.lolyay.lavaboth.backends.common.AbstractPlayerManager;
import io.lolyay.lavaboth.backends.lavalinkclient.player.LavaLinkPlayerManager;
import io.lolyay.lavaboth.backends.lavaplayer.player.LavaPlayerPlayerManager;
import io.lolyay.musicbot.GuildMusicManager;
import io.lolyay.utils.Logger;
import io.lolyay.utils.OtherUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static io.lolyay.lyrics.getters.LyricsGetterManager.initDefault;

public class LavMusicBot {
    private static final Map<Long, GuildMusicManager> musicManagers = new ConcurrentHashMap<>();
    public static JDABuilder builder;
    public static JDA jda;
    public static boolean debug = false;
    public static boolean shouldRegisterCommands = true;
    public static AbstractPlayerManager playerManager;
    public static Environment environment;
    public static Scheduler scheduledTasksManager = new Scheduler();
    public static CustomEventBus eventBus = new CustomEventBus();

    public static void init() throws InterruptedException {

        builder = JDABuilder.create(ConfigManager.getConfig("discord-bot-token"), GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
        builder.disableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS);
        Logger.debug("Created Builder, Setting up...");


        builder.setStatus(OnlineStatus.ONLINE);

        //Deprecated now Eventbus: EventRegistrer.register();
        builder.addEventListeners(new JdaEventsToBusEvents(eventBus));
        Logger.debug("Registering events...");

        if (ConfigManager.getConfig("operating-mode").equalsIgnoreCase("lavaplayer")) {
            environment = Environment.LAVAPLAYER;
            Logger.debug("Using LavaPlayer Backend...");
            setupLavaPlayer();
        } else if (ConfigManager.getConfig("operating-mode").equalsIgnoreCase("nodes")) {
            environment = Environment.LAVALINK_CLIENT;
            Logger.debug("Using BackendSwapper Backend...");
            setupLink(builder);
        } else {
            Logger.err("Unknown Operating Mode: " + ConfigManager.getConfig("operating-mode"));
            System.exit(1);
        }
        Logger.debug("Loading Lyrics Getters...");
        initDefault(ConfigManager.getConfig("musixmatch-user-cookie"));

        Logger.log("Music Bot Setup Complete");

        eventBus.post(new PreJdaBuildEvent(builder));
        jda = builder.build().awaitReady();

        jdaBuilt(jda);
        //   LavaBoth.debug = true;

    }

    public static synchronized GuildMusicManager getGuildMusicManager(long guildId) {
        return musicManagers.computeIfAbsent(guildId, (key) ->
                new GuildMusicManager(key, GuildConfigManager.getGuildConfig(Long.toString(key)), playerManager));
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

    private static void setupLavaPlayer() {
        playerManager = LavaPlayerPlayerManager.getBuilder()
                .setDecoderFormat(StandardAudioDataFormats.DISCORD_OPUS)
                .setOpusEncodingQuality(10)
                .setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH)
                .setTrackStuckTimeout(100)
                .setUseGhostSeeking(true)
                .build();
        new AdditionalSourcesManager().setup((LavaPlayerPlayerManager) playerManager);
        playerManager.getSearchManager().registerDefaultSearchers();


    }


    public static void setupLink(JDABuilder jdaBuilder) {
        ConnectionInfo connectionInfo = null;


        if (ConfigManager.getConfigBool("using-nodes-json-file")) {
            Logger.err("This version of Lavaboth does not support nodes.json file YET!");
            System.exit(1);
          /*  Logger.debug("Loading Nodes from nodes.json file...");
            ArrayList<NodeOptions> nodes = NodesJsonManager.loadNodes();
            for (NodeOptions node : nodes) {
                lavaLinkClient.addNode(node);
            }*/

        } else {
            connectionInfo = new ConnectionInfo("LavaLinkServer",
                    URI.create(getConProtocol() + ConfigManager.getConfig("lavalink-host") + ":" + ConfigManager.getConfig("lavalink-port")),
                    Objects.equals(ConfigManager.getConfig("lavalink-secure"), "true"),
                    ConfigManager.getConfig("lavalink-password")
            );
            Logger.debug("Loading One Node from Config...");
        }


        playerManager = new LavaLinkPlayerManager(jdaBuilder, String.valueOf(OtherUtil.guildIdFromToken(ConfigManager.getConfig("discord-bot-token"))), connectionInfo);
        playerManager.getSearchManager().registerDefaultSearchers();

    }

    private static String getConProtocol() {
        return Objects.equals(ConfigManager.getConfig("lavalink-secure"), "true") ? "wss://" : "ws://";
    }

}
