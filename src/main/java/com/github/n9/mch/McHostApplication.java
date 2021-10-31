package com.github.n9.mch;

import com.github.n9.mch.event.ReadyListener;
import com.github.n9.mch.event.TestMessageRecieve;
import com.github.n9.mch.event.command.*;
import com.github.n9.mch.minecraft.MinecraftServer;
import com.github.n9.mch.minecraft.ServerManager;
import com.github.n9.mch.thread.ServerInformationThread;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class McHostApplication {

	public static JDA jda;
	public static ServerManager manager;
	public static String javaPath;
	public static ConsoleLogger logger;

	public static String IP;

	public static void main(String[] args) throws IOException, InterruptedException, LoginException {
		//SpringApplication.run(McHostApplication.class, args);

		PaperUpdate.run();

		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				whatismyip.openStream()));

		IP = in.readLine();

		manager = new ServerManager();
		// test case
		// System.out.println("FreePort Test: " + manager.freePort());

		/*	test case
		if(args.length < 1) {
			System.out.println("args: " + args.length);
			return;
		}
		*/

		logger = new ConsoleLogger();

		JDABuilder builder = JDABuilder.createDefault(args[0]);
		builder.addEventListeners(new ReadyListener());
		//builder.addEventListeners(new SlashStartCommand());
		builder.addEventListeners(new TestMessageRecieve());

		// commands
		builder.addEventListeners(new addCommand());
		builder.addEventListeners(new getIdCommand());
		builder.addEventListeners(new leaveCommand());
		builder.addEventListeners(new registerCommand());
		builder.addEventListeners(new servercmdCommand());
		builder.addEventListeners(new startCommand());
		builder.addEventListeners(new stopCommand());

		jda = builder.build().awaitReady();

		//jda.upsertCommand("start", "サーバーを起動します。").complete();
		//jda.upsertCommand("stop", "サーバーを停止します。").complete();
		//jda.upsertCommand()

		new ServerInformationThread().start();

		Runtime.getRuntime().addShutdownHook(new Thread(
				() -> {
					for (MinecraftServer server : manager.getAll()) server.process.destroy();
				}
		));
	}



}
