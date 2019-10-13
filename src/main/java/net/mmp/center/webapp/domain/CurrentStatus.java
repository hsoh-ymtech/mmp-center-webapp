package net.mmp.center.webapp.domain;

import lombok.Data;

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
@Data
public class CurrentStatus {
	@Id
	@Column(name = "sess_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)//기본키 생성을 db에 위임
	private int sessId;

	@Column(name = "sender_ip")
	private String senderIp;

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
}
