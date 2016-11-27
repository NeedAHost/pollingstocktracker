package org.stocks.trackerbot.model.hkex;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Shareholding {

	private String shareId;
	private String participantId;
	private Date date;
	private BigInteger value;
	private Integer percentX100;

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public String getParticipantId() {
		return participantId;
	}

	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}

	public Date getDate() {
		return date;
	}

	public String getDateStr() {
		return sdf.format(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getPercentX100() {
		return percentX100;
	}

	public void setPercentX100(Integer percentX100) {
		this.percentX100 = percentX100;
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public String toString() {
		String s = "";
		s += shareId + " ";
		s += participantId + " ";
		if (date != null) {
			s += sdf.format(date) + " ";
		}
		if (value != null) {
			s += value + " ";
		}
		if (percentX100 != null) {
			try {
				BigDecimal bd = new BigDecimal(percentX100).divide(new BigDecimal(100));
				s += bd.toString();
			} catch (Exception e) {
				s += "N/A";
			}
		}
		return s;
	}

	public BigInteger getValue() {
		return value;
	}

	public void setValue(BigInteger value) {
		this.value = value;
	}
}
