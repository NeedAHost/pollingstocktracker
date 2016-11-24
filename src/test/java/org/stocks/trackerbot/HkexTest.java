package org.stocks.trackerbot;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;
import org.stocks.trackerbot.dao.ShareholdingDao;
import org.stocks.trackerbot.hkex.HkexSource;
import org.stocks.trackerbot.model.hkex.Shareholding;
import org.stocks.trackerbot.util.DateUtil;
import org.stocks.trackerbot.util.HttpUtil;

public class HkexTest {

	@Test
	public void postTest() {
		String sessionToken = "4730.22";
		String data = "txt_today_d=21&txt_today_m=11&txt_today_y=2016&current_page=1&stock_market=HKEX&IsExist_Slt_Stock_Id=False&IsExist_Slt_Part_Id=False&rdo_SelectSortBy=Shareholding&sessionToken=" + sessionToken + "&sel_ShareholdingDate_d=20&sel_ShareholdingDate_m=11&sel_ShareholdingDate_y=2016&txt_stock_code=00001&txt_stock_name=&txt_ParticipantID=&txt_Participant_name=";
		String cookie = "ASPSESSIONIDQABRTRAT=AKKLCBNAIJANPDDCEMIAELDL; path=/TS0161f2e5=014125927600311a218d05d321e8625d69350da7ea1f244611c6c728f40f35e50a353b9f9f18d92d8776f02b6bcd23563f9693ef53; Path=/; HTTPOnly";
		String resp = HttpUtil.post("http://www.hkexnews.hk/sdw/search/search_sdw.asp", data, cookie);
		System.out.println(resp);
	}
	
	@Test
	public void extractTokenTest() {
		try {
			InputStream in = getClass().getResourceAsStream("/hkex_capture_get_1.html");
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			HkexSource hkex = new HkexSource();
			String extractSessionToken = hkex.extractSessionToken(sb.toString());
			System.out.println(extractSessionToken);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void realTest() {
		HkexSource hkex = new HkexSource();
		ShareholdingDao dao = new ShareholdingDao();
		String symbol = "0575";
		
		List<String> dates = DateUtil.getDateStrInRange("2015-11-24", "2016-11-23");
		for (String date : dates) {List<Shareholding> shList = hkex.get(symbol, date);
			for (Shareholding sh : shList) {
				System.out.println(sh);
			}
			dao.insert(shList);
		}
	}
	
}
