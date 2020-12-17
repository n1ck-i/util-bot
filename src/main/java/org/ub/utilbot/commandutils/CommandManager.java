package org.ub.utilbot.commandutils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private static final Logger log = LogManager.getLogger(CommandManager.class);
    private static final String PREFIX = "!";

    private static Map<String,Command> commands = new HashMap<>();


    public static void runCommand(CommandContext context) {
        Command commandToRun = null;

        commandToRun = commands.get(context.getLabel().toLowerCase());


        if (commandToRun == null) {
            log.warn("Command was not found: " + context.getLabel());
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
        return commands.containsKey(command.getName().toLowerCase());
    }

    /**
     * Registers the command
     * @param command   The command to register
     */
    public static void registerCommand(Command command) {
        if (!commandExists(command)) {
            commands.put(command.getName().toLowerCase(), command);
            log.info("Registered command: " + command.getName().toLowerCase());
        }
    }

    /**
     * Tries to unregister a command that is equal according to the given .equals() method.
     * @param command   The command to unregister
     */
    /*
    public static void unregisterCommand(Command command) {
        if (commandExists(command)) {
            for(int i = 0; i < commands.size(); i++) {
                if (commands.get(i).equals(command)) {
                    commands.remove(i);
                    return;
                }
            }
        }
    }*/

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
    public static Map<String, Command> getCommands() {
        return commands;
    }

}
