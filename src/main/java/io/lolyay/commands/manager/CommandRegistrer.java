package io.lolyay.commands.manager;

import io.lolyay.LavMusicBot;
import io.lolyay.config.ConfigManager;
import io.lolyay.utils.KVPair;
import io.lolyay.utils.Logger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.*;

public class CommandRegistrer {
    private static final ArrayList<SlashCommandData> commands = new ArrayList<>();
    private static final Map<String, Command> commandsToRun = new HashMap<>();

    private static final Command[] commandsToBeRegistered = CommandRegistry.getCommands().toArray(new Command[0]);

    public static void runCommand(String name, SlashCommandInteractionEvent event) {
        Command command = commandsToRun.get(name);

        if (command.requiresPermission() && !canRunCommand(Objects.requireNonNull(event.getMember()))) {
            event.reply("You don't have permission to use this command").setEphemeral(true).queue();
            return;
        }
        try {
            command.execute(CommandContext.of(event));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void register(Guild guild) {
        commands.clear();
        for (Command command : commandsToBeRegistered) {
            registerCommandImpl(command);
        }
        clearGuildCommands(guild.retrieveCommands().complete(), guild);
        guild.updateCommands().addCommands(commands).complete();
        Logger.debug("Registered Commands for guild: " + guild.getName());
    }

    public static void register() { // Bot command
        commands.clear();
        for (Command command : commandsToBeRegistered) {
            registerCommandImpl(command);
        }

        clearBotCommands(LavMusicBot.jda.retrieveCommands().complete());
        LavMusicBot.jda.updateCommands().addCommands(commands).complete();
        // not needed : registertorun();
        Logger.debug("Registered Commands");
        Logger.success("Bot is now Ready, You can use it now!");

    }


    /**
     * Registers only the commands that aren't already registered in Discord.
     * This is more efficient than re-registering all commands.
     */
    public static void registerUnregisteredCommands() {
        try {
            // Get all currently registered command names
            List<String> registeredCommandNames = LavMusicBot.jda.retrieveCommands().complete()
                    .stream()
                    .map(net.dv8tion.jda.api.interactions.commands.Command::getName)
                    .toList();

            // Find commands that need to be registered
            List<SlashCommandData> commandsToRegister = new ArrayList<>();
            List<Command> commandsToAdd = new ArrayList<>();

            for (Command command : commandsToBeRegistered) {
                if (!registeredCommandNames.contains(command.getName())) {
                    Logger.debug("Command not found, will register: " + command.getName());
                    
                    // Create the command data
                    SlashCommandData commandData = createCommandData(command);
                    if (commandData != null) {
                        commandsToRegister.add(commandData);
                        commandsToAdd.add(command);
                    }
                }
            }

            // Register new commands if any
            if (!commandsToRegister.isEmpty()) {
                clearBotCommands(LavMusicBot.jda.retrieveCommands().complete());
                commandsToRegister.clear();
                commandsToAdd.clear();
                for (Command command : commandsToBeRegistered) {
                    SlashCommandData commandData = createCommandData(command);
                    if (commandData != null) {
                        commandsToRegister.add(commandData);
                        commandsToAdd.add(command);
                    }

                }
                LavMusicBot.jda.updateCommands().addCommands(commandsToRegister).queue(
                        success -> {
                            Logger.success("Successfully registered " + commandsToRegister.size() + " new commands, Bot is now Ready, You can use it now!");
                            // Only add to runtime commands after successful registration
                            Arrays.stream(commandsToBeRegistered).forEach(CommandRegistrer::registerCommandToRunImpl);
                        },
                        error -> Logger.err("Failed to register commands: " + error.getMessage())
                );
            } else {
                Arrays.stream(commandsToBeRegistered).forEach(CommandRegistrer::registerCommandToRunImpl);
                Logger.debug("No new commands to register");
                Logger.success("Bot is now Ready, You can use it now!");
            }
        } catch (Exception e) {
            Logger.err("Error in registerUnregisteredCommands: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates the SlashCommandData for a command
     */
    private static SlashCommandData createCommandData(Command command) {
        try {
            if (command.getOptions() == null) {
                return Commands.slash(command.getName(), command.getDescription());
            } else if (command.getOptions() instanceof CommandOption[]) {
                return commandOptionBuilderImpl(command);
            } else if (command.getOptions() instanceof CommandOptionMultiple[]) {
                return commandOptionMultipleBuilderImpl(command);
            } else {
                Logger.warn("Command %s has invalid options type".formatted(command.getName()));
                return null;
            }
        } catch (Exception e) {
            Logger.err("Error creating command data for " + command.getName() + ": " + e.getMessage());
            return null;
        }
    }

    private static void clearGuildCommands(List<net.dv8tion.jda.api.interactions.commands.Command> commandList, Guild guild) {
        for (net.dv8tion.jda.api.interactions.commands.Command command : commandList) {
            guild.deleteCommandById(command.getId()).complete();
            Logger.debug("Deleted Guild Command: " + command.getName());
        }
    }

    private static void clearBotCommands(List<net.dv8tion.jda.api.interactions.commands.Command> commandList) {
        for (net.dv8tion.jda.api.interactions.commands.Command command : commandList) {
            try {
                LavMusicBot.jda.deleteCommandById(command.getId()).complete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Logger.debug("Deleted Bot Command: " + command.getName());
        }
    }

    public static void registerCommandsToRun() {
        for (Command command : commandsToBeRegistered) {
            registerCommandToRunImpl(command);
        }
    }

    private static void registerCommandImpl(Command command) {
        if (command.getOptions() == null) {
            SlashCommandData slash = Commands.slash(command.getName(),command.getDescription());
            commands.add(slash);
        }
        if (command.getOptions() instanceof CommandOption[]) {
            commands.add(commandOptionBuilderImpl(command));
        }
        else if (command.getOptions() instanceof CommandOptionMultiple[]) {
            commands.add(commandOptionMultipleBuilderImpl(command));
        } else { // Left unchanged! Registering, but warning
            Logger.warn("Command %s has no options, use with ´CommandOption[0]´ if no arguments are needed!".formatted(command.getName()));
            SlashCommandData slash = Commands.slash(command.getName(), command.getDescription());
            commands.add(slash);
        }
        registerCommandToRunImpl(command);
    }

    private static void registerCommandToRunImpl(Command command) {
        commandsToRun.put(command.getName(), command);
    }

    private static SlashCommandData commandOptionBuilderImpl(Command command){
        String name = command.getName();
        String description = command.getDescription();
        CommandOption[] options = (CommandOption[]) command.getOptions();


        SlashCommandData slash = Commands.slash(name,description);
        for (CommandOption option : options) {
            slash.addOption(option.getType(), option.getName(), option.getDescription(),option.isRequired());
        }
        return slash;
    }


    private static SlashCommandData commandOptionMultipleBuilderImpl(Command command) {
        String name = command.getName();
        String description = command.getDescription();
        CommandOptionMultiple[] options = (CommandOptionMultiple[]) command.getOptions();


        SlashCommandData slash = Commands.slash(name, description);
        for (CommandOptionMultiple option : options) {
            OptionData data = new OptionData(option.getType(), option.getName(), option.getDescription(),option.isRequired());
            for (KVPair<String, String> pair : option.getOptions()) {
                data.addChoice(pair.getKey(), pair.getValue());
            }
            slash.addOptions(data);
        }
        return slash;
    }

    public static boolean canRunCommand(Member member) {
        ArrayList<String> adminRoles = (ArrayList<String>) ConfigManager.getConfigRaw("role-id-whitelist");
        if (!ConfigManager.getConfigBool("permissions-enabled"))
            return true;
        for (String role : adminRoles) {
            if (member.getRoles().contains(LavMusicBot.jda.getRoleById(role))) {
                return !ConfigManager.getConfigBool("whitelist-acts-as-blacklist");
            }
        }
        return ConfigManager.getConfigBool("whitelist-acts-as-blacklist");
    }



    public static ArrayList<KVPair<String, String>> getCommandsWithDescription() {
        ArrayList<KVPair<String, String>> commands = new ArrayList<>();
        for (Command command : commandsToRun.values()) {
            commands.add(new KVPair<>(command.getName(), command.getDescription()));
        }
        return commands;
    }
}
