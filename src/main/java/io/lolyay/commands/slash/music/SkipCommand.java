
package io.lolyay.commands.slash.music;


import io.lolyay.LavMusicBot;
import io.lolyay.commands.manager.Command;
import io.lolyay.commands.manager.CommandContext;
import io.lolyay.commands.manager.CommandOption;
import io.lolyay.lavaboth.tracks.MusicAudioTrack;
import io.lolyay.utils.Emoji;

public class SkipCommand extends Command {


    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips the current track and plays the next one in the queue.";
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
        if (!LavMusicBot.getGuildMusicManager(event.getGuild().getIdLong()).isPlaying()) {
            event.reply(Emoji.ERROR.getCode() + " No Track is playing, couldn't skip!").queue();
            return;
        }

        MusicAudioTrack currentTrack = LavMusicBot.getGuildMusicManager(event.getGuild().getIdLong()).skip();
        if(currentTrack == null)
            event.reply(Emoji.SUCCESS.getCode() + " No more Tracks to play, stopping!").queue();
        else
            event.reply(Emoji.SUCCESS.getCode() + " Skipped: **" + currentTrack.trackInfo().title() + "** (Requested by " + currentTrack.userData().userName() + " )").queue();

    }
}
