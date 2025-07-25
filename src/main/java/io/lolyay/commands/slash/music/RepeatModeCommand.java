
package io.lolyay.commands.slash.music;

import io.lolyay.LavMusicBot;
import io.lolyay.commands.manager.Command;
import io.lolyay.commands.manager.CommandContext;
import io.lolyay.commands.manager.CommandOptionMultiple;
import io.lolyay.musicbot.queue.RepeatMode;
import io.lolyay.utils.Emoji;
import io.lolyay.utils.KVPair;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import static io.lolyay.commands.manager.CommandOptionMultiple.enumToKVPairArray;


public class RepeatModeCommand extends Command {


    @Override
    public String getName() {
        return "repeat";
    }

    @Override
    public String getDescription() {
        return "Sets the Repeatmode";
    }

    @Override
    public CommandOptionMultiple[] getOptions() {
        KVPair<String, String>[] repeatOptions = enumToKVPairArray(RepeatMode.class);
        return new CommandOptionMultiple[] {
                new CommandOptionMultiple("mode", "The Repeatmode", repeatOptions, OptionType.STRING, true, false)
        };
    }

    @Override
    public boolean requiresPermission() {
        return true;
    }

    @Override
    public void execute(CommandContext event) {
        String argsstr = event.getOption("mode").getAsString();
        RepeatMode value;
        //  Settings settings = event.getClient().getSettingsFor(event.getGuild());
        if(argsstr.equalsIgnoreCase("false") || argsstr.equalsIgnoreCase("off"))
        {
            value = RepeatMode.OFF;
        }
        else if(argsstr.equalsIgnoreCase("true") || argsstr.equalsIgnoreCase("on") || argsstr.equalsIgnoreCase("all"))
        {
            value = RepeatMode.ALL;
        }
        else if(argsstr.equalsIgnoreCase("one") || argsstr.equalsIgnoreCase("single"))
        {
            value = RepeatMode.SINGLE;
        }
        else
        {
            event.reply(Emoji.ERROR.getCode() + " Invalid repeat mode: `" + argsstr + "` Valid modes: `false`, `true`, `all`, `one`, `single`").queue();
            return;
        }
        LavMusicBot.getGuildMusicManager(event.getGuild().getIdLong()).setRepeatMode(value);
        event.reply(Emoji.SUCCESS.getCode() + " Repeat mode is now `"+value.getUserFriendlyName()+"`").queue();
    }
}
