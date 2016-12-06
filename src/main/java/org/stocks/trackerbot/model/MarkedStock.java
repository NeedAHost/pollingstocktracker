package org.stocks.trackerbot.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.stocks.trackerbot.util.NumberNormalizer;

public class MarkedStock {

	private String symbol;
	private String startPrice;
	private String endPrice;
	private String shareholding;
	private Stock stock;
	private LocalDate completionDate;

	private static final BigDecimal scale = new BigDecimal("1.3");

	@Override
	public int hashCode() {
		if (symbol == null) {
			return super.hashCode();
		}
		return symbol.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MarkedStock) {
			return symbol.equals(((MarkedStock) o).getSymbol());
		}
		return false;
	}

	public String printWithStock() {
		StringBuilder tmp = new StringBuilder("Expected price: (" + getStartPrice() + ", " + getEndPrice() + ") ");
		if (stock != null) {
			BigDecimal cur = NumberNormalizer.normalize(stock.getPrice());
			if (cur.compareTo(new BigDecimal(this.getStartPrice())) <= 0
					|| cur.compareTo(new BigDecimal(this.getEndPrice())) >= 0) {
				tmp.appendCodePoint(Emoji.cross);
			} else {
				tmp.appendCodePoint(Emoji.tick);
			}
		}
		tmp.append("\nVol limit: " + getShareholding() + "M ");
		if (stock != null) {
			BigDecimal cur = NumberNormalizer.normalize(stock.getVolume());
			if (cur.compareTo(new BigDecimal(this.getShareholding())) >= 0) {
				tmp.appendCodePoint(Emoji.tick);
			} else {
				tmp.appendCodePoint(Emoji.cross);
			}
		}
		if (this.completionDate != null) {
			tmp.append("\nCompletion: " + this.completionDate.toString());
		}
		tmp.append("\n");
		return tmp.toString();
	}

	public String getSymbol() {
		return symbol;
	}

	private final static Pattern noLeadingZeroPattern = Pattern.compile("^[0]+(?!$)");

	public void setSymbol(String symbol) {
		String noLeadingZero = noLeadingZeroPattern.matcher(symbol).replaceFirst("");
		this.symbol = String.format("%04d", Integer.parseInt(noLeadingZero));
	}

	public String getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(String startPrice) {
		this.startPrice = startPrice;
	}

	public String getEndPrice() {
		return endPrice;
	}

	public void setEndPrice(String endPrice) {
		this.endPrice = endPrice;
	}

	public void setEndPriceByStartPrice() {
		if (startPrice == null) {
			return;
		}
		BigDecimal ret = new BigDecimal(startPrice);
		ret = ret.multiply(scale);
		ret = ret.setScale(2, RoundingMode.HALF_UP);
		endPrice = ret.toString();
	}

	public String getShareholding() {
		return shareholding;
	}

	public void setShareholding(String shareholding) {
		this.shareholding = shareholding;
	}

	@Override
	public String toString() {
		String m = symbol + "," + startPrice + "," + endPrice + "," + shareholding + ",";
		if (this.getCompletionDate() != null) {
			m += getCompletionDate().toString();
		}
		return m;
	}

	public String print() {
		String m = symbol + ", " + startPrice + ", " + shareholding + "M";
		if (this.getCompletionDate() != null) {
			m += ", " + getCompletionDate();
		}
		return m;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public LocalDate getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(LocalDate completionDate) {
		this.completionDate = completionDate;
	}

	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public void setCompletionDate(String completionDate) {
		if (completionDate == null || completionDate.length() == 0) {
			return;
		}
		try {
			this.completionDate = LocalDate.parse(completionDate, dtf);
		} catch (Exception e) {
		}
	}
}
