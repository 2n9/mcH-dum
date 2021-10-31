package com.github.n9.mch.event;

import com.github.n9.mch.ConsoleLogger;
import com.github.n9.mch.McHostApplication;
import com.github.n9.mch.Utils;
import com.github.n9.mch.minecraft.MinecraftError;
import com.github.n9.mch.minecraft.MinecraftServer;
import com.github.n9.mch.minecraft.ServerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestMessageRecieve extends ListenerAdapter {

    /*String channel_prefix = "mch-";
    String category = "mch";



    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(!event.getMessage().getAuthor().isBot()){
            if(event.getMessage().getContentRaw().equals("start")){
                try {
                    McHostApplication.manager.newServer(event.getChannel().getName(), event.getChannel());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if(event.getMessage().getContentRaw().equals("stop")) {
                MinecraftError error = null;
                error = McHostApplication.manager.getServer(event.getChannel().getName()).stop();
                if(error != null) {
                    event.getChannel().sendMessage("Result: " + error.getMsg()).complete();
                }
            } else if(event.getMessage().getContentRaw().startsWith("add")) {
                String cmd = event.getMessage().getContentRaw().replaceFirst("add", "");
                if(cmd.isEmpty() || cmd.isBlank()) {
                    event.getChannel().sendMessage("add [1234567890]").complete();
                    return;
                }
                String id = cmd.replaceFirst(" ", "");
                List<User> found = new ArrayList<>();
                McHostApplication.jda.retrieveUserById(id).map(
                        found::add
                ).complete();
                User members = found.get(0);
                if(members == null) {
                    event.getChannel().sendMessage("ユーザーが見つかりませんでした。Botに`getid`と送信することで自分のIDを確認することができます。").complete();
                    return;
                }
                User mem = members;

                if(mem == null) {
                    event.getChannel().sendMessage("user not found").complete();
                    return;
                }
                for (PermissionOverride pov : event.getChannel().getMemberPermissionOverrides()){
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
            } else if(event.getMessage().getContentRaw().equalsIgnoreCase("leave")) {
                if(event.getChannel().getName().startsWith(channel_prefix)) {
                    event.getChannel().sendMessage("<@" + event.getMember().getIdLong() + "> \nleave confirmでメンバーから脱退することができます。\nサーバーを初めに作成したユーザーが脱退した場合サーバーは削除されます。\nその他のユーザーが脱退した場合このチャンネルにはアクセスできなくなります。").complete();
                    return;
                }
            } else if(event.getMessage().getContentRaw().equalsIgnoreCase("leave confirm")) {
                if(event.getChannel().getName().startsWith(channel_prefix)) {
                    Utils.sendMessage(event.getAuthor(), event.getChannel().getName() + "のメンバーから外れました。");
                    event.getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + "> がメンバーから脱退しました。").complete();
                    Objects.requireNonNull(event.getChannel().getPermissionOverride(Objects.requireNonNull(event.getMember()))).delete().complete();
                    if(event.getChannel().getTopic().equals(event.getAuthor().getId())) {
                        File userdir = MinecraftServer.genUserDir(event.getChannel().getName());
                        if(userdir.exists()) Utils.delete(userdir.getPath());
                        if(userdir.exists()) ConsoleLogger.info("Delete userdir" + userdir.getPath());
                        for (PermissionOverride pov : event.getChannel().getMemberPermissionOverrides()){
                            try {
                                Utils.sendMessage(pov.getMember().getUser(), event.getChannel() + "が" + event.getMember().getUser().getAsTag() + "によって削除されました。");
                            } catch (NullPointerException ex) {
                                continue;
                            }
                        }
                        event.getChannel().delete().complete();
                    }
                    return;
                } else {
                    return;
                }
            } else if(event.getMessage().getContentRaw().startsWith("cmd")) {
                String cmd = event.getMessage().getContentRaw().replaceFirst("cmd", "");
                if(cmd.isEmpty() || cmd.isBlank()) {
                    event.getChannel().sendMessage("cmd [commands...]").complete();
                    return;
                } else if (McHostApplication.manager.isFound(event.getChannel().getName())){
                    McHostApplication.manager.getServer(event.getChannel().getName()).command(cmd);
                    event.getChannel().sendMessage("コマンドを送信しました。").complete();
                } else {
                    event.getChannel().sendMessage("サーバーが見つかりませんでした。").complete();
                    return;
                }
            } else if(event.getMessage().getContentRaw().startsWith("register")){
                if(!event.getChannel().getName().equals("register")) {
                    return;
                }
                event.getMessage().delete().complete();
                String[] args = event.getMessage().getContentRaw().split(" ");
                if(args.length < 2) {
                    Utils.sendMessage(event.getAuthor(), "register [server name]");
                    return;
                } else {
                    String name = args[1];
                    if(!name.matches("^[0-9a-zA-Z]*$")){
                        Utils.sendMessage(event.getAuthor(), "サーバー名は英数字のみで入力してください。");
                        return;
                    }
                    Category maincg = null;
                    for (Category cg : event.getGuild().getCategories()){
                        if(cg.getName().equals(category)){
                            maincg = cg;
                            for(TextChannel ch : cg.getTextChannels()) {
                                if(ch.getName().equalsIgnoreCase(channel_prefix + name)){
                                    Utils.sendMessage(event.getAuthor(), "既に同じ名前のサーバーが存在します。別の名前に変更してください。");
                                    return;
                                }
                            }
                        }
                    }

                    if(maincg == null) {
                        Utils.sendMessage(event.getAuthor(), "カテゴリが存在しませんでした。\n管理者まで連絡してください。");
                        return;
                    }

                    /*for (TextChannel textch : maincg.getTextChannels()){
                        for (PermissionOverride po : textch.getMemberPermissionOverrides()){
                            System.out.println(textch.getName() + ": " + po.getMember().getUser().getAsTag());
                            if (po.getAllowed().contains(Permission.VIEW_CHANNEL)) {
                                Utils.sendMessage(event.getAuthor(), "あなたは既に別のサーバーのメンバーです。\nサーバーを新規に作成する場合は、既存サーバーのメンバーから脱退して下さい。");
                                return;
                            }
                        }
                    }*/


                    /*TextChannel ch = maincg.createTextChannel(channel_prefix + name).complete();
                    Role r = null;
                    for (Role rr : event.getGuild().getRoles()) {
                        if(rr.getName().equals("@everyone")) {
                            r = rr;
                            break;
                        }
                    }
                    if(r == null) {
                        Utils.sendMessage(event.getAuthor(), "Roleが存在しませんでした。\n管理者まで連絡してください。");
                        return;
                    }

                    ch.createPermissionOverride(r).setDeny(Permission.VIEW_CHANNEL).queue();
                    ch.createPermissionOverride(Objects.requireNonNull(event.getMember())).setAllow(Permission.VIEW_CHANNEL).queue();
                    ch.getManager().setTopic(event.getAuthor().getId()).queue();

                    event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> #" + ch.getName() + " を作成しました。").complete();
                    Message msg = ch.sendMessage("<@" + event.getAuthor().getId() + "> サーバーの作成が完了しました。\n\n" +
                            "このチャンネルでは以下コマンドを使用することができます。\n\n```\n" +
                            "start サーバー起動の信号を送る\n" +
                            "stop  サーバー停止の信号を送る\n" +
                            "cmd [コマンド] [コマンド]をサーバーに送信する（起動中のみ）\n" +
                            "（例: cmd op abc 「abcを管理者にする」）\n" +
                            "leave このチャンネルのメンバーから脱退する\n" +
                            "add [ユーザーID] [ユーザーID]のユーザーをこのチャンネルのメンバーに追加する。\n" +
                            "ユーザーIDに関してはMinecraftHosting Botのメッセージにgetidと入力することや、Discordの開発者モードで確認することができます。\n" +
                            "（例: 902495204358123521）\n```\n\n" +
                            "現在βテストであるため、負荷状態などで起動できる場合とできない場合があります。\n" +
                            "ご了承ください。\n" +
                            "サーバーを一つにまとめている機構のため、毎回IPが変更されます。\n" +
                            "「サーバーを追加」ではなく「直接接続」をおすすめします。\n" +
                            "3分間誰もサーバーにログインしていない場合サーバーは自動で停止します。").complete();
                    File userdir = MinecraftServer.genUserDir(ch.getName());
                    userdir.mkdir();
                    msg.pin().complete();
                }
            }
        }
    }*/
}
