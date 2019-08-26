package net.mmp.center.webapp.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "quality_history")
public class QualityHistory {

	@Id
	@Column(name = "sess_id")
	private int sessId;

	@Column(name = "sender_ip")
	private String senderIp;

	@Column(name = "reflector_ip")
	private String reflectorIp;

	@Column(name = "send_count")
	private int sendCount;

	@Column(name = "repeat_count")
	private int repeatCount;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "complete_time")
	private LocalDateTime completeTime;

	@Column(name = "measure_protocol")
	private String measureProtocol;

	@Column(name = "measure_mode")
	private String measureMode;

	@Column(name = "sender_port")
	private int senderPort;
	
	@Column(name = "reflector_port")
	private int reflectorPort;
	
	@Column(name = "measure_result")
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
