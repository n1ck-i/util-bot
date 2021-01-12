package org.ub.utilbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Time;
import java.time.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commands.RepositoryAccess;
import org.ub.utilbot.entities.Meeting;
import org.ub.utilbot.entities.Professor;
import org.ub.utilbot.entities.UserToMeeting;
import org.ub.utilbot.repositories.MeetingRepository;
import org.ub.utilbot.repositories.ProfessorRepository;
import org.ub.utilbot.repositories.UserRepository;
import org.ub.utilbot.repositories.UserToMeetingRepository;
import javax.security.auth.login.LoginException;
@Component

public class Reminder implements ApplicationContextAware {
    private final Logger log = LogManager.getLogger(Reminder.class);

    @Autowired
    private UserRepository urepository;

    @Autowired
    private UserToMeetingRepository utmRepository;

    @Autowired
    private MeetingRepository meetRepository;

    @Autowired
    private ProfessorRepository profRepository;

    private ApplicationContext appContext;

    public void remind() throws LoginException {
        //Get current day in int
        int day = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) % 7;
        log.info("This is the Day: " + day);


        Calendar cal = Calendar.getInstance();
        cal.setTime(Time.valueOf(LocalTime.now()));

        // Get the upper and lower limit for a span of 30 minutes around the given timestamp
        cal.add(Calendar.MINUTE, 0);
        Time lowerLimit = new Time(cal.getTimeInMillis());
        cal.add(Calendar.MINUTE, 30);
        Time upperLimit = new Time(cal.getTimeInMillis());
        log.info( "" + upperLimit + lowerLimit);
        log.info("starting to sort meetings");

        // Filter the meetings to be on the same day and within 30 minutes of the given timestamp
        List<Meeting> meetings = ((List<Meeting>)meetRepository.findByWeekday(day)).stream()
                .filter(m -> m.getStartTime().before(upperLimit) && m.getStartTime().after(lowerLimit))
                .collect(Collectors.toList());
        log.info("Finished sorting Meetings" + meetings.size());

        //Check if meetings exist
        if(meetings.size() == 0){
            log.warn("No Meetings found.");
            return;
        }

        //get bot class
        JDA jda = this.appContext.getBean(Bot.class).getClient();

        //Find relevent User Discord IDs and send messages in for loop (iterating over the relevent meetings)
        for(Meeting m : meetings) {

            //Filter the lectures and send message in Lecture Channel
            log.info("Searching for current lectures");
            if(m.getGroupNumber() == 0){
                String link = m.getLink();
                String message = "The lecture will begin soon!\n" +  "Here is the Link: " + link;
                String channelId = "";
                for ( Professor prof : profRepository.findAll()){
                    if (prof.getId() == m.getRefProfId()){
                        channelId = prof.getChannelId();
                        break;
                    }
                }
                log.info("Sending reminder in Lecture Channel");
                //MessageChannel channel = jda.getGuildByID(760421261649248296l).getTextChannelById(channelId); // Channel auf Info & IT-Sec Erstis Bonn
                MessageChannel channel = jda.getGuildById(786297622876651611l).getTextChannelById(798513607846920193l); // Testdaten für UbotBestBot
                channel.sendMessage(message).queue();
            }

            //Grab the uto database elements
            log.info("Grabbing user database elements");
            //grab the users
            List<User> users =
                    ((List<UserToMeeting>) utmRepository.findByRefMeetingId(m.getId())).stream()
                    .map(uts -> urepository.findById(uts.getRefUserId()))
                    .map(u -> Long.parseLong(u.getDiscordId()))
                    .map(nxu -> jda.retrieveUserById(nxu).complete()).collect(Collectors.toList());

            // Iterate through Users for the next step
            log.info("Starting to Iterate through Users. User size = " + users.size());
            for (User user : users) {
                // Content for the messages and declare variable message
                String link = m.getLink();
                String message;
                log.info("In for loop now");
                //check if meeting has refTutorID to filter the vorlesung and tutorium
                if(m.getRefTutorId() == null){
                    message = "@" + user.getName() + "" + "one of your lectures will begin soon!\n" + "Here is the Link: " + link;
                    log.info("Message made for " + user.getName());
                } else{
                    message = "@" + user.getName() + " one of your exercises will begin soon!\n" + "Here is the Link: " + link;
                    log.info("Message made for " + user.getName());
                }

                // Open DM with User and send message
                try {
                    user.openPrivateChannel().queue((channel) -> {channel.sendMessage(message).queue();});
                    log.info("Message sent to " + user.getName());
                } catch(Exception e) {
                    log.error("Bot could not send DM to User " + user.getName() + Arrays.toString(e.getStackTrace()));
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }
}
