package org.stocks.trackerbot.telegram.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.TrackerBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class StopCommand extends BotCommand {

	private static final String commandIdentifier = "stop";
	private static final String description = "stop messaging";
	private TrackerBot trackerBot;
	
	public StopCommand() {
		this(commandIdentifier, description);
	}
	
	public StopCommand(String commandIdentifier, String description) {
		super(commandIdentifier, description);
	}

	public StopCommand(TrackerBot trackerBot) {
		super(commandIdentifier, description);
		this.trackerBot = trackerBot;
	}

	private static final Logger logger = LoggerFactory.getLogger(StopCommand.class);
	
	public void stop() {
		trackerBot.setRemainingSkipCount(Integer.MAX_VALUE);
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		try {
			logger.info(commandIdentifier + " command received");
			stop();
			SendMessage ans = new SendMessage();
			ans.setChatId(chat.getId().toString());
			ans.setText("acknowledged");
			ans.disableNotification();
			absSender.sendMessage(ans);
		} catch (TelegramApiException e) {
			logger.error("command execute fail", e);
		}
	}

}
