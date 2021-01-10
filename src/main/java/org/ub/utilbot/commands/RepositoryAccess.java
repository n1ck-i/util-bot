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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class RepositoryAccess implements Command {
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
        log.info(Arrays.toString(context.getArgs()));
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
                List<Professor> profs = getProfs();
                String profString = "";
                for (Professor prof: profs) {
                    profString += prof.getName() + " : " + prof.getSubject() + " - <#" + prof.getChannelId() + ">\n";
                }
                context.getChannel().sendMessage(profString).queue();
                break;
        }

    }

    private List<Professor> getProfs() {
        Iterable<Professor> profs = profRepository.findAll();
        List<Professor> profList = new ArrayList<>();

        profs.forEach(profList::add);
        return profList;

    }

    private void addProf(CommandContext context) {
        Professor prof = new Professor();
        prof.setName(context.getArgs()[1]);
        prof.setSubject(context.getArgs()[2]);
        prof.setChannelId(context.getArgs()[3]);

        prof = profRepository.save(prof);
        log.info("Added professor to repository: " + prof.toString());
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
    }

    private void addTutor(CommandContext context) {
        Tutor tut = new Tutor();
        tut.setName(String.join(" ", Arrays.copyOfRange(context.getArgs(),1,context.getArgs().length)));

        tut = tutRepository.save(tut);
        log.info("Added Tutor: " + tut.toString());

    }
}
