package org.ub.utilbot.reminder;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.ub.utilbot.Bot;
import org.ub.utilbot.entities.Meeting;
import org.ub.utilbot.entities.UserToMeeting;
import org.ub.utilbot.repositories.MeetingRepository;
import org.ub.utilbot.repositories.UserRepository;
import org.ub.utilbot.repositories.UserToMeetingRepository;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import java.awt.Color;
@Component

public class Reminder implements ApplicationContextAware {
    private final Logger log = LogManager.getLogger(Reminder.class);

    @Autowired
    private UserRepository urepository;

    @Autowired
    private UserToMeetingRepository utmRepository;

    @Autowired
    private MeetingRepository meetRepository;

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
        log.info( "" + upperLimit + " - " + lowerLimit);
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

        JDA jda = this.appContext.getBean(Bot.class).getClient();

        //Find relevent User Discord IDs and send messages in for loop (iterating over the relevent meetings)
        for(Meeting m : meetings) {

            //Grab the utm database elements
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
                //declare variable messageEmbed
                String link = m.getLink();
                MessageEmbed messageEmbed;
                //check if meeting has refTutorID to filter the lectures from exercises
                if(m.getRefTutorId() == null){
                    //EmbedBuilder used to make Embed for Message
                    EmbedBuilder eb = new EmbedBuilder()
                            .setTitle("You have a reminder!", null)
                            .setDescription("@" + user.getName() + " one of your lectures will begin soon! Here is the Link: " + link);
                            eb.setColor(new Color(63,196,224));
                    messageEmbed = eb.build();
                    log.info("Message made for " + user.getName());

                } else {
                    //EmbedBuilder used to make Embed for Message
                    EmbedBuilder eb = new EmbedBuilder()
                            .setTitle("You have a reminder!", null)
                            .setDescription("@" + user.getName() + " one of your lectures will begin soon! Here is the Link: " + link);
                    messageEmbed = eb.build();
                    eb.setColor(new Color(63,196,224));
                    log.info("Message made for " + user.getName());

                }

                // Open DM with User and send message
                try {
                    user.openPrivateChannel().queue((channel) -> {channel.sendMessage(messageEmbed).queue();});
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
