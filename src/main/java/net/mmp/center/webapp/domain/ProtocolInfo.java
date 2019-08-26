package net.mmp.center.webapp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
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

	public int getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(int protocolId) {
		this.protocolId = protocolId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
