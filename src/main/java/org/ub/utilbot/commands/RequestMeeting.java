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

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        return "!meeting [subject] [identifier]";
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

        // Lists all of today's lectures and tutorings for the selected subject
        if (identifier.equals("list") || identifier.equals("")) {

            List<Meeting> meetings = (List<Meeting>) meetRepository.findByWeekday(day);

            String response = "";
            if (meetings.size() > 0) {

                context.getChannel().sendMessage(meetResponse(meetings, subject)).queue();
            }
            else {

                response = "There are no lectures or tutorings for this subject today.";
                context.getChannel().sendMessage(response).queue();
            }
        }

        // If the user wants to find a meeting by the starttime
        else if (identifier.contains(":")) {
            // Convert the timestamp into a Calendar object
            DateFormat format = new SimpleDateFormat("kk:mm", Locale.GERMAN);
            Date d;
            try {
                d = format.parse(identifier);
            }catch (java.text.ParseException e){
                log.error(e.toString());
                return;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);

            // Get the upper and lower limit for a span of 32 minutes around the given timestamp
            cal.add(Calendar.MINUTE, 16);
            Time upperLimit = new Time(cal.getTimeInMillis());
            cal.add(Calendar.MINUTE, -32);
            Time lowerLimit = new Time(cal.getTimeInMillis());
            log.info( "" + upperLimit + lowerLimit);

            // Filter the meetings to be on the same day and within 16 minutes of the given timestamp
            List<Meeting> meetings = ((List<Meeting>)meetRepository.findByWeekday(day)).stream()
                    .filter(m -> m.getStartTime().before(upperLimit) && m.getStartTime().after(lowerLimit))
                    .collect(Collectors.toList());


            // Send the response
            context.getChannel().sendMessage(meetResponse(meetings, subject)).queue();
        }

        // If the identifier is a number, then it is a group number
        else if(isNum(identifier)) {
            int groupNum = Integer.parseInt(identifier);

            // Filter the meetings by the group number. Lectures have the group number 0
            List<Meeting> meetings = ((List<Meeting>) meetRepository.findByWeekday(day)).stream()
                    .filter(m -> m.getGroupNumber() == groupNum)
                    .collect(Collectors.toList());

            context.getChannel().sendMessage(meetResponse(meetings, subject)).queue();
        }

        // Else it'll have to be a tutor's name
        else {
            List<String> tutorNames = ((List<Tutor>) tutRepository.findAll()).stream()
                    .map(Tutor::getName)
                    .collect(Collectors.toList());

            // Checks if a valid tutor even exists
            boolean validTutor = false;
            for (String t: tutorNames) {
                if (t.contains(identifier)) {
                    validTutor = true;
                    break;
                }
            }

            // If it does, then it runs through the meetings and filters them
            if (validTutor) {

                // First filters everything out that has the group number 0, as those will not have a reftutorid
                List<Meeting> meetings = ((List<Meeting>) meetRepository.findByWeekday(day)).stream()
                        .filter(m -> m.getGroupNumber()>0)
                        .filter(m -> tutRepository.findById(m.getRefTutorId()).getName().contains(identifier))
                        .collect(Collectors.toList());

                context.getChannel().sendMessage(meetResponse(meetings, subject)).queue();
            }

            // If it doesn't, then it returns an error
            else {
                context.getChannel().sendMessage("I could not find anything with your arguments. Please try again with diferent ones.").queue();
            }
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

    public String meetResponse(Iterable<Meeting> meetings, String subject) {
        // Convert to list to allow streaming
        List<Meeting> meetingList = (List<Meeting>) meetings;

        // Grab a list of professors and of IDs
        List<Professor> subjectProfs = (List<Professor>) profRepository.findBySubject(subject);
        List<String> subjectProfsIDs = subjectProfs.stream()
                .map(Professor::getId)
                .collect(Collectors.toList());

        // Filter the meetingList by which meeting's professor's ID is on the list of subjectProfs IDs
        meetingList = meetingList.stream()
                .filter(m -> subjectProfsIDs.contains(m.getRefProfId()))
                .collect(Collectors.toList());
        String response = "";

        if(meetingList.size() == 0) {
            response = "I could not find any meetings for the given query.";
            return response;
        }

        // Sort the meetings by their start time.
        meetingList.sort(Comparator.comparing(Meeting::getStartTime));

        // Iterate over all meetings and append a String to the response depending on it being a Tutoring or not
        // This is decided by whether or not there is a RefTutorID in the database
        for (Meeting m: meetingList) {

            // In case there are several professors for the subject, select the first one
            String profName = subjectProfs.stream()
                    .filter(p -> p.getId().equals(m.getRefProfId()))
                    .collect(Collectors.toList())
                    .get(0)
                    .getName();
            if (m.getRefTutorId() == null) {

                response += "**__Lecture__**\n" + profName + " : " + m.getStartTime().toString() + "\n<" + m.getLink() + ">\n\n";
            }
            else {

                // Find the tutor for this tutoring
                String tutName = tutRepository.findById(m.getRefTutorId()).getName();
                response += "**__Tutoring__**\n" + tutName + " - " + profName + " : " + m.getStartTime().toString() + "\n" + "Group-ID: " + String.valueOf(m.getGroupNumber()) + "\n<" + m.getLink() + ">\n\n";
            }
        }

        return response;
    }

    public boolean isNum(String n) {
        if (n == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(n);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
