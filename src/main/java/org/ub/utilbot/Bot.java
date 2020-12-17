package org.ub.utilbot;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.ub.utilbot.commands.ExampleCommand;
import org.ub.utilbot.commandutils.CommandContext;
import org.ub.utilbot.commandutils.CommandManager;
import org.ub.utilbot.commandutils.MessageReceivedListener;
import org.ub.utilbot.entities.User;
import org.ub.utilbot.repositories.UserRepository;

import java.util.Arrays;

@Component
@Profile("!test")
public class Bot extends ListenerAdapter implements CommandLineRunner {
    @Value("${app.jda.token}")
    private String token;

    private JDA client;

    private final Logger log = LogManager.getLogger(Bot.class);

    @Override
    public void run(String... args) throws Exception {

        client = JDABuilder.createDefault(token).build();

        client.addEventListener(new MessageReceivedListener());


        log.info("Bot started.");

        CommandManager.registerCommand(new ExampleCommand());

    }

    public JDA getClient() {
        return this.client;
    }
    /*
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        log.info("Received message: " + e.getMessage().getContentRaw());
        String prefix = CommandManager.getPrefix();

        if (!e.getMessage().getContentRaw().startsWith(prefix)) {
            return;
        }

        String[] messageContent = e.getMessage().getContentStripped().replace(prefix,"").split("");

        String command = messageContent[0];
        String[] args = {};
        if (messageContent.length > 1) {
            args = Arrays.copyOfRange(messageContent, 1, messageContent.length - 1);
        }

        CommandContext context = new CommandContext(e.getMember(), e.getChannel(), e.getMessage(), command, args);

        CommandManager.runCommand(context);
    }*/

}
