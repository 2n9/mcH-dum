package com.github.n9.mch.event.command;

import com.github.n9.mch.McHostApplication;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SlashStartCommand extends ListenerAdapter {

    public SlashStartCommand(){
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if(!event.getName().equals("start")) return;
        event.reply("サーバー起動のシグナルを送信しました。").queue();
        try {
            McHostApplication.manager.newServer(event.getTextChannel().getName(), event.getTextChannel());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
