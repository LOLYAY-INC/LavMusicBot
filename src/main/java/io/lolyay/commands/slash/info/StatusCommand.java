
package io.lolyay.commands.slash.info;


import io.lolyay.LavMusicBot;
import io.lolyay.commands.manager.Command;
import io.lolyay.commands.manager.CommandContext;
import io.lolyay.commands.manager.CommandOption;
import io.lolyay.embedmakers.StatusEmbedGenerator;
import io.lolyay.musicbot.GuildMusicManager;

import java.util.Collections;

public class StatusCommand extends Command {


    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "Gets the current Status";
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[0];
    }

    @Override
    public boolean requiresPermission() {
        return false;
    }

    @Override
    public void execute(CommandContext event) {
        GuildMusicManager musicManager = LavMusicBot.getGuildMusicManager(event.getGuild().getIdLong());
        event.replyEmbeds(Collections.singletonList(StatusEmbedGenerator.generate(musicManager).build())).queue();
    }
}
