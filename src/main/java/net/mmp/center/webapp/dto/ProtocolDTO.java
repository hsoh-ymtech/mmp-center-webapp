package net.mmp.center.webapp.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class ProtocolDTO {
	
	@NotBlank
	private String type;
	public ProtocolDTO(String type) {
		this.type = type;
	}
}
