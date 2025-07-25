
package io.lolyay.commands.slash.music;


import io.lolyay.LavMusicBot;
import io.lolyay.commands.manager.Command;
import io.lolyay.commands.manager.CommandContext;
import io.lolyay.commands.manager.CommandOption;
import io.lolyay.musicbot.GuildMusicManager;
import io.lolyay.utils.Emoji;

public class ShuffleCommand extends Command {


    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getDescription() {
        return "Shuffles the Queue";
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[0];
    }

    @Override
    public boolean requiresPermission() {
        return true;
    }

    @Override
    public void execute(CommandContext event) {
        GuildMusicManager musicManager = LavMusicBot.getGuildMusicManager(event.getGuild().getIdLong());
        if(!musicManager.isPlaying()){
            event.reply(Emoji.ERROR.getCode() + " No Track is playing, couldn't shuffle!").queue();
            return;
        }
        musicManager.shuffle();
        event.reply(Emoji.SUCCESS.getCode() + " Shuffled the Queue").queue();
    }
}
