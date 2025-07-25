package io.lolyay.events;

import io.lolyay.eventbus.EventBus;
import net.dv8tion.jda.api.events.GenericEvent;

public class CustomEventBus extends EventBus {

    public void post(GenericEvent event) {
        dispatchEvent(event);
    }
}
