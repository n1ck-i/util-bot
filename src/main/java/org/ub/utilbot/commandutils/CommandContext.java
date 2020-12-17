package org.ub.utilbot.commandutils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class CommandContext {
    // The Member that ran the command
    private Member member;

    // The TextChannel in which the command was run
    private MessageChannel channel;

    // The Message that was sent containing the command
    private Message message;

    // The label used to call the command
    private String label;

    // The args passed with the command
    private String[] args;

    /**
     *
     * @param member    The Member that ran the command
     * @param channel   The TextChannel the command was ran in
     * @param message   The Message that was sent containing the command
     * @param label     The label used to call the command
     * @param args      The args passed with the command
     */
    public CommandContext(Member member, MessageChannel channel, Message message, String label, String[] args) {
        this.member = member;
        this.channel = channel;
        this.message = message;
        this.label = label;
        this.args = args;
    }

    /**
     *
     * @return  The Member that ran the command
     */
    public Member getMember() {
        return member;
    }

    /**
     *
     * @return  The channel that the command was called in
     */
    public MessageChannel getChannel() {
        return channel;
    }

    /**
     *
     * @return  The Message that the command was called with
     */
    public Message getMessage() {
        return message;
    }

    /**
     *
     * @return  The label used to call the command
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     * @return  The arguments passed to the command
     */
    public String[] getArgs() {
        return args;
    }
}
