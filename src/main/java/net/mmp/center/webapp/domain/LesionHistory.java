package net.mmp.center.webapp.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lesion_history")
@Data
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
}
