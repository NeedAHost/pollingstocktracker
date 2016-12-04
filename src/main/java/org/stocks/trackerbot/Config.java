package org.stocks.trackerbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

	private static final Logger logger = LoggerFactory.getLogger(Config.class);

	// local
	public static final String[] configFilePath = new String[] { "C:\\Users\\Main\\Documents\\trackerbot\\config.txt",
			"C:\\Users\\Administrator\\Documents\\workspace\\config.txt" };

	public static String markedFilePath = "C:\\Users\\Main\\Documents\\trackerbot\\marked.txt";

	public static int pollPeriod = 10;

	public static int maxRetryCount = 3;

	public static boolean skipOldData = true;

	public static Long chatId;
	
	public static String token;

	static {
		File file = null;
		for (String p : configFilePath) {
			file = new File(p);
			if (file.exists() && file.isFile()) {
				logger.info("Config file: " + p);
				break;
			}
		}
		try (InputStream is = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader br = new BufferedReader(isr);) {

			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				try {
					String[] split = line.split("=");
					if (split == null || split.length < 2) {
						continue;
					}
					logger.info("Config: " + line);
					String key = split[0];
					String value = split[1];
					if (key.equals("pollPeriod")) {
						pollPeriod = Integer.parseInt(value);
					} else if (key.equals("maxRetryCount")) {
						maxRetryCount = Integer.parseInt(value);
					} else if (key.equals("chatId")) {
						chatId = Long.parseLong(value);
					} else if (key.equals("markedFilePath")) {
						markedFilePath = value;
					} else if (key.equals("token")) {
						token = value;
					}
				} catch (Exception e) {
					logger.error("invalid format: " + line, e);
				}
			}
		} catch (Exception e) {
			logger.error("config init fail", e);
		}
	}

}
