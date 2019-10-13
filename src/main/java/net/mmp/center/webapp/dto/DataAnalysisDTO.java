package net.mmp.center.webapp.dto;

import lombok.Data;
import net.mmp.center.webapp.model.ESData;
import org.springframework.data.domain.PageImpl;

@Data
public class DataAnalysisDTO {

	private String senderIp;
	private String reflectorIp;
	private int lostPacketTH;
	private int duplicatePacketTH;
	private int outoforderPacketTH;
	private int pdvTH;
	private int ipdvTH;
	private String startTime;
	private String endtime;
	
	public static class DataataAnalysisResultDTO {
		private double searchAchiveTime;
		private PageImpl<ESData> resultData;
		
		public DataataAnalysisResultDTO() {}
		
		public DataataAnalysisResultDTO(double searchAchiveTime, PageImpl<ESData> resultData) {
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

}
