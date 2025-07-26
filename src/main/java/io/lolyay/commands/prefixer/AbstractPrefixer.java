package io.lolyay.commands.prefixer;

import io.lolyay.commands.manager.Command;
import io.lolyay.commands.manager.CommandContext;
import io.lolyay.commands.manager.CommandOptionType;
import io.lolyay.config.ConfigManager;
import io.lolyay.utils.KVPair;
import io.lolyay.utils.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractPrefixer {
    protected final String PREFIX;

    public AbstractPrefixer() {
        this.PREFIX = ConfigManager.getConfig("command-prefix");
    }

    public abstract String[] getAliases();

    public abstract Command getReferer();

    public CommandContext generateContext(Message message) {
        Logger.debug("Generating context for command: " + getReferer().getName());
        CommandContext context = CommandContext.of(message, getReferer());
        String args = message.getContentDisplay().replace(PREFIX + getReferer().getName(), "").trim();
        if (getReferer().getOptions() != null) {
            ArrayList<CommandContext.CommandOption> options = extractOptions(message, args);
            options.forEach(context::addOption);
        }
        return context;
    }

    protected ArrayList<CommandContext.CommandOption> extractOptions(Message message, String args) {
        ArrayList<CommandContext.CommandOption> options = new ArrayList<>();
        Command command = getReferer();
        List<String> arguments = new ArrayList<>();

        if (args != null && !args.isBlank()) {
            Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
            Matcher matcher = regex.matcher(args);
            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    arguments.add(matcher.group(1));
                } else if (matcher.group(2) != null) {
                    arguments.add(matcher.group(2));
                } else {
                    arguments.add(matcher.group());
                }
            }
        }

        for (int i = 0; i < command.getOptions().length; i++) {
            CommandOptionType optionType = command.getOptions()[i];
            if (i < arguments.size()) {
                if (optionType.isGreedy()) {
                    String greedyArg = String.join(" ", arguments.subList(i, arguments.size()));
                    options.add(CommandContext.CommandOption.of(new KVPair<>(optionType.getType(), greedyArg), optionType.getName(), false, message));
                    break;
                } else {
                    options.add(CommandContext.CommandOption.of(new KVPair<>(optionType.getType(), arguments.get(i)), optionType.getName(), false, message));
                }
            }
        }
        return options;
    }
}
