package io.lolyay.commands.helpers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.Collection;
import java.util.function.Consumer;

public class EditBuilder {
    private final InteractionHook hook;
    private final MessageEditBuilder messageEditBuilder = new MessageEditBuilder();

    public EditBuilder(InteractionHook hook) {
        this.hook = hook;
    }

    public EditBuilder setContent(String content) {
        messageEditBuilder.setContent(content);
        return this;
    }

    public EditBuilder setEmbeds(Collection<? extends MessageEmbed> embeds) {
        messageEditBuilder.setEmbeds(embeds);
        return this;
    }

    public EditBuilder setComponents(Collection<? extends LayoutComponent> components) {
        messageEditBuilder.setComponents(components);
        return this;
    }

    public EditBuilder setAttachments(Collection<? extends FileUpload> attachments) {
        messageEditBuilder.setAttachments(attachments);
        return this;
    }

    private RestAction<Message> buildAction() {
        if (hook == null) return null;
        return hook.editOriginal(messageEditBuilder.build());
    }

    public void queue() {
        RestAction<Message> action = buildAction();
        if (action != null) action.queue();
    }

    public void queue(Consumer<? super Message> success) {
        RestAction<Message> action = buildAction();
        if (action != null) action.queue(success);
    }

    public Message complete() {
        RestAction<Message> action = buildAction();
        return action != null ? action.complete() : null;
    }
}
