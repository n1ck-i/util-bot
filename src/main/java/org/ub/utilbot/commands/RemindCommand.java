package org.ub.utilbot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;
import org.ub.utilbot.commandutils.MeetUtils;
import org.ub.utilbot.entities.Meeting;
import org.ub.utilbot.entities.Professor;
import org.ub.utilbot.repositories.MeetingRepository;
import org.ub.utilbot.repositories.ProfessorRepository;
import org.ub.utilbot.repositories.TutorRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RemindCommand implements Command{
    @Autowired
    private ProfessorRepository profRepository;

    @Autowired
    private MeetingRepository meetRepository;

    @Autowired
    private TutorRepository tutRepository;

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
        // Checks if there are arguments present
        if (context.getArgs().length == 0) {
            context.getChannel().sendMessage("I need arguments to figure out which meetings you want information for.\nThe usage is as follows: `!meeting [subject] [identifier]`").queue();
            return;
        }

        // Checks if the first argument is a valid subject
        List<String> types = getTypes();
        if (!types.contains(context.getArgs()[0])) {
            context.getChannel().sendMessage("Please give me the subject you want me to get the lectures for first. It should be one of these: " + types.toString()).queue();
            return;
        }

        // Subject is the first argument and the rest is concatenated to be the identifier
        String subject = context.getArgs()[0];

        String identifier = String.join(" ", Arrays.copyOfRange(context.getArgs(),1,context.getArgs().length));

        int day = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2) % 7;
        MeetUtils util = new MeetUtils(profRepository,meetRepository,tutRepository);
        List<Meeting> meetings = util.getMeetings(subject,identifier,day);

        if (meetings.size() == 0) {
            //TODO Send message that there was no meeting fitting the query
            String response = "I could not find any fitting meetings for your response. Maybe try using group numbers for tutorings or just the subject alone for lectures.";
            context.getChannel().sendMessage(response).queue();
        }
        else if (meetings.size() == 1) {
            //TODO Sign up and send message
            String response = meetings.get(0).toString();

        }
        // If all meetings are lectures are lectures
        else if (meetings.stream().allMatch(m -> m.getRefTutorId() == null)) {
            //TODO Sign up and send message
        }
        // It's not exact enough. Return the list and instructions on how to make it more exact
        else {
            String response = "The request isn't exact enough. There are several meetings that could be meant. Please use the group number as an identifier for a tutoring or leave the identifier blank to sign up for the lecture.\n" + util.meetResponse(meetings);
            context.getChannel().sendMessage(response).queue();
        }
    }

    public List<String> getTypes() {
        List<String> types = new ArrayList<>();
        for (Professor prof: profRepository.findAll()) {
            if (!types.contains(prof.getSubject())) {
                types.add(prof.getSubject());
            }
        }
        return types;
    }
}
