package org.ub.utilbot;

/*
 *
 * This code is only meant as an example and therefore commented out
 * so it doesn't run
 *
 */

/*
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.ub.utilbot.entities.User;
import org.ub.utilbot.repositories.UserRepository;

// @Component is used so that the springframework knows to instaciate this class
@Component
// the profile settings are set so that this will not be executed during JUnit context test (compile time)
@Profile("!test")
public class TestRunner implements CommandLineRunner {

	// Logger is used instead of println so it can be output
	// to a file as well as STDOUT
	// class is passed as argmuent for the naming of the logger
	private final Logger log = LogManager.getLogger(TestRunner.class);

	// with @Autowired the spring framework will automatically create
	// an instance of the specified interface (org.ub.ubot.repositories.UserRepository)
	// ass well as the necessary MySQL query code at runtime and "inject" it
	@Autowired
	private UserRepository userRepo;

	@Override
	public void run(String... args) throws Exception {
		// findAll() will query for all available entries in the database
		for (User u : userRepo.findAll()) {
			//log.info(u.getDiscordId());
		}
		// findById() will query for a specified entry in the database
		// with the given id
		User test = userRepo.findById("0cc7bbb4-3b2b-11eb-9782-107b44195023");
		log.info("Found: " + test);

		// to save a new entry first create new instance
		User second = new User();
		// set the values accordingly
		second.setDiscordId("yet-another-from-code");
		// IMPORTANT: do not set the id, since it will be generated
		//			  when saving the entry in the database
		// then save the new instance
		second = userRepo.save(second);
		// now the generated id will be available in the returned instance
		log.info("New user saved under id: " + second.getId());
	}

}
*/