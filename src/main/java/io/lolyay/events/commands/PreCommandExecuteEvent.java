package io.lolyay.events.commands;

import io.lolyay.commands.manager.Command;
import io.lolyay.eventbus.Event;
import net.dv8tion.jda.api.entities.Member;

public class PreCommandExecuteEvent extends Event {
    private final Command command;
    private final Member member;


    public PreCommandExecuteEvent(Command command, Member member) {
        this.command = command;
        this.member = member;

    }

    public Command getCommand() {
        return command;
    }

    public Member getMember() {
        return member;
    }
}