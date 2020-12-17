package org.ub.utilbot.commandutils;

public interface Command {

    /**
     * @return The name of the command
     */
    public String getName();

    /**
     * @return The description of the command
     */
    public String getDescription();

    /**
     *
     * @return The usage of the command
     */
    public String getUsage();

    /**
     *
     * @param context The context for the command
     */
    public void onCommand(CommandContext context);

    /**
     *
     * @param command   The command to compare to
     * @return          Whether the commands have the same name
     */
    public default boolean equals(Command command) {
        return this.getName().equalsIgnoreCase(command.getName());
    }
}
