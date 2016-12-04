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

public class MarkCommand extends BotCommand {

	private static final String commandIdentifier = "mark";
	private static final String description = "mark a stock e.g. [/mark symbol price shareholding]";
	private TrackerBot trackerBot;
	private MarkedStockDao dao = new MarkedStockDao(); // hope there is no
														// concurrency

	public MarkCommand() {
		this(commandIdentifier, description);
	}

	public MarkCommand(String commandIdentifier, String description) {
		super(commandIdentifier, description);
	}

	public MarkCommand(TrackerBot trackerBot) {
		super(commandIdentifier, description);
		this.trackerBot = trackerBot;
	}

	private static final Logger logger = LoggerFactory.getLogger(MarkCommand.class);

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		try {
			logger.info(commandIdentifier + " command received");
			SendMessage ans = new SendMessage();
			ans.setChatId(chat.getId().toString());
			String m = "";
			if (arguments == null || arguments.length < 3) {
				m += "Send something like '/mark symbol price shareholding'\n";
			} else {
				MarkedStock inputMs = new MarkedStock();
				inputMs.setSymbol(arguments[0]);
				inputMs.setStartPrice(arguments[1]);
				inputMs.setEndPriceByStartPrice();
				inputMs.setShareholding(arguments[2]);
				dao.addOrUpdate(inputMs);
			}
			
			Set<MarkedStock> all = dao.getAll();
			m += "Added.\n";
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
