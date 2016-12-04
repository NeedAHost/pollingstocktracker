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

public class ResetCommand extends BotCommand {

	private static final String commandIdentifier = "reset";
	private static final String description = "reset timer and flush memory";
	private TrackerBot trackerBot;
	
	public ResetCommand() {
		this(commandIdentifier, description);
	}
	
	public ResetCommand(String commandIdentifier, String description) {
		super(commandIdentifier, description);
	}

	public ResetCommand(TrackerBot trackerBot) {
		super(commandIdentifier, description);
		this.trackerBot = trackerBot;
	}

	private static final Logger logger = LoggerFactory.getLogger(ResetCommand.class);
	
	public void reset() {
		trackerBot.setRemainingSkipCount(0);
		trackerBot.setLastDataId("");
		trackerBot.getRecommender().clear();
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		try {
			logger.info(commandIdentifier + " command received");
			reset();
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
