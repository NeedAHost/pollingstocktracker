package org.stocks.trackerbot.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.Config;
import org.stocks.trackerbot.TrackerBot;
import org.stocks.trackerbot.telegram.command.ListHistoryCommand;
import org.stocks.trackerbot.telegram.command.ReportCommand;
import org.stocks.trackerbot.telegram.command.ResetCommand;
import org.stocks.trackerbot.telegram.command.ScanCommand;
import org.stocks.trackerbot.telegram.command.StopCommand;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class TelegramHandler extends TelegramLongPollingCommandBot {

	private static final Logger logger = LoggerFactory.getLogger(TelegramHandler.class);
	private final String name = "PollingTrackerBot";
	private final String token = "219272152:AAEE9xHk0_eB53KUgjNzmMy8hBium_C4Az4";
	private TrackerBot trackerBot;
		
	public TelegramHandler(TrackerBot trackerBot) {
		this.trackerBot = trackerBot;
		register(new ScanCommand(trackerBot));
		register(new ResetCommand(trackerBot));
		register(new StopCommand(trackerBot));
		register(new ListHistoryCommand(trackerBot));
		register(new ReportCommand(trackerBot));
	}

	@Override
	public void processNonCommandUpdate(Update update) {
		try {
			if (update.hasMessage()) {
				Message message = update.getMessage();

			}
		} catch (Exception e) {
			logger.error("handle update fail", e);
		}
	}

	public void sendMessage(String msg) {
		if (msg == null || msg.length() == 0) {
			return;
		}
		SendMessage req = new SendMessage();
		req.enableMarkdown(true);
		req.setText(msg);
		req.setChatId(Config.chatId.toString());
		req.disableWebPagePreview();
		try {
			logger.info("sending telegram msg..");
			sendMessage(req);
		} catch (TelegramApiException e) {
			logger.error("send msg fail", e);
		}
	}

	@Override
	public String getBotUsername() {
		return name;
	}

	@Override
	public String getBotToken() {
		return token;
	}

}
