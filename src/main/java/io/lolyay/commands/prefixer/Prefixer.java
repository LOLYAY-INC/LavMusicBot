package io.lolyay.commands.prefixer;

import io.lolyay.commands.manager.CommandContext;
import io.lolyay.commands.manager.CommandRegistry;
import io.lolyay.commands.prefixer.prefixers.GenericPrefixer;
import io.lolyay.config.ConfigManager;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Prefixer {
    private final List<AbstractPrefixer> prefixers;

    public Prefixer() {
        this.prefixers = CommandRegistry.getCommands().stream()
                .map(GenericPrefixer::new)
                .collect(Collectors.toList());
    }

    public boolean canHandle(Message message) {
        return this.prefixers.stream()
                .anyMatch(prefixer ->
                        prefixer.getReferer().getName().equalsIgnoreCase(
                                message.getContentDisplay().split(" ")[0].replace(ConfigManager.getConfig("command-prefix"), "")
                        ) || Arrays.stream(prefixer.getAliases()).anyMatch(alias -> alias.equalsIgnoreCase(message.getContentDisplay().split(" ")[0].replace(ConfigManager.getConfig("command-prefix"), "")))
                );
    }

    public AbstractPrefixer getPrefixer(Message message) {
        return prefixers.stream()
                .filter(prefixer ->
                        prefixer.getReferer().getName().equalsIgnoreCase(
                                message.getContentDisplay().split(" ")[0].replace(ConfigManager.getConfig("command-prefix"), "")
                        ) || Arrays.stream(prefixer.getAliases()).anyMatch(alias -> alias.equalsIgnoreCase(message.getContentDisplay().split(" ")[0].replace(ConfigManager.getConfig("command-prefix"), "")))
                )
                .findFirst().orElse(null);
    }


    public void handle(Message message) {
        AbstractPrefixer prefixer = this.getPrefixer(message);
        if (prefixer != null) {
            CommandContext context = prefixer.generateContext(message);
            try {
                prefixer.getReferer().execute(context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
