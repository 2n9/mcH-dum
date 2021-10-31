package com.github.n9.mch.minecraft;

import com.github.n9.mch.McHostApplication;
import com.github.n9.mch.Utils;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinecraftServer {

    TextChannel channel;
    String name;
    public Process process;
    Thread thread;
    int port;
    File datadir = new File(System.getProperty("user.dir") + File.separator + "data");
    public File userdir;
    String[] command;

    public MinecraftServer(String name, TextChannel channel, int port, String... command) {
        this.port = port;
        this.name = name;
        this.channel = channel;

        this.command = command;

        this.userdir = new File(datadir, name);
        if (!this.userdir.exists()) {
            this.userdir.mkdirs();
        }

        this.process = null;
    }

    public static File genUserDir(String name){
        return new File(new File(System.getProperty("user.dir") + File.separator + "data"), name);
    }

    public boolean isAlive() {
        if (process == null) return false;
        if (!process.isAlive()) return false;
        if (!thread.isAlive()) return false;
        return true;
    }

    public MinecraftError start() {
        if (isAlive()) return MinecraftError.STARTING;
        try {
            first();
        } catch (IOException e) {
            e.printStackTrace();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ProcessBuilder builder = new ProcessBuilder();
                if(Utils.getOS() == Utils.OS.WINDOWS) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("cmd,/c,");
                    for(String str : command) {
                        if(str.equals("java")) {
                            buffer.append("C:\\Users\\user\\.jdks\\corretto-16.0.2\\bin\\java,");
                        } else
                            buffer.append(str + ",");
                    }
                    builder.command(buffer.toString().split(","));
                    System.out.println("cmd: " + Arrays.toString(buffer.toString().split(",")));
                } else {
                    builder.command(command);
                    System.out.println(Arrays.toString(command));
                }
                builder.directory(userdir);
                //builder.destroyOnExit();
                try {
                    process = builder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("StartServer " + name + " p:" + port);
                channel.sendMessage("サーバーが起動しました。\nIP: " + McHostApplication.IP + ":" + port).complete();
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                process.destroy();
                System.out.println("End Process " + name + "#" + process.pid());
                channel.sendMessage("サーバーが停止しました。").complete();
                //TODO: send server close message
            }
        });
        thread.start();
        return null;
    }

    private void first() throws IOException {
        if(new File(userdir.getPath() + File.separator + "logs").exists())
            Utils.delete(userdir.getPath() + File.separator + "logs");
        if(new File(userdir.getPath() + File.separator + "cache").exists())
            Utils.delete(userdir.getPath() + File.separator + "cache");
        if(new File(userdir.getPath() + File.separator + "server.jar").exists())
            new File(userdir.getPath() + File.separator + "server.jar").delete();
        if(new File(userdir.getPath() + File.separator + "server.properties").exists())
            new File(userdir.getPath() + File.separator + "server.properties").delete();
        if(new File(userdir.getPath() + File.separator + "plugins").exists())
            Utils.delete(userdir.getPath() + File.separator + "plugins");
        List<String> files = new ArrayList<>();
        for (File f : userdir.listFiles()) {
            files.add(f.getName());
        }

        try {
            File serverJar = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "base", "server.jar");
            File targetJar = new File(userdir.getPath() + File.separator + "server.jar");
            Files.copy(serverJar.toPath(), targetJar.toPath());
        } catch (FileAlreadyExistsException ex ){
            // null
        }

        try {
            File cacheF = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "base", "cache");
            File cacheF2 = new File(userdir.getPath() + File.separator + "cache");
            Files.copy(cacheF.toPath(), cacheF2.toPath());
            cacheF2.mkdirs();
            for(File f : cacheF2.listFiles()) {
                Files.copy(f.toPath(), new File(cacheF2 + File.separator + f.getName()).toPath());
            }
        } catch (FileAlreadyExistsException ex ){
            // null
        }

        try {
            File plugin = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "base", "plugins");
            File plugin2 = new File(userdir.getPath() + File.separator + "plugins");
            plugin2.mkdirs();
            for(File f : plugin.listFiles()) {
                Files.copy(f.toPath(), new File(plugin2 + File.separator + f.getName()).toPath());
            }
        } catch (FileAlreadyExistsException ex ){
            // null
        }


        if (!files.contains("eula.txt")) {
            File eula = new File(userdir, "eula.txt");
            eula.createNewFile();
            FileWriter file = new FileWriter(eula);
            PrintWriter pw = new PrintWriter(new BufferedWriter(file));
            pw.println("eula=true");
            pw.flush();
            pw.close();
        }

        File properties = new File(userdir, "server.properties");
        properties.createNewFile();
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(properties), StandardCharsets.UTF_8)));
        List<String> elements = List.of(
                "spawn-protection=16",
                "max-tick-time=60000",
                "query.port=" + port,
                "generator-settings=",
                "sync-chunk-writes=true",
                "force-gamemode=false",
                "allow-nether=true",
                "enforce-whitelist=false",
                "gamemode=survival",
                "broadcast-console-to-ops=true",
                "enable-query=false",
                "player-idle-timeout=0",
                "difficulty=easy",
                "spawn-monsters=true",
                "broadcast-rcon-to-ops=true",
                "op-permission-level=4",
                "pvp=true",
                "entity-broadcast-range-percentage=100",
                "snooper-enabled=true",
                "level-type=default",
                "hardcore=false",
                "enable-status=true",
                "enable-command-block=true",
                "max-players=10",
                "network-compression-threshold=256",
                "resource-pack-sha1=",
                "max-world-size=29999984",
                "function-permission-level=2",
                "rcon.port=25575",
                "server-port=" + port,
                "debug=false",
                "server-ip=",
                "spawn-npcs=true",
                "allow-flight=true",
                "level-name=world",
                "view-distance=6",
                "resource-pack=",
                "spawn-animals=true",
                "white-list=false",
                "rcon.password=",
                "generate-structures=true",
                "max-build-height=256",
                "online-mode=true",
                "level-seed=",
                "use-native-transport=true",
                "prevent-proxy-connections=false",
                "enable-jmx-monitoring=false",
                "enable-rcon=false",
                "motd=A Minecraft Server"
        );

        for(String s : elements) {
            pw.println(s);
            pw.flush();
        }
        pw.close();
    }

    public MinecraftError stop() {
        if (!isAlive()) return MinecraftError.NOT_FOUND;
        command("save-all","stop");
        return null;
    }

    public void command(String... commands) {
        if(!isAlive()) return;
        PrintWriter out = new PrintWriter(process.getOutputStream());
        for(String cmd : commands) {
            out.print(cmd);
            out.print(System.getProperty("line.separator"));
            out.flush();
        }
    }

}
