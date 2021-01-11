package org.ub.utilbot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;

@Component
public class HelpCommand implements Command {

	private Logger log = LogManager.getLogger(HelpCommand.class);

	@Override
	public String getName() {
		return "ubot";
	}

	@Override
	public String getDescription() {
		return "Sends help message about the available commands for this bot.";
	}

	@Override
	public String getUsage() {
		return "!ubot help";
	}

	@Override
	public void onCommand(CommandContext context) {

		log.info("Help command invoked");

		// send a generic welcome message if on arguments are provided
		// or the provided arg is not "help"
		if (context.getArgs().length == 0 || !context.getArgs()[0].equals("help")) {
			log.warn("Default welcome msg");
			context.getChannel().sendMessage("Hello :salute:\n" + "For more info on available commands try '!ubot help'").queue();
			return;
		}

		// send help message
		log.info("Sending help");
		context.getChannel().sendMessage(
				"The Util Bot provides the following commands to help you find your online lectures and exercises on time.\n\n" +
				"Meeting command: " + new RequestMeeting().getUsage() +
				"\n    Subject: luds, alpro, ti, etc.." +
				"\n    Identifier: you can use the following items as identifiers" +
				"\n        list ('list')     -> keyword will list all available lectures/exercises for the specified subject" +
				"\n        time ('12:00')    -> will list all lectures/exercises around the specified time and subject" +
				"\n        groupNumber ('2') -> will send the link for the specified exercise group" +
				"\n        tutorName         -> will list all exercises hosted by the specified tutor" +
				"\n\n" +
				"Remind command: " + new RemindCommand().getUsage() +
				"\n    With the remind command you can select lectures/exercises similar to how you request meetings and can then subscribe to them." +
				"\n    You will then be notified each time shortly before the lecture/exercise starts with the link to join." +
				"\n\n" +
				"Remove command: " + new RemoveCommand().getUsage() +
				"\n    With the remove command you can unsubscribe to an existing reminder." +
				"\n    Also when you subscribe to a reminder the bot will send you a command you can easily copy paste to unsubscribe."
				).queue();
	}

}
