package org.ub.utilbot.commandutils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.ub.utilbot.Bot;

import java.util.Arrays;

@Component
public class MessageReceivedListener extends ListenerAdapter {
    private final Logger log = LogManager.getLogger(MessageReceivedListener.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        log.info("Received message: " + e.getMessage().getContentRaw());
        String prefix = CommandManager.getPrefix();

        if (!e.getMessage().getContentRaw().startsWith(prefix)) {
            return;
        }
        String[] messageContent = e.getMessage().getContentStripped().replace(prefix, "").split(" ");

        String command = messageContent[0];
        String[] args = {};
        if (messageContent.length > 1) {
            args = Arrays.copyOfRange(messageContent, 1, messageContent.length - 1);
        }

        CommandContext context = new CommandContext(e.getMember(), e.getChannel(), e.getMessage(), command, args);

        CommandManager.runCommand(context);
    }
}
