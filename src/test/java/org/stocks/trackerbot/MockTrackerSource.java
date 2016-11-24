package org.stocks.trackerbot;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class MockTrackerSource extends TrackerSource {

	@Override
	public String getData() {
		try {
			InputStream in = getClass().getResourceAsStream("/capture_1.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			
			Random rnd = new Random();
			int s = rnd.nextInt(9998) + 1;
			return sb.toString().replace("323", Integer.toString(s));
		} catch (Exception e) {
			return "";
		}
	}
	
	@Override
	public String getUrlStr() {
		return "LOCAL";
	}

}
