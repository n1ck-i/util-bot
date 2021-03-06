package org.ub.utilbot.commands;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;
import org.ub.utilbot.commandutils.MeetUtils;
import org.ub.utilbot.entities.Meeting;
import org.ub.utilbot.entities.Professor;
import org.ub.utilbot.entities.User;
import org.ub.utilbot.entities.UserToMeeting;
import org.ub.utilbot.repositories.ProfessorRepository;
import org.ub.utilbot.repositories.UserRepository;
import org.ub.utilbot.repositories.UserToMeetingRepository;

import net.dv8tion.jda.api.entities.ChannelType;

@Component
public class RemindCommand implements Command, ApplicationContextAware {

    String[] days = {"Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    private final Logger log = LogManager.getLogger(RemindCommand.class);

    private ApplicationContext appContext;

    @Autowired
    private ProfessorRepository profRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserToMeetingRepository utmRepository;

    @Override
    public String getName() {
        return "remind";
    }

    @Override
    public String getDescription() {
        return "Get reminded for a lecture or tutoring. You will receive a message 30 minutes before it starts";
    }

    @Override
    public String getUsage() {
        return "!remind [subject] [identifier]";
    }

    @Override
    public void onCommand(CommandContext context) {
        MeetUtils util = appContext.getBean(MeetUtils.class);


        Professor prof = profRepository.findByChannelId(context.getChannel().getId());
        String identifier;
        // if the channel is the specific channel for the subject
        // the subject argument is inferred and doesn't need to be specified
        if (prof != null) {
            identifier = String.join(" ", context.getArgs());
        } else {
            // Checks if there are arguments present
            if (context.getArgs().length == 0) {
                context.getChannel().sendMessage("I need arguments to figure out which meetings you want information for.\n" +
                        "The usage is as follows: `!meeting [subject] [identifier]`").queue();
                return;
            }


            prof = profRepository.findBySubject(context.getArgs()[0]);
            // Checks if the first argument is a valid subject
            if (prof == null) {
                context.getChannel().sendMessage("Please give me the subject you want me to get the lectures for first." +
                        "It should be one of these: " + util.getTypes().toString()).queue();
                return;
            }

            // Subject is the first argument and the rest is concatenated to be the identifier
            identifier = String.join(" ", Arrays.copyOfRange(context.getArgs(),1,context.getArgs().length));
        }



        int day = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) % 7;
        List<Meeting> meetings = util.getMeetings(prof, identifier, day);

        if (meetings.size() == 0) {

            log.warn("No meetings found for, subject: " + prof.getSubject() + ", day: " + day + ", identifier: " + identifier);
            String response = "I could not find any fitting meetings for your response. Maybe try using group numbers for tutorings or just the subject alone for lectures.";
            this.sendResponseMessage(context, response);

        } else if (meetings.size() == 1) {
            Meeting m = meetings.get(0);

            User user = this.checkForUser(context.getMessage().getAuthor().getId());
            if (checkForExistingReminder(m.getId(),user.getId())) {
                context.getChannel().sendMessage("You are already being reminded for this meeting.").queue();
                return;
            }

            // Add UserToMeeting mapping to database
            UserToMeeting utm = new UserToMeeting();
            utm.setRefMeetingId(m.getId());
            utm.setRefUserId(user.getId());

            utm = utmRepository.save(utm);
            log.info("Added UserToMeeting instance: " + utm.toString());


            String response = "I signed you up to be reminded for " + prof.getSubject() + " at " + m.getStartTime() + " on "
                + days[m.getWeekday()] + ".\nTo remove this reminder, please run the command `!remove " + utm.getId() + "`";
            this.sendResponseMessage(context, response);


        } else if (meetings.stream().allMatch(m -> m.getRefTutorId() == null)) {
            // If all meetings are lectures

            User user = this.checkForUser(context.getMessage().getAuthor().getId());

            String response = "";
            for (Meeting meet: meetings) {
                if (checkForExistingReminder(meet.getId(),user.getId())) {
                    context.getChannel().sendMessage("You are already being reminded for this meeting.").queue();
                    continue;
                }
                UserToMeeting utm = new UserToMeeting();
                utm.setRefMeetingId(meet.getId());
                utm.setRefUserId(user.getId());

                utm = utmRepository.save(utm);
                log.info("Added UserToMeeting instance: " + utm.toString());
                response += "I signed you up to be reminded for " + prof.getSubject() + " at " + meet.getStartTime()
                    + " on " + days[meet.getWeekday()] + ".\nTo remove this reminder, please run the command `!remove " + utm.getId() + "`\n\n";
            }
            // response needs to be assigned to a new variable here
            // so that jda call will see that it is final from here on out
            String msg = response;
            this.sendResponseMessage(context, msg);

        } else {
            // It's not exact enough. Return the list and instructions on how to make it more exact
            String response = "The request isn't exact enough. There are several meetings that could be meant. " +
                "Please use the group number as an identifier for a tutoring or leave the identifier blank to sign up for the lecture.\n"
                + util.meetResponse(meetings);
            context.getChannel().sendMessage(response).queue();
        }
    }

    // Checks if the user is already in the database
    // otherwise saves him to the db
    private User checkForUser(String id) {
        User user = userRepository.findByDiscordId(id);

        // save user to db if he doesn't already exist
        if (user == null) {
            User u = new User();
            u.setDiscordId(id);

            u = userRepository.save(u);
            user = u;
            log.info("Added User to database: " + u.toString());
        }
        return user;
    }

    private boolean checkForExistingReminder(String meetID, String userID) {
        return ((List<UserToMeeting>) utmRepository.findAll()).stream()
                .anyMatch(utm -> utm.getRefUserId().equals(userID) && utm.getRefMeetingId().equals(meetID));
    }

    private void sendResponseMessage(CommandContext ctx, String msg) {
        // msgs can only be deleted by the bot in non private channels
        if (!ctx.getChannel().getType().equals(ChannelType.PRIVATE))
            ctx.getMessage().delete().complete();

        ctx.getMessage().getAuthor().openPrivateChannel()
            .queue(channel -> channel.sendMessage(msg).queue());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }
}
