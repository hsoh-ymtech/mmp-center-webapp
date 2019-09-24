package net.mmp.center.webapp.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class ProtocolInfo {

	public ProtocolInfo() {}

	public ProtocolInfo(int protocolId, String type) {
		super();
		this.protocolId = protocolId;
		this.type = type;
	}

	@Id
	@Column(name = "protocol_id")
	private int protocolId;

	@Column(name = "type")
	private String type;
	
}
