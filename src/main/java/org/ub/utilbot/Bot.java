package org.ub.utilbot;

import net.dv8tion.jda.api.JDA;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.ub.utilbot.commands.ExampleCommand;
import org.ub.utilbot.commands.RepositoryAccess;
import org.ub.utilbot.commandutils.CommandManager;
import org.ub.utilbot.commandutils.MessageReceivedListener;

import javax.annotation.PreDestroy;

@Component
@Profile("!test")
public class Bot implements CommandLineRunner {

    private JDA client;

    private final Logger log = LogManager.getLogger(Bot.class);


    @Override
    public void run(String... args) throws Exception {
        client = JDAClient.getInstance().getJDA();
        client.addEventListener(new MessageReceivedListener());

        log.info("Bot started.");

        CommandManager.registerCommand(new ExampleCommand());
        CommandManager.registerCommand(new RepositoryAccess());

    }

    @PreDestroy
    public void destroy() {
        client.shutdownNow();
        log.info("Destroyed");
    }

}
