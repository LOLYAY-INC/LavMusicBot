package io.lolyay.commands.slash.music;


import io.lolyay.commands.manager.Command;
import io.lolyay.commands.manager.CommandContext;
import io.lolyay.commands.manager.CommandOption;
import io.lolyay.musicbot.lyrics.SyncedLyricsPlayer;
import io.lolyay.utils.Emoji;

public class StopLiveLyricsCommand extends Command {

    @Override
    public String getName() {
        return "stoplive";
    }

    @Override
    public String getDescription() {
        return "Stops the live lyrics";
    }

    @Override
    public CommandOption[] getOptions() {
        return null;
    }

    @Override
    public boolean requiresPermission() {
        return false;
    }


    @Override
    public void execute(CommandContext event) {
        if (!SyncedLyricsPlayer.isLive(event.getGuild().getIdLong())) {
            event.reply(Emoji.ERROR.getCode() + " No Lyrics are currently active").queue();
            return;
        }
        SyncedLyricsPlayer.stop(event.getGuild().getIdLong());
        event.reply(Emoji.SUCCESS.getCode() + " Stopped Live Lyrics!").queue();

    }


}