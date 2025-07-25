package io.lolyay.events.bot;

import io.lolyay.eventbus.Event;
import net.dv8tion.jda.api.events.GenericEvent;

public class JDAEvent extends Event {
    private final GenericEvent genericEvent;

    public JDAEvent(GenericEvent genericEvent) {
        this.genericEvent = genericEvent;
    }

    public GenericEvent getGenericEvent() {
        return genericEvent;
    }
}
