package io.lolyay.events.listeners.jda;

import io.lolyay.eventbus.EventListener;
import io.lolyay.utils.Logger;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.jetbrains.annotations.NotNull;

public class OnReadyEventListener {
    @EventListener
    public void onReady(@NotNull ReadyEvent event) {
        Logger.log("Bot connected to Discord! ( Doesn't Mean its ready )");
    }
}
