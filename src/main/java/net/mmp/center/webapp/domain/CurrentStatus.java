package net.mmp.center.webapp.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "current_status")
public class CurrentStatus {

	@Id
	@Column(name = "sess_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)//기본키 생성을 db에 위임
	private int sessId;

	@Column(name = "sender_ip")
	private String senderIp;

	@Column(name = "sender_port")
	private int senderPort;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "reflector_id", nullable = false)
	private ReflectorInfo reflectorInfo;

	@ManyToOne(optional = false)
	@JoinColumn(name = "protocol_id", nullable = false)
	private ProtocolInfo protocolInfo;

	@Column(name = "measure_mode")
	private String measureMode;

	@Column(name = "send_count")
	private int sendCount;

	@Column(name = "repeat_count")
	private int repeatCount;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "pid")
	private int pid;

	@Column(name = "timeout")
	private int timeout;

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

	public int getSenderPort() {
		return senderPort;
	}

	public void setSenderPort(int senderPort) {
		this.senderPort = senderPort;
	}

	public ReflectorInfo getReflectorInfo() {
		return reflectorInfo;
	}

	public void setReflectorInfo(ReflectorInfo reflectorInfo) {
		this.reflectorInfo = reflectorInfo;
	}

	
	public ProtocolInfo getProtocolInfo() {
		return protocolInfo;
	}

	public void setProtocolInfo(ProtocolInfo protocolInfo) {
		this.protocolInfo = protocolInfo;
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

	public String getMeasureMode() {
		return measureMode;
	}

	public void setMeasureMode(String measureMode) {
		this.measureMode = measureMode;
	}
	
	
}
