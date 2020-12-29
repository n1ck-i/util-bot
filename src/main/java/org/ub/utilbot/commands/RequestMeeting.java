package org.ub.utilbot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;
import org.ub.utilbot.entities.Meeting;
import org.ub.utilbot.entities.Professor;
import org.ub.utilbot.entities.Tutor;
import org.ub.utilbot.repositories.MeetingRepository;
import org.ub.utilbot.repositories.ProfessorRepository;
import org.ub.utilbot.repositories.TutorRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RequestMeeting implements Command {
    @Autowired
    private ProfessorRepository profRepository;

    @Autowired
    private MeetingRepository meetRepository;

    @Autowired
    private TutorRepository tutRepository;

    private final Logger log = LogManager.getLogger(RequestMeeting.class);

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
        /*
        String[] input1 = {"ti","list"};
        String[] input2 = {"ti"};
        String[] input3 = {"list"};

        String[] input = input1;
        */

        log.info(Arrays.toString(context.getArgs()));
        List<String> types = getTypes();
        if (!types.contains(context.getArgs()[0])) {
            context.getChannel().sendMessage("Please give me the subject you want me to get the lectures for first. It should be one of these: " + types.toString()).queue();
            return;
        }

        String subject = context.getArgs()[0];
        //String type = context.getArgs()[0];

        String identifier = String.join(" ", Arrays.copyOfRange(context.getArgs(),1,context.getArgs().length));

        log.info(subject + "..." + identifier + ".");

        // Lists all of today's lectures and tutorings for the selected subject
        if (identifier.equals("list") || identifier.equals("")) {
            List<Professor> subjectProfs = (List<Professor>) profRepository.findBySubject(subject);
            List<String> subjectProfsIDs = subjectProfs.stream().map(Professor::getId).collect(Collectors.toList());

            int day = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2) % 7;
            List<Meeting> meetings = (List<Meeting>) meetRepository.findByWeekday(day);
            meetings = meetings.stream().filter(m -> subjectProfsIDs.contains(m.getRefProfId())).collect(Collectors.toList());

            log.info(meetings.size());
            String response = "";
            if (meetings.size() > 0) {
                meetings.sort(Comparator.comparing(Meeting::getStartTime));

                for (Meeting m: meetings) {
                    if (m.getRefTutorId() == null) {
                        String profName = subjectProfs.stream()
                                .filter(p -> p.getId().equals(m.getRefProfId()))
                                .collect(Collectors.toList())
                                .get(0)
                                .getName();
                        response += "**__Lecture__**\n" + profName + " : " + m.getStartTime().toString() + "\n<" + m.getLink() + ">\n\n";
                    }
                    else {
                        String profName = subjectProfs.stream()
                                .filter(p -> p.getId().equals(m.getRefProfId()))
                                .collect(Collectors.toList())
                                .get(0)
                                .getName();
                        String tutName = tutRepository.findById(m.getRefTutorId()).getName();
                        response += "**__Tutoring__**\n" + tutName + " - " + profName + " : " + m.getStartTime().toString() + "\n" + "Group-ID: " + String.valueOf(m.getGroupNumber()) + "\n<" + m.getLink() + ">\n\n";
                    }
                }
            }
            else {
                response = "There are no lectures or tutorings for this subject today.";
            }
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
