package org.stocks.trackerbot.telegram.command;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.TrackerBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class ReportCommand extends BotCommand {

	private static final String commandIdentifier = "report";
	private static final String description = "create summary for today's recommended ";
	private TrackerBot trackerBot;
	
	public ReportCommand() {
		this(commandIdentifier, description);
	}
	
	public ReportCommand(String commandIdentifier, String description) {
		super(commandIdentifier, description);
	}

	public ReportCommand(TrackerBot trackerBot) {
		super(commandIdentifier, description);
		this.trackerBot = trackerBot;
	}

	private static final Logger logger = LoggerFactory.getLogger(ReportCommand.class);
	
	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		try {
			List<String> summarized = this.trackerBot.getRecommender().summarize();
			for (String s : summarized) {
				SendMessage ans = new SendMessage();
				ans.enableMarkdown(true);
				ans.setChatId(chat.getId().toString());
				ans.setText(s);
				ans.disableWebPagePreview();
				absSender.sendMessage(ans);
			}		
		} catch (TelegramApiException e) {
			logger.error("command execute fail", e);
		}
	}

}
