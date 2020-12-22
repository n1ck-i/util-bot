package org.ub.utilbot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;
import org.ub.utilbot.entities.Professor;
import org.ub.utilbot.repositories.ProfessorRepository;

import java.util.Arrays;

@Component
public class RepositoryAccess implements Command {
    @Autowired
    private ProfessorRepository profRepository;

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
                break;
            case "addMeeting":
                break;
        }

    }

    private void addProf(CommandContext context) {
        Professor prof = new Professor();
        prof.setName(context.getArgs()[1]);
        prof.setSubject(context.getArgs()[2]);
        prof.setChannelId(context.getArgs()[3]);

        prof = profRepository.save(prof);
        log.info("Added professor to repository: " + prof.toString());
    }
}
