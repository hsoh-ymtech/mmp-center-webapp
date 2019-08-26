package net.mmp.center.webapp.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class QualityHistoryDTO {
	
	public QualityHistoryDTO() {}
	
	public QualityHistoryDTO(int sessId, String senderIp, String reflectorIp, int sendCount, int repeatCount,
			LocalDateTime startTime, LocalDateTime completeTime, String measureProtocol, String measureMode,
			int senderPort, int reflectorPort, String measureResult) {
		super();
		this.sessId = sessId;
		this.senderIp = senderIp;
		this.reflectorIp = reflectorIp;
		this.sendCount = sendCount;
		this.repeatCount = repeatCount;
		this.startTime = startTime;
		this.completeTime = completeTime;
		this.measureProtocol = measureProtocol;
		this.measureMode = measureMode;
		this.senderPort = senderPort;
		this.reflectorPort = reflectorPort;
		this.measureResult = measureResult;
	}
	
	public static class QualityHistorySearchDTO {
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

	/**
	 * Session ID
	 */
	@NotNull
	private int sessId;
	
	/**
	 * Sender IP Address
	 */
	private String senderIp;
	
	/**
	 * Reflector IP Address
	 */
	private String reflectorIp;
	
	/**
	 * 1회 측정 시 송신하는 Packet 수
	 */
	@NotNull
	private int sendCount;
	
	/**
	 * 품질 측정 횟수
	 */
	@NotNull
	private int repeatCount;
	
	/**
	 * 측정 시작 시간
	 */
	@NotNull
	private LocalDateTime startTime;
	
	/**
	 * 측정 완료 시간
	 */
	@NotNull
	private LocalDateTime completeTime;
	
	/**
	 * 프로토콜 종류
	 */
	@NotBlank
	private String measureProtocol;
	
	/**
	 * Full TWAMP시 측정 모드
	 */
	@NotBlank
	private String measureMode;
	
	/**
	 * Sender Port
	 * 
	 * Registered port = 1024 ~ 49151
	 * 
	 */
	
//	@Max(value = 49151)
//	@Min(value = 1024)
	private int senderPort;
	
	/**
	 * Reflector Port
	 * 
	 * Registered port = 1024 ~ 49151
	 * 
	 */
	
//	@Max(value = 49151)
//	@Min(value = 1024)
	private int reflectorPort;
	
	/**
	 * 품질 측정 결과
	 */
	@NotBlank
	private String measureResult;

	public int getSessId() {
		return sessId;
	}

	public void setSessId(int sessId) {
		this.sessId = sessId;
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

	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(LocalDateTime completeTime) {
		this.completeTime = completeTime;
	}

	public String getMeasureProtocol() {
		return measureProtocol;
	}

	public void setMeasureProtocol(String measureProtocol) {
		this.measureProtocol = measureProtocol;
	}

	public String getMeasureMode() {
		return measureMode;
	}

	public void setMeasureMode(String measureMode) {
		this.measureMode = measureMode;
	}

	public int getSenderPort() {
		return senderPort;
	}

	public void setSenderPort(int senderPort) {
		this.senderPort = senderPort;
	}

	public int getReflectorPort() {
		return reflectorPort;
	}

	public void setReflectorPort(int reflectorPort) {
		this.reflectorPort = reflectorPort;
	}

	public String getMeasureResult() {
		return measureResult;
	}

	public void setMeasureResult(String measureResult) {
		this.measureResult = measureResult;
	}
}
