package org.stocks.trackerbot.model.hkex;

import java.util.Date;

public class News {

	private String symbol;
	private String name;
	private String title;
	private String pdfUrl;
	private Date time;

	public boolean before(News n2) {
		if (n2.getTime() == null || getTime() == null) {
			return false;
		}
		return getTime().before(n2.getTime());
	}
	
	private String getUrl() {
		return "https://www.google.com.hk/finance?q=HKG:" + this.getSymbol().substring(1);
	}
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPdfUrl() {
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return symbol + " " + name + " " + title + " " + pdfUrl + " " + time;
	}
	
	public String print() {
		return "[" + symbol + "](" + this.getUrl() + ") " + name + "\n[" + title + "](" + pdfUrl + ")\n" + time;
	}
}
