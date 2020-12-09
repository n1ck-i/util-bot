package org.ub.utilbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "org.ub")
@SpringBootApplication
public class UtilBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(UtilBotApplication.class, args);
	}

}
