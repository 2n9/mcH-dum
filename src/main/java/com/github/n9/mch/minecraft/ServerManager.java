package com.github.n9.mch.minecraft;

import com.github.n9.mch.McHostApplication;
import com.github.n9.mch.Utils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerManager {

    List<MinecraftServer> servers;
    public File maindir = new File(System.getProperty("user.dir") + File.separator + "data");
    public int max = 50;

    public ServerManager(){
        servers = new ArrayList<>();

    }

    public List<MinecraftServer> getAll(){
        return servers;
    }

    public void newServer(String name, TextChannel channel) throws IOException, InterruptedException {
        if(!isFound(name)){
            if(servers.size() > max){
                channel.sendMessage("サーバーリソースが枯渇しているため現在サーバーを新規に起動することはできません。\nserverinfoチャンネルからサーバー起動数を確認の上、再度お試しください。").complete();
                return;
            }
            MinecraftServer server = new MinecraftServer(name, channel, freePort(), "java", "-Xmx750M", "-jar", "server.jar", "nogui");
            servers.add(server);
            server.start();
        }
    }

    public int freePort() {
        List<Integer> uses = new ArrayList<>();
        servers.forEach(server -> uses.add(server.port));
        int min = 25000;
        int max = 26000;
        int r = Utils.getRandom(min, max);
        while(uses.contains(r)) r = Utils.getRandom(min, max);
        return r;
    }

    public MinecraftServer getServer(String name){
        for(MinecraftServer server : getAll()){
            if(server.name.equals(name)) return server;
        }
        return null;
    }

    public File getUserdir(String name) {
        return new File(System.getProperty("user.dir") + File.separator + "data", name);
    }

    public boolean isFound(String name){
        if(servers.size() < 1)  return false;
        try {
            for (MinecraftServer server : servers) {
                if (server.name.equals(name)) {
                    if (server.isAlive()) {
                        return true;
                    } else {
                        servers.remove(server);
                    }
                }
            }
        } catch (NullPointerException ex) {
            return false;
        }
        return false;
    }

}
