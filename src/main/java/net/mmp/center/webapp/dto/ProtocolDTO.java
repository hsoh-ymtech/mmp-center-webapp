package net.mmp.center.webapp.dto;

import org.hibernate.validator.constraints.NotBlank;

public class ProtocolDTO {
	
	@NotBlank
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
