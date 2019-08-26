package net.mmp.center.webapp.dto;

import net.mmp.center.webapp.model.ESData;
import org.springframework.data.domain.PageImpl;

public class BigdataAnalysisDTO {

	private String senderIp;
	private String reflectorIp;
	private int lostPacketTH;
	private int duplicatePacketTH;
	private int outoforderPacketTH;
	private int pdvTH;
	private int ipdvTH;
	private String startTime;
	private String endtime;
	
	public static class BigdataAnalysisResultDTO {
		private double searchAchiveTime;
		private PageImpl<ESData> resultData;
		
		public BigdataAnalysisResultDTO() {}
		
		public BigdataAnalysisResultDTO(double searchAchiveTime, PageImpl<ESData> resultData) {
			this.searchAchiveTime = searchAchiveTime;
			this.resultData = resultData;
		}
		
		public double getSearchAchiveTime() {
			return searchAchiveTime;
		}
		public void setSearchAchiveTime(double searchAchiveTime) {
			this.searchAchiveTime = searchAchiveTime;
		}
		public PageImpl<ESData> getResultData() {
			return resultData;
		}
		public void setResultData(PageImpl<ESData> resultData) {
			this.resultData = resultData;
		}
	}
	
	public String getSenderIp() {
		return senderIp;
	}
	public void setSenderIp(String senderIp) {
		this.senderIp = senderIp;
	}
	public String getReflectorIp() {
		return reflectorIp;
	}
	public void setReflectorIp(String reflectorIp) {
		this.reflectorIp = reflectorIp;
	}
	public int getLostPacketTH() {
		return lostPacketTH;
	}
	public void setLostPacketTH(int lostPacketTH) {
		this.lostPacketTH = lostPacketTH;
	}
	public int getDuplicatePacketTH() {
		return duplicatePacketTH;
	}
	public void setDuplicatePacketTH(int duplicatePacketTH) {
		this.duplicatePacketTH = duplicatePacketTH;
	}
	public int getOutoforderPacketTH() {
		return outoforderPacketTH;
	}
	public void setOutoforderPacketTH(int outoforderPacketTH) {
		this.outoforderPacketTH = outoforderPacketTH;
	}
	public int getPdvTH() {
		return pdvTH;
	}
	public void setPdvTH(int pdvTH) {
		this.pdvTH = pdvTH;
	}
	public int getIpdvTH() {
		return ipdvTH;
	}
	public void setIpdvTH(int ipdvTH) {
		this.ipdvTH = ipdvTH;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
}
