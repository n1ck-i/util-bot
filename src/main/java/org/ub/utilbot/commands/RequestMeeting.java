package org.ub.utilbot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;
import org.ub.utilbot.repositories.UserRepository;

public class RequestMeeting implements Command {
    private final Logger log = LogManager.getLogger(RequestMeeting.class);

    @Autowired
    private UserRepository userRepo;

    @Override
    public String getName() {
        return "meeting";
    }

    @Override
    public String getDescription() {
        return "Returns information regarding the different lectures and meetings.";
    }

    @Override
    public String getUsage() {
        return "!meeting [type] [identifier]";
    }

    @Override
    public void onCommand(CommandContext context) {

    }
}
