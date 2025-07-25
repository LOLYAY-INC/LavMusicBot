package io.lolyay.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class JdaEventsToBusEvents implements EventListener {

    private final CustomEventBus eventBus;

    public JdaEventsToBusEvents(CustomEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        eventBus.post(genericEvent);
    }
}
