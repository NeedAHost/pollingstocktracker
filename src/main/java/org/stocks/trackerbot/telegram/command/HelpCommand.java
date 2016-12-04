package org.stocks.trackerbot.telegram.command;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.Config;
import org.stocks.trackerbot.TrackerBot;
import org.stocks.trackerbot.tagger.Tag2Emoji;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class HelpCommand extends BotCommand {

	private static final String commandIdentifier = "help";
	private static final String description = "print basic info";
	private TrackerBot trackerBot;
	
	public HelpCommand() {
		this(commandIdentifier, description);
	}
	
	public HelpCommand(String commandIdentifier, String description) {
		super(commandIdentifier, description);
	}

	public HelpCommand(TrackerBot trackerBot) {
		super(commandIdentifier, description);
		this.trackerBot = trackerBot;
	}

	private static final Logger logger = LoggerFactory.getLogger(HelpCommand.class);

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		try {
			logger.info(commandIdentifier + " command received from " + chat.getId().toString());
			StringBuilder sb = new StringBuilder();
			for (Entry<String, Integer> e : Tag2Emoji.getMap().entrySet()) {
				sb.appendCodePoint(e.getValue()).append(" " + e.getKey() + "\n");
			}
			SendMessage ans = new SendMessage();
			ans.setChatId(Config.chatId.toString());
			ans.setText(sb.toString());
			ans.disableNotification();
			absSender.sendMessage(ans);
		} catch (TelegramApiException e) {
			logger.error("command execute fail", e);
		}
	}

}
