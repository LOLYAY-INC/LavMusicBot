
package io.lolyay.commands.slash.music;


import io.lolyay.LavMusicBot;
import io.lolyay.commands.manager.Command;
import io.lolyay.commands.manager.CommandContext;
import io.lolyay.commands.manager.CommandOption;
import io.lolyay.musicbot.GuildMusicManager;
import io.lolyay.utils.Emoji;

public class PauseCommand extends Command {


    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return "Pauses the current Track, resume with /resume";
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
            event.reply(Emoji.ERROR.getCode() + " No Track is playing, couldn't pause!").queue();
            return;
        }
        if(musicManager.isPaused()){
            event.reply(Emoji.ERROR.getCode() + " Already paused!").queue();
            return;
        }
        musicManager.pause();
        event.reply(Emoji.SUCCESS.getCode() + " Paused Playback!").queue();
    }
}
