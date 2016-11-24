package org.stocks.trackerbot.telegram.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.TrackerBot;
import org.stocks.trackerbot.tagger.BlackList;
import org.stocks.trackerbot.tagger.Tag2Emoji;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class ScanCommand extends BotCommand {

	private static final String commandIdentifier = "scan";
	private static final String description = "give me a list of stock symbol and I will check them against a blacklist";
	private TrackerBot trackerBot;
	
	public ScanCommand() {
		this(commandIdentifier, description);
	}
	
	public ScanCommand(String commandIdentifier, String description) {
		super(commandIdentifier, description);
	}

	public ScanCommand(TrackerBot trackerBot) {
		this(commandIdentifier, description);
		this.trackerBot = trackerBot;
	}

	private static final Logger logger = LoggerFactory.getLogger(ScanCommand.class);

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		try {
			SendMessage ans = new SendMessage();
			ans.setChatId(chat.getId().toString());
			if (arguments == null || arguments.length == 0) {
				ans.setText("Send something like '/scan 939'");
				absSender.sendMessage(ans);
				return;
			}
			StringBuilder reply = new StringBuilder();
			for (String symbol : arguments) {
				String tag = BlackList.INST.scan(symbol);
				int emoji = Tag2Emoji.mapTag(tag);
				reply.append(String.format("%1$04d", Integer.parseInt(symbol)) + " : ");
				reply.appendCodePoint(emoji);
				reply.append(" " + tag + "\n");
			}
			ans.setText(reply.toString());
			absSender.sendMessage(ans);
		} catch (TelegramApiException e) {
			logger.error("command execute fail", e);
		}
	}

}
