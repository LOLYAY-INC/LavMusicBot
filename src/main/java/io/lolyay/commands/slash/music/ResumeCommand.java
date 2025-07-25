
package io.lolyay.commands.slash.music;


import io.lolyay.LavMusicBot;
import io.lolyay.commands.manager.Command;
import io.lolyay.commands.manager.CommandContext;
import io.lolyay.commands.manager.CommandOption;
import io.lolyay.musicbot.GuildMusicManager;
import io.lolyay.utils.Emoji;

public class ResumeCommand extends Command {


    @Override
    public String getName() {
        return "resume";
    }

    @Override
    public String getDescription() {
        return "Resumes the current Track if paused with /pause.";
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
            event.reply(Emoji.ERROR.getCode() + " No Track was playing, couldn't resume!").queue();
            return;
        }
        if(!musicManager.isPaused()){
            event.reply(Emoji.ERROR.getCode() + " Already playing!").queue();
            return;
        }
        musicManager.resume();
        event.reply(Emoji.SUCCESS.getCode() + " Resumed Playback!").queue();
    }
}
