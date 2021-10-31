package com.github.n9.mch.event.command;

import com.github.n9.mch.McHostApplication;
import com.github.n9.mch.minecraft.MinecraftError;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class stopCommand extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getMessage().getAuthor().isBot()) {
            if (event.getMessage().getContentRaw().equals("stop")) {
                MinecraftError error = null;
                error = McHostApplication.manager.getServer(event.getChannel().getName()).stop();
                if (error != null) {
                    event.getChannel().sendMessage("Result: " + error.getMsg()).complete();
                }
            }
        }
    }

}
