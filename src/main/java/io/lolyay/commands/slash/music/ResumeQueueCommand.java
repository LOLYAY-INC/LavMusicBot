package io.lolyay.commands.slash.music;


import io.lolyay.LavMusicBot;
import io.lolyay.commands.manager.Command;
import io.lolyay.commands.manager.CommandContext;
import io.lolyay.commands.manager.CommandOption;
import io.lolyay.embedmakers.StatusEmbedGenerator;
import io.lolyay.musicbot.GuildMusicManager;
import io.lolyay.utils.Emoji;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.Collections;

public class ResumeQueueCommand extends Command {

    @Override
    public String getName() {
        return "resumequeue";
    }

    @Override
    public String getDescription() {
        // You might want to update this if it's a fully functional command now
        return "Joins the voice channel and resumes the queue where it last left off.";
    }

    @Override
    public CommandOption[] getOptions() {
        return null;
    }

    @Override
    public boolean requiresPermission() {
        return true;
    }

    @Override
    public void execute(CommandContext event) {
        final Member member = event.getMember();
        final Guild guild = event.getGuild();


        final GuildVoiceState memberVoiceState = member.getVoiceState();
        if (memberVoiceState == null || !memberVoiceState.inAudioChannel()) {
            event.reply(Emoji.ERROR.getCode() + " You must be in a voice channel to use this command!").setEphemeral(true).queue();
            return;
        }

        final VoiceChannel memberChannel = memberVoiceState.getChannel().asVoiceChannel();

        // Check bot permissions
        if (!guild.getSelfMember().hasPermission(memberChannel, Permission.VOICE_CONNECT)) {
            event.reply(Emoji.ERROR.getCode() + " I don't have permission to connect to your voice channel!").setEphemeral(true).queue();
            return;
        }
        if (!guild.getSelfMember().hasPermission(memberChannel, Permission.VOICE_SPEAK)) {
            event.reply(Emoji.ERROR.getCode() + " I don't have permission to speak in your voice channel!").setEphemeral(true).queue();
            return;
        }

        event.deferReply(false);


        final GuildMusicManager musicManager = LavMusicBot.getGuildMusicManager(guild.getIdLong());

        if (!musicManager.isPlaying()) {
            event.reply(Emoji.ERROR.getCode() + " No Track is playing, couldn't resume!").queue();
            return;
        }

        if (musicManager.getQueManager().isEmpty()) {
            event.reply(Emoji.ERROR.getCode() + " Queue is empty!").queue();
            return;
        }

        member.getJDA().getDirectAudioController().connect(memberChannel);
        event.reply(Emoji.SUCCESS.getCode() + " Resumed Playback!").queue();
        event.replyEmbeds(Collections.singleton(StatusEmbedGenerator.generate(musicManager).build())).queue();
    }
}