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
import org.ub.utilbot.repositories.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListRemindersCommand implements Command {
    private final Logger log = LogManager.getLogger(RequestMeeting.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserToMeetingRepository utmRepository;

    @Autowired
    private ProfessorRepository profRepository;

    @Autowired
    private MeetingRepository meetRepository;

    @Autowired
    private TutorRepository tutRepository;

    String[] days = {"Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Override
    public String getName() {
        return "reminders";
    }

    @Override
    public String getDescription() {
        return "Lists your reminders with their respective IDs";
    }

    @Override
    public String getUsage() {
        return "!reminders";
    }

    @Override
    public void onCommand(CommandContext context) {
        User user = userRepository.findByDiscordId(context.getMessage().getAuthor().getId());
        if (user == null) {
            context.getChannel().sendMessage("You have no reminders at this point.").queue();
            return;
        }

        List<UserToMeeting> utos = ((List<UserToMeeting>)utmRepository.findAll()).stream()
                .filter(uto -> uto.getRefUserId().equals(userRepository.findByDiscordId(user.getDiscordId()).getId()))
                .collect(Collectors.toList());

        if (utos.size() == 0) {
            context.getChannel().sendMessage("You have no reminders at this point.").queue();
            return;
        }
        String response = "";
        for (UserToMeeting uto: utos) {
            Meeting meeting = meetRepository.findById(uto.getRefMeetingId());

            String profName = profRepository.findById(meeting.getRefProfId()).getName();

            if (meeting.getRefTutorId() == null) {

                response += "**__Lecture__**\n" + profName + " : " + meeting.getStartTime().toString()
                        + " - " + days[meeting.getWeekday()] + "\n<" + meeting.getLink() + ">\n";
            }
            else {

                // Find the tutor for this tutoring
                String tutName = tutRepository.findById(meeting.getRefTutorId()).getName();
                response += "**__Exercise__**\n" + tutName + " - " + profName + " : " +
                        meeting.getStartTime().toString()+ " - " + days[meeting.getWeekday()] + "\n" + "Group-ID: " +
                        String.valueOf(meeting.getGroupNumber()) + "\n<" + meeting.getLink() + ">\n";
            }
            response+= "Remove this reminder with `!remove "+ uto.getId() +"`\n\n";
        }
        context.getChannel().sendMessage(response).queue();
    }
}
