package org.stocks.trackerbot.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.Config;
import org.stocks.trackerbot.model.MarkedStock;

public class MarkedStockDao {

	private static final Logger logger = LoggerFactory.getLogger(MarkedStockDao.class);

	public Set<MarkedStock> getAll() {
		Set<MarkedStock> temp = new LinkedHashSet<MarkedStock>();
		File file = new File(Config.markedFilePath);
		try (InputStream is = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader br = new BufferedReader(isr);) {

			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				try {
					String[] split = line.split(",");
					MarkedStock ms = new MarkedStock();
					ms.setSymbol(split[0]);
					ms.setStartPrice(split[1]);
					ms.setEndPrice(split[2]);
					ms.setShareholding(split[3]);
					if (split.length >= 5) {
						ms.setCompletionDate(split[4]);
					}
					temp.add(ms);
				} catch (Exception e) {
					logger.error("invalid format: " + line, e);
				}
			}
		} catch (Exception e) {
			logger.error("get all fail", e);
		}
		return temp;
	}

	private void write(Set<MarkedStock> markedStocks) {
		File file = new File(Config.markedFilePath);
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
			for (MarkedStock ms : markedStocks) {
				writer.write(ms.toString() + "\n");				
			}
		} catch (Exception e) {
			logger.error("write fail", e);
		}
	}

	public void addOrUpdate(MarkedStock ms) {
		Set<MarkedStock> all = this.getAll();
		all.remove(ms);
		all.add(ms);
		write(all);
	}
	
	public void remove(MarkedStock ms) {
		Set<MarkedStock> all = this.getAll();
		all.remove(ms);
		write(all);
	}

}
