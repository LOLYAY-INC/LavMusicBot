package io.lolyay.events.commands;

import io.lolyay.eventbus.Event;

public class CommandsRegistredEvent extends Event {
    public boolean registeredCommands;

    public CommandsRegistredEvent(boolean registeredCommands) {
        this.registeredCommands = registeredCommands;
    }

}