package net.mmp.center.webapp.domain;

import lombok.Data;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "quality_history")
@Data
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

}
