package io.lolyay.commands.helpers;

import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import javax.annotation.CheckReturnValue;
import java.util.function.Consumer;

public class ReplyBuilder<T> {
    private final RestAction<T> action;

    @CheckReturnValue
    public ReplyBuilder(RestAction<T> action) {
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null");
        }
        this.action = action;
    }

    @CheckReturnValue
    public ReplyBuilder<T> setEphemeral(boolean ephemeral) {
        if (action instanceof ReplyCallbackAction) {
            ((ReplyCallbackAction) action).setEphemeral(ephemeral);
        } else if (action instanceof WebhookMessageCreateAction) {
            ((WebhookMessageCreateAction<?>) action).setEphemeral(ephemeral);
        }
        // For other actions like MessageCreateAction, ephemeral is not supported and is ignored.
        return this;
    }

    public void queue() {
        action.queue();
    }

    public void queue(Consumer<? super T> success) {
        action.queue(success);
    }

    @CheckReturnValue
    public T complete() {
        return action.complete();
    }
}
