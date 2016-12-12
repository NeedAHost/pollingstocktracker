package org.stocks.trackerbot.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.Config;
import org.stocks.trackerbot.TrackerBot;
import org.stocks.trackerbot.telegram.command.HelpCommand;
import org.stocks.trackerbot.telegram.command.ListHistoryCommand;
import org.stocks.trackerbot.telegram.command.MarkCommand;
import org.stocks.trackerbot.telegram.command.ReportCommand;
import org.stocks.trackerbot.telegram.command.ResetCommand;
import org.stocks.trackerbot.telegram.command.ScanCommand;
import org.stocks.trackerbot.telegram.command.SetPendingCommand;
import org.stocks.trackerbot.telegram.command.SetPullBackCommand;
import org.stocks.trackerbot.telegram.command.StopCommand;
import org.stocks.trackerbot.telegram.command.UnmarkCommand;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class TelegramHandler extends TelegramLongPollingCommandBot {

	private static final Logger logger = LoggerFactory.getLogger(TelegramHandler.class);
	private final String name = "PollingTrackerBot";
	private final String token = Config.token;
	private TrackerBot trackerBot;
		
	public TelegramHandler(TrackerBot trackerBot) {
		this.trackerBot = trackerBot;
		register(new ScanCommand(trackerBot));
		register(new ResetCommand(trackerBot));
		register(new StopCommand(trackerBot));
		register(new ListHistoryCommand(trackerBot));
		register(new ReportCommand(trackerBot));
		register(new MarkCommand(trackerBot));
		register(new UnmarkCommand(trackerBot));
		register(new HelpCommand(trackerBot));
		register(new SetPendingCommand(trackerBot));
		register(new SetPullBackCommand(trackerBot));
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
	
	public void send(SendMessage req) {
		req.enableMarkdown(true);
		req.setChatId(Config.chatId.toString());
		req.disableWebPagePreview();
		try {
			logger.info("sending telegram msg..");
			sendMessage(req);
		} catch (TelegramApiException e) {
			logger.error("send msg fail", e);
		}
	}
	
	public void sendUrlImageAndCaption(String url, String caption) {
        SendPhoto req = new SendPhoto();
        // Set destination chat id
        req.setChatId(Config.chatId.toString());
        // Set the photo url as a simple photo
        req.setPhoto(url);
//        req.disableNotification();
        req.setCaption(caption);
        try {
            // Execute the method
        	logger.info("sending telegram msg..");
            sendPhoto(req);
        } catch (TelegramApiException e) {
        	logger.error("send image fail", e);
        }
	}

	public Message sendMessage(String msg) {
		if (msg == null || msg.length() == 0) {
			return null;
		}
		SendMessage req = new SendMessage();
		req.enableMarkdown(true);
		req.setText(msg);
		req.setChatId(Config.chatId.toString());
		req.disableWebPagePreview();
		try {
			logger.info("sending telegram msg..");
			return sendMessage(req);
		} catch (TelegramApiException e) {
			logger.error("send msg fail", e);
			return null;
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
