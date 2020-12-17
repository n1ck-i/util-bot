package org.ub.utilbot.commandutils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ub.utilbot.Bot;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private static final Logger log = LogManager.getLogger(CommandManager.class);
    private static final String PREFIX = "!";

    private static List<Command> commands = new ArrayList<>();


    public static void runCommand(CommandContext context) {
        Command commandToRun = null;

        for (Command command: commands) {
            if (context.getLabel().equalsIgnoreCase(command.getName())) {
                commandToRun = command;
            }
        }

        if (commandToRun == null) {
            return;
        }

        log.info("Ran command: " + commandToRun.getName());
        commandToRun.onCommand(context);
    }

    /**
     *
     * @param command   Command to check for a duplicate
     * @return          Whether or not the given command is already registered
     */
    private static boolean commandExists(Command command) {
        for (Command existingCommand: commands) {
            if (command.equals(existingCommand)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Registers the command
     * @param command   The command to register
     */
    public static void registerCommand(Command command) {
        if (!commandExists(command)) {
            commands.add(command);
            log.info("Registered command: " + command.getName());
        }
    }

    /**
     * Tries to unregister a command that is equal according to the given .equals() method.
     * @param command   The command to unregister
     */
    public static void unregisterCommand(Command command) {
        if (commandExists(command)) {
            for(int i = 0; i < commands.size(); i++) {
                if (commands.get(i).equals(command)) {
                    commands.remove(i);
                    return;
                }
            }
        }
    }

    /**
     *
     * @return  The prefix for the bot
     */
    public static String getPrefix() {
        return PREFIX;
    }

    /**
     *
     * @return  A list of all registered commands
     */
    public static List<Command> getCommands() {
        return commands;
    }
}
