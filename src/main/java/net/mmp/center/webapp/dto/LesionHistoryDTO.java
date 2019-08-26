package net.mmp.center.webapp.dto;

public class LesionHistoryDTO {

	private int lhId;
	private int sessId;
	private String startTime;
	private String completeTime;
	private String senderIp;
	private String reflectorIp;
	private String lesionCode;

	
	public int getLhId() {
		return lhId;
	}

	public void setLhId(int lhId) {
		this.lhId = lhId;
	}

	public int getSessId() {
		return sessId;
	}

	public void setSessId(int sessId) {
		this.sessId = sessId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
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

	public String getLesionCode() {
		return lesionCode;
	}

	public void setLesionCode(String lesionCode) {
		this.lesionCode = lesionCode;
	}
	
	public static class LesionHistorySearchDTO {
		private String senderIp;
		private String reflectorIp;
		
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
	}
}
