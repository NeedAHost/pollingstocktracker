package org.stocks.trackerbot;

public class Config {

	// local
//	public static final String markedFilePath = "C:\\Users\\Main\\Documents\\trackerbot\\marked.txt";
	// aws
	public static final String markedFilePath = "C:\\Users\\Administrator\\Documents\\workspace\\marked.txt";
	
	public static final int pollPeriod = 7;
	
	public static final int maxRetryCount = 3;
	
	public static final boolean skipOldData = true; 
	
	public static final Long chatId = -1001061651082L; // group
//	public static final Long chatId = 245513956L; // private
	
}
