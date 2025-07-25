package io.lolyay.events.listeners.jda;

import io.lolyay.commands.prefixer.Prefixer;
import io.lolyay.config.ConfigManager;
import io.lolyay.eventbus.EventListener;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static io.lolyay.commands.manager.CommandRegistrer.canRunCommand;

public class OnMessageEvent {
    private static final Prefixer prefixer = new Prefixer();

    @EventListener
    public void onMessageEvent(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().startsWith(ConfigManager.getConfig("command-prefix"))) {
            if (prefixer.canHandle(event.getMessage()) && canRunCommand(event.getMember())) {
                prefixer.handle(event.getMessage());
            }
        }
    }
}
