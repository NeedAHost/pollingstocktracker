package org.stocks.trackerbot.telegram.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.TrackerBot;
import org.stocks.trackerbot.model.Stock;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class ListHistoryCommand extends BotCommand {

	private static final String commandIdentifier = "list";
	private static final String description = "list all recommended stocks";
	private TrackerBot trackerBot;
	
	public ListHistoryCommand() {
		this(commandIdentifier, description);
	}
	
	public ListHistoryCommand(String commandIdentifier, String description) {
		super(commandIdentifier, description);
	}

	public ListHistoryCommand(TrackerBot trackerBot) {
		super(commandIdentifier, description);
		this.trackerBot = trackerBot;
	}

	private static final Logger logger = LoggerFactory.getLogger(ListHistoryCommand.class);
	
	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		try {
			String m = "Recommended: ";
			for (Stock s : trackerBot.getRecommender().getRecommended()) {
				m += s.getSymbolPadded() + " ";
			}
			SendMessage ans = new SendMessage();
			ans.setChatId(chat.getId().toString());
			if (m.length() > 0) {
				ans.setText(m);
			} else {
				ans.setText("Nothing");
			}
			absSender.sendMessage(ans);
		} catch (TelegramApiException e) {
			logger.error("command execute fail", e);
		}
	}

}
