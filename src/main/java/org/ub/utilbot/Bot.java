package org.ub.utilbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.ub.utilbot.commands.*;
import org.ub.utilbot.commandutils.CommandManager;
import org.ub.utilbot.commandutils.MessageReceivedListener;

import javax.annotation.PreDestroy;

@Component
@Profile("!test")
public class Bot implements CommandLineRunner, ApplicationContextAware {

    @Value("${app.jda.token}")
    private String token;

    private JDA client;

    private final Logger log = LogManager.getLogger(Bot.class);

    private ApplicationContext appContext;

    @Override
    public void run(String... args) throws Exception {
        client = JDABuilder.createDefault(token).build();
        client.addEventListener(new MessageReceivedListener());

        log.info("Bot started.");

        //CommandManager.registerCommand(new ExampleCommand());
        CommandManager.registerCommand(appContext.getBean(RepositoryAccess.class));
        CommandManager.registerCommand(appContext.getBean(RemindCommand.class));
        CommandManager.registerCommand(appContext.getBean(RemoveCommand.class));
        CommandManager.registerCommand(appContext.getBean(RequestMeeting.class));
    }

    @PreDestroy
    public void destroy() {
        client.shutdownNow();
        log.info("Destroyed");
    }

    public JDA getClient() {
        return client;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.appContext = applicationContext;

    }
}
