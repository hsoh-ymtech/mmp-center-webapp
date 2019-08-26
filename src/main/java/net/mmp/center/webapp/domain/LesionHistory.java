package net.mmp.center.webapp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lesion_history")
public class LesionHistory {

	@Id
	@Column(name = "lh_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)//기본키 생성을 db에 위임
	private int lhId;
	
	@Column(name = "sess_id")
	private int sessId;
	
	@Column(name = "start_time")
	private String startTime;
	
	@Column(name = "complete_time")
	private String completeTime;
	
	@Column(name = "sender_ip")
	private String senderIp;
	
	@Column(name = "reflector_ip")
	private String reflectorIp;
	
	@Column(name = "lesion_code")
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
}
