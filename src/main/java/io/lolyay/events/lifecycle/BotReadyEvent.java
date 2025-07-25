package io.lolyay.events.lifecycle;

import io.lolyay.eventbus.Event;
import net.dv8tion.jda.api.JDA;

public class BotReadyEvent extends Event {
    private final JDA jda;

    public BotReadyEvent(JDA jda) {
        this.jda = jda;
    }

    public JDA getJda() {
        return jda;
    }

}