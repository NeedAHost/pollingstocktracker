package org.stocks.trackerbot.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.stocks.trackerbot.util.NumberNormalizer;

public class Stock {

	private Category category;
	private String symbol;
	private String name;
	private String price;
	private String priceChange;
	private String volume;
	private String avgVolume;
	private String startTime;
	private String endTime;
	private String open;
	private String prevClose;

	private Set<String> tags = new HashSet<String>();
	
	public Stock() {
		
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
		BigDecimal percent = NumberNormalizer.normalize(volume).multiply(new BigDecimal("100"))
				.divide(NumberNormalizer.normalize(avgVolume), RoundingMode.HALF_UP);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(0);
		df.setMinimumFractionDigits(0);
		df.setGroupingUsed(false);
		return df.format(percent) + "%";
	}

	private static final String format = "[%1$4s:%2$4s](%3$s) %4$4s(%5$s) Vol:%6$s(%7$s) %8$s";

	public String print() {
		String shortName = name.length() <= 4 ? name : name.substring(0, 4);
		return String.format(format, symbol, shortName, getUrl(), price, priceChange, volume, this.getVolumeChange(),
				this.getStartTime());
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
}
