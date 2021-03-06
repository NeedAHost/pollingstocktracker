package org.stocks.trackerbot.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.util.NumberNormalizer;

public class Stock {

	private static final Logger logger = LoggerFactory.getLogger(Stock.class);

	private Category category;
	private String symbol;
	private String name;
	private String firstSeenPrice;
	private String price;
	private String priceChange;
	private String volume;
	private String avgVolume;
	private String startTime;
	private String endTime;
	private String open;
	private String prevClose;
	private String firstSeenTime;
	private MarkedStock marked;

	private Set<String> tags = new HashSet<String>();

	public Stock() {
		firstSeenTime = sdf.format(new Date());
	}

	public Stock(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Stock) {
			return symbol.equals(((Stock) o).getSymbol());
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (symbol != null) {
			return symbol.hashCode();
		}
		if (name != null) {
			return name.hashCode();
		}
		return super.hashCode();
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbolPadded() {
		return String.format("%04d", Integer.parseInt(symbol));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPriceChange() {
		return priceChange;
	}

	public void setPriceChange(String priceChange) {
		this.priceChange = priceChange;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getAvgVolume() {
		return avgVolume;
	}

	public void setAvgVolume(String avgVolume) {
		this.avgVolume = avgVolume;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getVolumeChange() {
		if (volume == null || avgVolume == null) {
			return "";
		}
		try {
			BigDecimal percent = NumberNormalizer.normalize(volume).multiply(new BigDecimal("100"))
					.divide(NumberNormalizer.normalize(avgVolume), RoundingMode.HALF_UP);
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(0);
			df.setMinimumFractionDigits(0);
			df.setGroupingUsed(false);
			return df.format(percent) + "%";
		} catch (Exception e) {
			logger.error("vol " + volume + ", avg vol " + avgVolume, e);
			return "";
		}
	}

	private static final String printFormat = "[%1$4s:%2$4s](%3$s)\nPrice: %4$4s (%5$s)\nVol: %6$s (%7$s)\n";
	public String print() {
		String tmp = String.format(printFormat, symbol, name, getUrl(), price, priceChange, volume,
				this.getVolumeChange());
		if (this.getMarked() != null) {
			tmp += this.getMarked().printWithStock();
		}
		return tmp;
	}
	
	private static final String noMarkupPrintFormat = "%1$4s:%2$4s\nPrice: %4$4s (%5$s)\nVol: %6$s (%7$s)\n";
	public String printNoMarkup() {
		String tmp = String.format(noMarkupPrintFormat, symbol, name, getUrl(), price, priceChange, volume,
				this.getVolumeChange());
		if (this.getMarked() != null) {
			tmp += this.getMarked().printWithStock();
		}
		tmp += this.getUrl() + "\n";
		return tmp;
	}
	
	private final static Random rnd = new Random();
	public String getChartUrl() {		
		// no cache
		return "http://chart.finance.yahoo.com/z?s=" + getSymbolPadded() + ".HK&t=1d&q=l&l=on&z=s&a=" + rnd.nextInt();
	}

	private static final String summarizeFormat = "[%1$4s](%3$s) %4$s > %5$s (%6$s)";

	public String printSummary() {
		String shortName = name.length() <= 4 ? name : name.substring(0, 4);
		return String.format(summarizeFormat, symbol, shortName, getUrl(), getFirstSeenPrice(), price, this.getEarnPercent(),
				firstSeenTime);
	}

	private String getUrl() {
		return "https://www.google.com.hk/finance?q=HKG:" + this.getSymbolPadded();
	}

	public String toString() {
		return symbol + "," + name + "," + price + "," + priceChange + "," + volume + "," + avgVolume + "," + startTime;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");

	public void setEndTime(Date endTime) {
		this.endTime = sdf.format(endTime);
	}

	public void setStartTime(Date startTime) {
		this.startTime = sdf.format(startTime);
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getPrevClose() {
		return prevClose;
	}

	public void setPrevClose(String prevClose) {
		this.prevClose = prevClose;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getFirstSeenPrice() {
		return firstSeenPrice;
	}

	public String getEarnPercent() {
		BigDecimal firstBd = new BigDecimal(getFirstSeenPrice());
		BigDecimal earning = new BigDecimal(price).subtract(firstBd).multiply(new BigDecimal("100")).divide(firstBd,
				RoundingMode.HALF_UP);
		earning = earning.setScale(2, BigDecimal.ROUND_HALF_UP);
		return earning.toString() + "%";
	}

	public String getFirstSeenTime() {
		return firstSeenTime;
	}

	public void setFirstSeenTime(String firstSeenTime) {
		if (this.firstSeenPrice == null) {
			this.firstSeenTime = firstSeenTime;
		}
	}

	public void setFirstSeenPrice(String firstSeenPrice) {
		if (this.firstSeenPrice == null) {
			this.firstSeenPrice = firstSeenPrice;
		}
	}

	public MarkedStock getMarked() {
		return marked;
	}

	public void setMarked(MarkedStock marked) {
		this.marked = marked;
	}
}
