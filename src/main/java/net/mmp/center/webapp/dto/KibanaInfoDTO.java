package net.mmp.center.webapp.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Data
public class KibanaInfoDTO {

	@Data
	public static class AddKibanaInfoDTO {
		@NotBlank
		private String host;
		@NotNull
		private int eport;
		@NotNull
		private int kport;
	}
	
	private int kibanaId;
	
	@URL
	private String url;
	private String host;
	private int eport;
	private int kport;
	
}
