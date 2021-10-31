package com.github.n9.mch.event.command;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class getIdCommand extends ListenerAdapter {

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equals("getid")) {
            if(!event.getAuthor().isBot()){
                event.getChannel().sendMessage("あなたのID: " + event.getAuthor().getId()).complete();
            }
        }
    }

}
