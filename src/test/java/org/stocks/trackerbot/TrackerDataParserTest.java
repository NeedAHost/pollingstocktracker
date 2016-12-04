package org.stocks.trackerbot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;
import org.stocks.trackerbot.model.TrackerData;

public class TrackerDataParserTest {

	@Test
	public void parseCapture1() throws URISyntaxException, UnsupportedEncodingException, IOException {
		Path path = Paths.get(getClass().getClassLoader().getResource("capture_1.txt").toURI());
		String rawData = new String(Files.readAllBytes(path), "UTF-8");
		
		TrackerDataParser parser = new TrackerDataParser();
		TrackerData data = parser.parse(rawData);
		System.out.println(data);
	}
	
//	@Ignore
	@Test
	public void printCapture1() throws URISyntaxException, UnsupportedEncodingException, IOException {
		TrackerBot bot = new TrackerBot();
		Scanner sc = null;
		try {
			bot.setSource(new MockTrackerSource());
			bot.startPolling();
			
			sc = new Scanner(System.in);
			System.out.println("Waiting input to exit..");
	        while(!sc.hasNextLine()) {        	
	        }
		} finally {
			bot.stop();
			sc.close();
		}
	}
	
	@Ignore
	@Test
	public void realTest() {
		TrackerBot bot = new TrackerBot();
		Scanner sc = null;
		try {
			bot.startPolling();
			
			sc = new Scanner(System.in);
			System.out.println("Waiting input to exit..");
	        while(!sc.hasNextLine()) {
	        }
		} finally {
			bot.stop();
			sc.close();
		}
	}
}
