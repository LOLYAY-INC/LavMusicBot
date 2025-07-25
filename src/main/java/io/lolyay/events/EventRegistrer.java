package io.lolyay.events;

import io.lolyay.LavMusicBot;
import io.lolyay.eventbus.Event;
import io.lolyay.eventbus.EventListener;
import io.lolyay.events.listeners.jda.OnGuildVoiceUpdate;
import io.lolyay.events.listeners.jda.OnMessageEvent;
import io.lolyay.events.listeners.jda.OnReadyEventListener;
import io.lolyay.events.listeners.jda.OnSlashCommandInteractionEventListener;
import io.lolyay.events.listeners.lavalink.OnTrackEnd;
import io.lolyay.events.listeners.lavalink.OnTrackPause;
import io.lolyay.events.listeners.lavalink.OnTrackResume;
import io.lolyay.events.listeners.lavalink.OnTrackStart;
import io.lolyay.lavaboth.LavaBoth;

public class EventRegistrer {
    public static void register() {
        LavMusicBot.eventBus.register(new OnReadyEventListener());
        LavMusicBot.eventBus.register(new OnSlashCommandInteractionEventListener());
        LavMusicBot.eventBus.register(new OnGuildVoiceUpdate());
        LavMusicBot.eventBus.register(new OnTrackEnd());
        LavMusicBot.eventBus.register(new OnMessageEvent());
        LavMusicBot.eventBus.register(new OnTrackStart());
        LavMusicBot.eventBus.register(new OnTrackPause());
        LavMusicBot.eventBus.register(new OnTrackResume());
        LavaBoth.eventBus.register(new Relay());

    }

    private static class Relay {
        @EventListener
        public void onEvent(Event event) {
            LavMusicBot.eventBus.post(event);
        }
    }
}
