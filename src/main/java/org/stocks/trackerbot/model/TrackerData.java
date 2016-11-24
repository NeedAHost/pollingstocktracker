package org.stocks.trackerbot.model;

import java.util.ArrayList;
import java.util.List;

public class TrackerData {

	private String id;
	private String time;
	
	private List<Stock> ups = new ArrayList<Stock>();
	private List<Stock> pendings = new ArrayList<Stock>();
	private List<Stock> pullBacks = new ArrayList<Stock>();
	private List<Stock> aos = new ArrayList<Stock>();
	private List<Stock> newHighs = new ArrayList<Stock>();
	private List<Stock> mcs = new ArrayList<Stock>();
	
	@Override
	public boolean equals(Object o) {
		TrackerData d = (TrackerData) o;
		return d.getId().equals(this.getId());
	}
	
	public String toString() {
		String p = "";
		p += id + " " + time + "\n";
		p += "\nUp\n";
		for (Stock stock : getUps()) {
			p += stock + "\n";
		}
		p += "\nPending\n";
		for (Stock stock : this.getPendings()) {
			p += stock + "\n";
		}
		p += "\nPull Back\n";
		for (Stock stock : this.getPullBacks()) {
			p += stock + "\n";
		}
		p += "\nAO\n";
		for (Stock stock : this.getAos()) {
			p += stock + "\n";
		}
		p += "\nNew High\n";
		for (Stock stock : this.getNewHighs()) {
			p += stock + "\n";
		}
		p += "\nMC\n";
		for (Stock stock : this.getMcs()) {
			p += stock + "\n";
		}
		return p;
	}

	public List<Stock> getUps() {
		return ups;
	}

	public void setUps(List<Stock> ups) {
		this.ups = ups;
	}

	public List<Stock> getPendings() {
		return pendings;
	}

	public void setPendings(List<Stock> pendings) {
		this.pendings = pendings;
	}

	public List<Stock> getPullBacks() {
		return pullBacks;
	}

	public void setPullBacks(List<Stock> pullBacks) {
		this.pullBacks = pullBacks;
	}

	public List<Stock> getAos() {
		return aos;
	}

	public void setAos(List<Stock> aos) {
		this.aos = aos;
	}

	public List<Stock> getNewHighs() {
		return newHighs;
	}

	public void setNewHighs(List<Stock> newHighs) {
		this.newHighs = newHighs;
	}

	public List<Stock> getMcs() {
		return mcs;
	}

	public void setMcs(List<Stock> mcs) {
		this.mcs = mcs;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
