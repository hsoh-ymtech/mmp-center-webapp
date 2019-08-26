package net.mmp.center.webapp.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class CurrentStatusDTO {
	
	public CurrentStatusDTO() {}
	
	public CurrentStatusDTO(int sessId, String senderIp, String reflectorIp, int sendCount, int repeatCount,
			int senderPort, int reflectorPort, String protocol, String measureMode, LocalDateTime startTime, int pid, int timeout, String id, String password) {
		super();
		this.sessId = sessId;
		this.senderIp = senderIp;
		this.reflectorIp = reflectorIp;
		this.sendCount = sendCount;
		this.repeatCount = repeatCount;
		this.senderPort = senderPort;
		this.reflectorPort = reflectorPort;
		this.protocol = protocol;
		this.measureMode = measureMode;
		this.startTime = startTime;
		this.pid = pid;
		this.timeout = timeout;
		this.id = id;
		this.password = password;
	}
	

	public static class CurrentStatusResultDTO {
		@NotNull
		private int sessid;
		@NotEmpty
		private String startTime;
		@NotEmpty
		private String endTime;
		@NotNull
		private int result;
		
		public int getSessid() {
			return sessid;
		}
		public void setSessid(int sessid) {
			this.sessid = sessid;
		}
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public int getResult() {
			return result;
		}
		public void setResult(int result) {
			this.result = result;
		}
	}

	/**
	 * Session ID
	 */
	private int sessId;
	
	/**
	 * Sender IP Address
	 */
	private String senderIp;
	
	/**
	 * Reflector Ip Address
	 */
	private String reflectorIp;
	
	/**
	 * 1회 측정 시 송신하는 Packet 수
	 */
	private int sendCount;
	
	/**
	 * 품질 측정 횟수
	 */
	private int repeatCount;
	
	/**
	 * Sender Port
	 */
	private int senderPort;
	
	/**
	 * Reflector Port
	 */
	private int reflectorPort;
	
	/**
	 * protocol
	 */
	private String protocol;

	/**
	 * measure Mode
	 */
	@NotEmpty
	private String measureMode;

	/**
	 * 측정 시작 시간
	 */
	@NotNull
	private LocalDateTime startTime;
	
	/**
	 * Process ID
	 */
	@NotNull
	private int pid;
	
	/**
	 * Time Out
	 */
	@NotNull
	@Min(value = 1)
	@Max(value = 300)
	private int timeout;

	
	@NotEmpty
	private String id;
	
	@NotEmpty
	private String password;
	
	
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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getMeasureMode() {
		return measureMode;
	}
	
	public void setMeasureMode(String measureMode) {
		this.measureMode = measureMode;
	}
	
	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
