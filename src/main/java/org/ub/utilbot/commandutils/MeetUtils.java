package org.ub.utilbot.commandutils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commands.RequestMeeting;
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
public class MeetUtils {
    @Autowired
    private ProfessorRepository profRepository;
    @Autowired
    private MeetingRepository meetRepository;
    @Autowired
    private TutorRepository tutRepository;

    private final Logger log = LogManager.getLogger(MeetUtils.class);

    public List<Meeting> getMeetings(String subject, String identifier, int day) {
        // Grab a list of professors and of IDs
        List<Professor> subjectProfs = (List<Professor>) profRepository.findBySubject(subject);
        List<String> subjectProfsIDs = subjectProfs.stream()
                .map(Professor::getId)
                .collect(Collectors.toList());

        // Filter the meetingList by which meeting's professor's ID is on the list of subjectProfs IDs
        List<Meeting> meetings = ((List<Meeting>)meetRepository.findAll()).stream()
                .filter(m -> subjectProfsIDs.contains(m.getRefProfId()))
                .collect(Collectors.toList());

        // If there's no identifier, then it returns the subject's lectures
        if (identifier.equals("")) {
            meetings = meetings.stream()
                    .filter(m -> m.getRefTutorId() == null)
                    .collect(Collectors.toList());
        }

        // Lists all of today's lectures and tutorings for the selected subject
        else if (identifier.equalsIgnoreCase("list")) {
            meetings = meetings.stream().filter(m -> m.getWeekday() == day).collect(Collectors.toList());

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
                return null;
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
            meetings = meetings.stream()
                    .filter(m -> m.getStartTime().before(upperLimit) && m.getStartTime().after(lowerLimit))
                    .collect(Collectors.toList());


        }

        // If the identifier is a number, then it is a group number
        else if(isNum(identifier)) {
            int groupNum = Integer.parseInt(identifier);

            // Filter the meetings by the group number. Lectures have the group number 0
            meetings = meetings.stream()
                    .filter(m -> m.getGroupNumber() == groupNum)
                    .collect(Collectors.toList());
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
                meetings = meetings.stream()
                        .filter(m -> m.getGroupNumber()>0)
                        .filter(m -> tutRepository.findById(m.getRefTutorId()).getName().contains(identifier))
                        .collect(Collectors.toList());

            }

            // If it doesn't, then it returns an error
            else {
                meetings = null;
            }
        }

        return meetings;
    }

    public static boolean isNum(String n) {
        try {
            double d = Double.parseDouble(n);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public String meetResponse(List<Meeting> meetings) {

        String[] days = {"Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String response = "";

        if(meetings.size() == 0) {
            response = "I could not find any meetings for the given query.";
            return response;
        }

        // Sort the meetings by their start time.
        meetings.sort(Comparator.comparing(Meeting::getStartTime));

        // Iterate over all meetings and append a String to the response depending on it being a Tutoring or not
        // This is decided by whether or not there is a RefTutorID in the database
        for (Meeting m: meetings) {

            String profName = profRepository.findById(m.getRefProfId()).getName();
            if (m.getRefTutorId() == null) {

                response += "**__Lecture__**\n" + profName + " : " + m.getStartTime().toString() + " - " + days[m.getWeekday()] + "\n<" + m.getLink() + ">\n\n";
            }
            else {

                // Find the tutor for this tutoring
                String tutName = tutRepository.findById(m.getRefTutorId()).getName();
                response += "**__Tutoring__**\n" + tutName + " - " + profName + " : " + m.getStartTime().toString() + " - " + days[m.getWeekday()] + "\n" + "Group-ID: " + String.valueOf(m.getGroupNumber()) + "\n<" + m.getLink() + ">\n\n";
            }
        }

        return response;
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
