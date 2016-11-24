package org.stocks.trackerbot;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.model.TrackerData;
import org.stocks.trackerbot.telegram.TelegramHandler;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

public class TrackerBot {

	public static void main(String[] args) {
		TrackerBot bot = new TrackerBot();
		try {
			bot.startPolling();
			while (true) {
			}
		} finally {
			bot.stop();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(TrackerBot.class);
	private TrackerSource source = new TrackerSource();
	private TrackerDataParser parser = new TrackerDataParser();
	private ScheduledExecutorService executorService;
	private TelegramHandler telegramHandler;

	private int pollPeriod = 7;
	private int maxRetryCount = 3;
	private int retryCount = 0;
	private int remainingSkipCount = 0;
	private String lastDataId = "";
	private LocalDate lastTimestamp = LocalDate.now();
	private Recommender recommender = new Recommender();

	public TrackerBot() {
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		telegramHandler = new TelegramHandler(this);
		try {
			telegramBotsApi.registerBot(telegramHandler);
		} catch (TelegramApiRequestException e) {
			logger.error("initialize telegram fail", e);
		}
	}

	public boolean isOldData(TrackerData data) {
		return getLastDataId().equals(data.getId());
	}

	public void stop() {
		if (executorService != null) {
			executorService.shutdown();
		}
		executorService = Executors.newScheduledThreadPool(2);
	}

	public void startPolling() {
		stop();
		executorService.scheduleAtFixedRate(() -> {
			try {
				clearEveryDay();
				if (getRemainingSkipCount() > 0) {
					setRemainingSkipCount(getRemainingSkipCount() - 1);
					return;
				}
				logger.info("polling from " + getSource().getUrlStr());
				TrackerData pulled = this.pullSource();
				logger.info("pulled.. \n" + pulled.toString());
				if (isOldData(pulled)) {
					logger.info("Old data " + pulled.getId());
					retryCount++;
					if (maxRetryCount <= retryCount) {
						// sleep
						int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
						if (curHour >= 0 && curHour <= 9) {
							// active hour
							setRemainingSkipCount(3);
						} else {
							setRemainingSkipCount(12);
						}
						retryCount = 0;
					}
					return;
				} else {
					retryCount = 0;
					setRemainingSkipCount(0);
				}
				setLastDataId(pulled.getId());
				telegramHandler.sendMessage(getRecommender().analyze(pulled));
			} catch (Exception e) {
				logger.error("unknown error", e);
			}
		}, 0, pollPeriod, TimeUnit.MINUTES);
	}

	private void clearEveryDay() {
		LocalDate now = LocalDate.now();
		if (!now.equals(lastTimestamp)) {
			getRecommender().clear();
		}
		lastTimestamp = now;
	}

	public TrackerData pullSource() {
		String rawStr = getSource().getData();
		TrackerData parsed = parser.parse(rawStr);
		return parsed;
	}

	public TrackerSource getSource() {
		return source;
	}

	public void setSource(TrackerSource source) {
		this.source = source;
	}

	public int getRemainingSkipCount() {
		return remainingSkipCount;
	}

	public void setRemainingSkipCount(int remainingSkipCount) {
		this.remainingSkipCount = remainingSkipCount;
	}

	public String getLastDataId() {
		return lastDataId;
	}

	public void setLastDataId(String lastDataId) {
		this.lastDataId = lastDataId;
	}

	public Recommender getRecommender() {
		return recommender;
	}

	public void setRecommender(Recommender recommender) {
		this.recommender = recommender;
	}

}
