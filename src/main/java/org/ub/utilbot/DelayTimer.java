package org.ub.utilbot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commandutils.Command;

import javax.security.auth.login.LoginException;
import java.util.*;
@Component
@Profile("!test")
class DelayTimer implements CommandLineRunner, ApplicationContextAware {

    private final Logger log = LogManager.getLogger(DelayTimer.class);
    private ApplicationContext appContext;

    @Override
    public void run(String... args) throws Exception {

        //Delay in Milliseconds * 1000 -> 15 Min
        long delay;
        delay = 5 * 1000;


        Reminder reminder = appContext.getBean(Reminder.class);
        final Timer timer = new Timer();


        final TimerTask task = new TimerTask() {
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
        timer.schedule(task, delay);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }
}
