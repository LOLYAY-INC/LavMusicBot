package io.lolyay.events.listeners.jda;

import io.lolyay.LavMusicBot;
import io.lolyay.config.ConfigManager;
import io.lolyay.eventbus.EventListener;
import io.lolyay.utils.Logger;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import org.jetbrains.annotations.NotNull;

public class OnGuildVoiceUpdate {
    @EventListener
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if(event.getChannelLeft() == null) return;
        if(event.getGuild().getSelfMember().getVoiceState() == null) return;
        if(event.getGuild().getSelfMember().getVoiceState().getChannel() == null) return;
        if(event.getChannelLeft().asVoiceChannel().getMembers().size() > 1) return;
        if(event.getChannelLeft().asVoiceChannel().getIdLong() == event.getGuild().getSelfMember().getVoiceState().getChannel().getIdLong()) {
            Logger.log("Alone in voice channel, leaving...");
            //   event.getJDA().getDirectAudioController().disconnect(event.getGuild());
            if (ConfigManager.getConfigBool("stop-on-empty-channel")) {
                LavMusicBot.getGuildMusicManager(event.getGuild().getIdLong()).stop();
                Logger.debug("Cleared queue");
            }
        }
    }
}
