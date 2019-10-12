package net.mmp.center.webapp.dto;

import lombok.Data;

@Data
public class LesionHistoryDTO {

	private int lhId;
	private int sessId;
	private String startTime;
	private String completeTime;
	private String senderIp;
	private String reflectorIp;
	private String lesionCode;

	@Data
	public static class LesionHistorySearchDTO {
		private String senderIp;
		private String reflectorIp;
	}
}
