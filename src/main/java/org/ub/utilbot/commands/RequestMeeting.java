package org.ub.utilbot.commands;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;
import org.ub.utilbot.commandutils.MeetUtils;

@Component
public class RequestMeeting implements Command, ApplicationContextAware {

    private final Logger log = LogManager.getLogger(RequestMeeting.class);

    private ApplicationContext appContext;

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
        return "!meeting [subject] [identifier]";
    }

    @Override
    public void onCommand(CommandContext context) {
        MeetUtils util = appContext.getBean(MeetUtils.class);

        log.info("Meeting requested by user(" + context.getMember().getId() + ") with args: " + String.join(",", context.getArgs()));

        // Checks if there are arguments present
        if (context.getArgs().length == 0) {
            log.info("No argument supplied");
            context.getChannel().sendMessage("I need arguments to figure out which meetings you want information for.\nThe usage is as follows: `!meeting [subject] [identifier]`").queue();
            return;
        }

        // Checks if the first argument is a valid subject
        List<String> types = util.getTypes();
        if (!types.contains(context.getArgs()[0])) {
            log.info("Invalid subject supplied");
            context.getChannel().sendMessage("Please give me the subject you want me to get the lectures for first. It should be one of these: " + types.toString()).queue();
            return;
        }

        // Subject is the first argument and the rest is concatenated to be the identifier
        String subject = context.getArgs()[0];

        String identifier = String.join(" ", Arrays.copyOfRange(context.getArgs(),1,context.getArgs().length));

        int day = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) % 7;
        //new MeetUtils(profRepository,meetRepository,tutRepository);
        context.getChannel().sendMessage(util.meetResponse(util.getMeetings(subject,identifier,day))).queue();

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }
}
