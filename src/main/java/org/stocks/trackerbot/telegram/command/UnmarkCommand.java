package org.stocks.trackerbot.telegram.command;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.TrackerBot;
import org.stocks.trackerbot.dao.MarkedStockDao;
import org.stocks.trackerbot.model.MarkedStock;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class UnmarkCommand extends BotCommand {

	private static final String commandIdentifier = "unmark";
	private static final String description = "unmark a stock e.g. [/unmark symbol]";
	private TrackerBot trackerBot;
	private MarkedStockDao dao = new MarkedStockDao(); // hope there is no
														// concurrency

	public UnmarkCommand() {
		this(commandIdentifier, description);
	}

	public UnmarkCommand(String commandIdentifier, String description) {
		super(commandIdentifier, description);
	}

	public UnmarkCommand(TrackerBot trackerBot) {
		super(commandIdentifier, description);
		this.trackerBot = trackerBot;
	}

	private static final Logger logger = LoggerFactory.getLogger(UnmarkCommand.class);

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		try {
			logger.info(commandIdentifier + " command received");
			SendMessage ans = new SendMessage();
			ans.setChatId(chat.getId().toString());
			String m = "";
			if (arguments == null || arguments.length < 1) {
				m += "Send something like '/unmark symbol'\n";
			} else {
				MarkedStock inputMs = new MarkedStock();
				inputMs.setSymbol(arguments[0]);
				dao.remove(inputMs);
			}
			
			Set<MarkedStock> all = dao.getAll();
			m = "Removed.\n";
			for (MarkedStock ms : all) {
				m += ms.print() + "\n";
			}
			ans.setText(m);
			ans.disableNotification();
			absSender.sendMessage(ans);
		} catch (TelegramApiException e) {
			logger.error("command execute fail", e);
		}
	}

}
