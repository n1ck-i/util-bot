package org.ub.utilbot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;
import org.ub.utilbot.entities.Meeting;
import org.ub.utilbot.entities.User;
import org.ub.utilbot.entities.UserToMeeting;
import org.ub.utilbot.repositories.MeetingRepository;
import org.ub.utilbot.repositories.ProfessorRepository;
import org.ub.utilbot.repositories.UserRepository;
import org.ub.utilbot.repositories.UserToMeetingRepository;

import java.util.List;

@Component
public class RemoveCommand implements Command {
    String[] days = {"Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    private final Logger log = LogManager.getLogger(RemoveCommand.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserToMeetingRepository utoRepository;

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Removes the reminder to a reminder that was created prior to it using an ID that is given when the reminder is created.";
    }

    @Override
    public String getUsage() {
        return "!remove <ReminderID>";
    }

    @Override
    public void onCommand(CommandContext context) {

        boolean exists = ((List<UserToMeeting>)utoRepository.findAll()).stream()
                .anyMatch(uto -> uto.getId().equals(context.getArgs()[0]));

        if (exists) {
            UserToMeeting utoElement = utoRepository.findById(context.getArgs()[0]);
            User user = userRepository.findByDiscordId(context.getMember().getId());
            // Checks if the reminder is by the given user
            if (user != null &&user.getId().equals(utoElement.getRefUserId())) {

                Meeting meeting = meetingRepository.findById(utoRepository.findById(context.getArgs()[0]).getRefMeetingId());
                String subject = professorRepository.findById(meeting.getRefProfId()).getSubject();
                utoRepository.delete(utoElement);
                String response = "I removed your reminder for " + subject + " at " + meeting.getStartTime() + " on " + days[meeting.getWeekday()] + ".";

                context.getChannel().sendMessage(response).queue();

                return;
            }
        }

        context.getChannel().sendMessage("I could not find a responding reminder for the given ID.").queue();
    }
}
