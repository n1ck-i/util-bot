package org.ub.utilbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;

public class JDAClient {
    private JDA client;

    private static JDAClient instance;

    private final Logger log = LogManager.getLogger(JDAClient.class);

    private JDAClient() throws LoginException {
        client = JDABuilder.createDefault("Nzg2Mjk3NDczMTQ0NDU1MjA5.X9EWlg.Nsjc4ZkqY7rLAaqrtVnWADJ54IU").build();
    }

    public static JDAClient getInstance() throws LoginException {
        if(instance == null) {
            instance = new JDAClient();
        }
        return instance;
    }

    public JDA getJDA() {
        return this.client;
    }
}
