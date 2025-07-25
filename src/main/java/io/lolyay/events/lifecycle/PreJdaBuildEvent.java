package io.lolyay.events.lifecycle;

import io.lolyay.eventbus.Event;
import net.dv8tion.jda.api.JDABuilder;

public class PreJdaBuildEvent extends Event {
    private final JDABuilder builder;

    public PreJdaBuildEvent(JDABuilder builder) {
        this.builder = builder;
    }

    public JDABuilder getBuilder() {
        return builder;
    }
}