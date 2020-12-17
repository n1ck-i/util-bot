package org.ub.utilbot.commands;

import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;

public class ExampleCommand implements Command {
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
        context.getChannel().sendMessage("Wooooo es testet!").queue();
    }
}
