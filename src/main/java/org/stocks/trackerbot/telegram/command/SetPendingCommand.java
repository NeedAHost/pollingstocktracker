package org.stocks.trackerbot.telegram.command;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.Config;
import org.stocks.trackerbot.TrackerBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class SetPendingCommand extends BotCommand {

	private static final String commandIdentifier = "pending";
	private static final String description = "set pending alert on/off";
	private TrackerBot trackerBot;
	
	public SetPendingCommand() {
		this(commandIdentifier, description);
	}
	
	public SetPendingCommand(String commandIdentifier, String description) {
		super(commandIdentifier, description);
	}

	public SetPendingCommand(TrackerBot trackerBot) {
		super(commandIdentifier, description);
		this.trackerBot = trackerBot;
	}

	private static final Logger logger = LoggerFactory.getLogger(SetPendingCommand.class);

	public static List<String> trueValues = Arrays.asList("1", "ON", "on", "On", "True", "true", "Yes", "yes");
	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		try {
			logger.info(commandIdentifier + " command received from " + chat.getId().toString());
			if (arguments != null && arguments.length > 0 ) {
				if (trueValues.contains(arguments[0])) {
					Config.pendingOn = true;
				} else {
					Config.pendingOn = false;
				}
				Config.flush();
			}
			SendMessage ans = new SendMessage();
			ans.setChatId(chat.getId().toString());
			ans.setText("Pending: " + Config.pendingOn);
			ans.disableNotification();
			absSender.sendMessage(ans);
		} catch (TelegramApiException e) {
			logger.error("command execute fail", e);
		}
	}

}
