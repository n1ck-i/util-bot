package org.ub.utilbot.commands;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;
import org.ub.utilbot.entities.Meeting;
import org.ub.utilbot.entities.Professor;
import org.ub.utilbot.entities.Tutor;
import org.ub.utilbot.repositories.MeetingRepository;
import org.ub.utilbot.repositories.ProfessorRepository;
import org.ub.utilbot.repositories.TutorRepository;

@Component
public class RepositoryAccess implements Command {
    @Value("${app.jda.repoEditRole}")
    private String repoEdit;

    private Role repoRole = null;

    @Autowired
    private ProfessorRepository profRepository;

    @Autowired
    private MeetingRepository meetRepository;

    @Autowired
    private TutorRepository tutRepository;

    private final Logger log = LogManager.getLogger(RepositoryAccess.class);

    @Override
    public String getName() {
        return "repo";
    }

    @Override
    public String getDescription() {
        return "Used to debug the repository";
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public void onCommand(CommandContext context) {
        if (repoRole == null) {
            repoRole = context.getMember().getJDA().getRoleById(Long.parseLong(repoEdit));
        }
        if (!context.getMember().hasPermission(Permission.ADMINISTRATOR)
            || !context.getMember().getRoles().contains(repoRole)) {
            return;
        }
        switch (context.getArgs()[0]) {
            case "addProf":
                addProf(context);
                break;
            case "addTutor":
                addTutor(context);
                break;
            case "addLecture":
                addLecture(context);
                break;
            case "addTutoring":
                addTutoring(context);
                break;
            case "getProfs":
                getProfs(context);
                break;
            case "getMeetings":
                getMeetings(context);
                break;
            case "getTutors":
                getTutors(context);
                break;
        }

    }

    private void getTutors(CommandContext context) {
        Iterable<Tutor> tuts = tutRepository.findAll();
        List<Tutor> tutList = new ArrayList<>();

        tuts.forEach(tutList::add);
        String tutString = "";
        for (Tutor tut: tutList) {
            tutString += tut.getName() + " | " + tut.getId() + "\n";
        }
        // Splits the message into sendable chunks in case it is too long
        for (String s : splitMessage(tutString)) {
            context.getChannel().sendMessage(s).queue();
        }

    }

    private void getProfs(CommandContext context) {
        Iterable<Professor> profs = profRepository.findAll();
        List<Professor> profList = new ArrayList<>();

        profs.forEach(profList::add);

        String profString = "";
        for (Professor prof: profList) {
            profString += prof.getName() + " : " + prof.getSubject() + " - <#" + prof.getChannelId() + "> | " + prof.getId() + "\n";
        }
        // Splits the message into sendable chunks in case it is too long
        for (String s : splitMessage(profString)) {
            context.getChannel().sendMessage(s).queue();
        }

    }

    private void getMeetings(CommandContext context) {
        String[] days = {"Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Iterable<Meeting> meets = meetRepository.findAll();
        List<Meeting> meetList = new ArrayList<>();

        meets.forEach(meetList::add);

        String meetString = "";
        for (Meeting meet: meetList) {
            meetString += "**" + days[meet.getWeekday()] + "** at **" + meet.getStartTime() + "** | **Prof**: " + profRepository.findById(meet.getRefProfId());
            if (meet.getRefTutorId() != null) {
                meetString += " | **Tutor**: " + tutRepository.findById(meet.getRefTutorId());
            }
            meetString += "\n";
        }


        // Splits the message into sendable chunks in case it is too long
        for (String s : splitMessage(meetString)) {
            System.out.println(s.length());
            context.getChannel().sendMessage(s).queue();
        }
    }

    private void addProf(CommandContext context) {
        Professor prof = new Professor();
        prof.setName(context.getArgs()[1]);
        prof.setSubject(context.getArgs()[2]);
        prof.setChannelId(context.getArgs()[3]);

        prof = profRepository.save(prof);
        log.info("Added professor to repository: " + prof.toString());
        context.getChannel().sendMessage("Added professor to repository: " + prof.toString()).queue();
    }


    private void addLecture(CommandContext context) {
        Meeting meeting = new Meeting();

        meeting.setRefProfId(context.getArgs()[1]);

        DateFormat format = new SimpleDateFormat("kk:mm", Locale.GERMAN);
        Date d;
        try {
            d = format.parse(context.getArgs()[2]);

        }
        catch (java.text.ParseException e){
            log.error(e.toString());
            return;
        }
        Time t =  new Time(d.getTime());
        meeting.setStartTime(t);
        meeting.setWeekday(Integer.parseInt(context.getArgs()[3]));
        meeting.setLink(context.getArgs()[4]);

        meeting = meetRepository.save(meeting);
        log.info("Added lecture to repository: " + meeting.toString());
        context.getChannel().sendMessage("Added lecture to repository: " + meeting.toString()).queue();
    }

    private void addTutoring(CommandContext context) {
        Meeting meeting = new Meeting();

        meeting.setRefProfId(context.getArgs()[1]);
        meeting.setRefTutorId(context.getArgs()[2]);
        DateFormat format = new SimpleDateFormat("kk:mm", Locale.GERMAN);

        Date d;
        try {
            d = format.parse(context.getArgs()[3]);

        }
        catch (java.text.ParseException e){
            log.error(e.toString());
            return;
        }
        Time t =  new Time(d.getTime());
        meeting.setStartTime(t);
        meeting.setWeekday(Integer.parseInt(context.getArgs()[4]));
        meeting.setGroupNumber(Integer.parseInt(context.getArgs()[5]));
        meeting.setLink(context.getArgs()[6]);

        meeting = meetRepository.save(meeting);
        log.info("Added tutoring to repository: " + meeting.toString());
        context.getChannel().sendMessage("Added tutoring to repository: " + meeting.toString()).queue();
    }

    private void addTutor(CommandContext context) {
        Tutor tut = new Tutor();
        tut.setName(String.join(" ", Arrays.copyOfRange(context.getArgs(),1,context.getArgs().length)));

        tut = tutRepository.save(tut);
        log.info("Added Tutor: " + tut.toString());
        context.getChannel().sendMessage("Added Tutor: " + tut.toString()).queue();

    }

    private List<String> splitMessage(String message) {
        List<String> messageSplits = new ArrayList<>();
        if (message.length() > 2000) {
            String[] splitMessage = message.split("\n");
            String tempSplit = "";
            for (int i = 0; i < splitMessage.length; i++) {
                tempSplit += splitMessage[i] + "\n";
                if (i != 0 && i % 5 == 0) {
                    messageSplits.add(tempSplit);
                    tempSplit = "";
                }
            }
            messageSplits.add(tempSplit);
        } else {
            messageSplits.add(message);
        }
        // Filters out empty strings
        messageSplits = messageSplits.stream().filter(s -> !s.equals("")).collect(Collectors.toList());
        return messageSplits;
    }
}
