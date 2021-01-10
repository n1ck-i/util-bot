package org.ub.utilbot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;
import org.ub.utilbot.commandutils.CommandManager;

@Component
public class ExampleCommand implements Command {
    private static final Logger log = LogManager.getLogger(CommandManager.class);
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getDescription() {
        return "This is a test command";
    }

    @Override
    public String getUsage() {
        return "test <Benoetigter arg> [Optionaler arg]";
    }

    @Override
    public void onCommand(CommandContext context) {
        context.getChannel().sendMessage("AAAAAAAAAAAAAAAAAAAA").queue();
        log.info("A");
    }
}
