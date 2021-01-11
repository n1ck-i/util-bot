package org.ub.utilbot.reminder;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
class DelayTimer implements CommandLineRunner, ApplicationContextAware {

    private final Logger log = LogManager.getLogger(DelayTimer.class);

    private ApplicationContext appContext;

    @Override
    public void run(String... args) throws Exception {

        //Delay in 1800 milliseconds * 1000 -> 30 Min
        long delay = 1800 * 1000;


        Reminder reminder = appContext.getBean(Reminder.class);
        Timer timer = new Timer();


        // wrap the remind function of the Rimder class in a timer task
        // so it can be scheduled
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    reminder.remind();
                }
                catch (LoginException e) {
                    log.error("" + Arrays.toString(e.getStackTrace()));
                }
            }
        };
        log.info("Starting schedule");
        timer.schedule(task, delay, delay);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }
}
