package io.lolyay.commands.helpers;

import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;

import javax.annotation.CheckReturnValue;

public class ModalBuilder {
    private final ModalCallbackAction action;

    @CheckReturnValue
    public ModalBuilder(ModalCallbackAction action) {
        this.action = action;
    }

    public void queue() {
        if (action == null) return;
        action.queue();
    }

    @CheckReturnValue
    public Void complete() {
        if (action == null) return null;
        return action.complete();
    }
}
