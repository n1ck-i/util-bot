package org.ub.utilbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.ub.utilbot.commandutils.Command;
import org.ub.utilbot.commandutils.CommandContext;

import java.awt.*;

@Component
public class HelpCommand implements Command {

	private Logger log = LogManager.getLogger(HelpCommand.class);

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getDescription() {
		return "Sends help message about the available commands for this bot.";
	}

	@Override
	public String getUsage() {
		return "!help";
	}

	@Override
	public void onCommand(CommandContext context) {

		// send help message
		log.info("Sending help");
		EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Help:", null);
			eb.setColor(new Color(63,196,224));
			eb.setDescription("The Util Bot provides the following commands to help you find your online lectures and exercises on time.\n\n");
			eb.addField("Meeting command: " + new RequestMeeting().getUsage(),
					"    Subject: luds, alpro, ti, etc.." +
					"\n    Identifier: you can use the following items as identifiers" +
					"\n        list ('list')     -> keyword will list all available lectures/exercises for the specified subject" +
					"\n        time ('12:00')    -> will list all lectures/exercises around the specified time and subject" +
					"\n        groupNumber ('2') -> will send the link for the specified exercise group" +
					"\n        tutorName         -> will list all exercises hosted by the specified tutor",false);
			eb.addField("Remind command: " + new RemindCommand().getUsage(),
					"    With the remind command you can select lectures/exercises similar to how you request meetings and can then subscribe to them." +
					"\n    You will then be notified each time shortly before the lecture/exercise starts with the link to join.",false);
			eb.addField(
					"Remove command: " + new RemoveCommand().getUsage(),
					"    With the remove command you can unsubscribe to an existing reminder." +
					"\n    Also when you subscribe to a reminder the bot will send you a command you can easily copy paste to unsubscribe.", false);
			eb.addField("Reminder command: !reminders","\n Lists all your currently running reminders together with the command to remove them respectively.", false);
			log.info("compiling message");
		context.getChannel().sendMessage(eb.build()).queue();

	}

}
