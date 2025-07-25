package io.lolyay.events.listeners.jda;


import io.lolyay.commands.manager.CommandRegistrer;
import io.lolyay.eventbus.EventListener;
import io.lolyay.utils.Logger;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class OnSlashCommandInteractionEventListener {
    @EventListener
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Logger.debug("Executing (Slash) command: " + event.getName());
        CommandRegistrer.runCommand(event.getName(), event);
    }
}