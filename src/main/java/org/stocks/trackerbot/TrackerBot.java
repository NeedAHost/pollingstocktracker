package org.stocks.trackerbot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.hkex.HkexNewsWeb;
import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.model.TrackerData;
import org.stocks.trackerbot.model.hkex.News;
import org.stocks.trackerbot.tagger.Tag2Emoji;
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
	private HkexNewsWeb newsWeb = new HkexNewsWeb();
	private ScheduledExecutorService executorService;
	private TelegramHandler telegramHandler;

	private int retryCount = 0;
	private int remainingSkipCount = 0;
	private String lastDataId = "";
	private LocalDate lastTimestamp = LocalDate.now();
	private Recommender recommender;
	private boolean reportSent = false;
	
	private News lastNews = null;

	public TrackerBot() {
		// ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		telegramHandler = new TelegramHandler(this);
		try {
			telegramBotsApi.registerBot(telegramHandler);
			recommender = new Recommender();
		} catch (TelegramApiRequestException e) {
			logger.error("initialize telegram fail", e);
		}
	}

	public boolean isOldData(TrackerData data) {
		if (data == null || data.getId() == null || getLastDataId() == null) {
			return false;
		}
		return Config.skipOldData && getLastDataId().equals(data.getId());
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
					if (Config.maxRetryCount <= retryCount) {
						// sleep
						LocalDate now = LocalDate.now();
						if (now.getDayOfWeek() == DayOfWeek.SATURDAY 
								|| now.getDayOfWeek() == DayOfWeek.SUNDAY) {
							setRemainingSkipCount(6);
						} else {
							int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
							if (curHour >= 8 && curHour <= 16) {
								// active hour
								setRemainingSkipCount(0);
							} else if (curHour >= 17) {
								if (!reportSent) {
									sentReport();
								}
								setRemainingSkipCount(6);
							}							
						}
						retryCount = 0;
					}
				} else {
					retryCount = 0;
					setRemainingSkipCount(0);
					setLastDataId(pulled.getId());
					Collection<Stock> analyze = getRecommender().analyze(pulled);
					this.send(analyze);
				}				
				List<News> latestNews = this.getLatestNews();
				this.send(latestNews);				
			} catch (Exception e) {
				logger.error("unknown error", e);
			}
		}, 0, Config.pollPeriod, TimeUnit.MINUTES);
	}

	private List<News> getLatestNews() {
		// find new news
		List<News> latestNews = new ArrayList<News>();
		List<News> newsList = newsWeb.getPlacingNewsList();
		newsList.addAll(newsWeb.getRevenueNewsList());
		if (lastNews != null) {
			for (News n : newsList) {
				if (lastNews.before(n)) {
					latestNews.add(n);
				}
			}	
		} else {
			latestNews = newsList;
		}
		// update last news
		for (News n : latestNews) {
			if (lastNews == null) {
				lastNews = n;
			} else {
				if (lastNews.before(n)) {
					lastNews = n;
				}
			}
		}
		return latestNews; 
	}

	private void sentReport() {
		for (String s : getRecommender().summarize()) {
			logger.info(s);
			telegramHandler.sendMessage(s);
		}
		reportSent = true;
	}

	private void clearEveryDay() {
		LocalDate now = LocalDate.now();
		if (!now.equals(lastTimestamp)) {
			getRecommender().clear();
			reportSent = false;
		}
		lastTimestamp = now;
	}

	private void send(Collection<Stock> latest) {
		for (Stock f : latest) {
			StringBuilder msg = new StringBuilder();
			msg = msg.appendCodePoint(Tag2Emoji.mapTag(f.getCategory().name()));
			for (String t : f.getTags()) {
				Integer i = Tag2Emoji.mapTag(t);
				if (i != null) {
					msg = msg.appendCodePoint(i);
				}
			}
			msg = msg.append("\n" + f.printNoMarkup());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			this.telegramHandler.sendUrlImageAndCaption(f.getChartUrl(), msg.toString());
		}
	}
		
	private void send(List<News> latestNews) {
		for (News n : latestNews) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			this.telegramHandler.sendMessage(n.print());
		}		
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
