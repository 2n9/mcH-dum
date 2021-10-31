package com.github.n9.mch.event.command;

import com.github.n9.mch.McHostApplication;
import com.github.n9.mch.minecraft.MinecraftError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class addCommand extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getMessage().getAuthor().isBot()) {
           if (event.getMessage().getContentRaw().startsWith("add")) {
                String cmd = event.getMessage().getContentRaw().replaceFirst("add", "");
                if (cmd.isEmpty() || cmd.isBlank()) {
                    event.getChannel().sendMessage("add [1234567890]").complete();
                    return;
                }
                String id = cmd.replaceFirst(" ", "");
                List<User> found = new ArrayList<>();
                McHostApplication.jda.retrieveUserById(id).map(
                        found::add
                ).complete();
                User members = found.get(0);
                if (members == null) {
                    event.getChannel().sendMessage("ユーザーが見つかりませんでした。Botに`getid`と送信することで自分のIDを確認することができます。").complete();
                    return;
                }
                User mem = members;

                if (mem == null) {
                    event.getChannel().sendMessage("user not found").complete();
                    return;
                }
                for (PermissionOverride pov : event.getChannel().getMemberPermissionOverrides()) {
                    try {
                        if (pov.getMember().getUser().getAsTag().equals(mem.getAsTag())) {
                            event.getChannel().sendMessage(cmd + "は既に参加済みです。").complete();
                            return;
                        }
                    } catch (NullPointerException ex) {
                        continue;
                    }
                }
                Member m2 = event.getGuild().retrieveMemberById(id).complete();
                event.getChannel().createPermissionOverride(Objects.requireNonNull(m2)).setAllow(Permission.VIEW_CHANNEL).queue();
                event.getChannel().sendMessage("<@" + mem.getId() + "> が参加しました。").complete();
            }
        }
    }

}
