package net.mmp.center.webapp.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
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

}
