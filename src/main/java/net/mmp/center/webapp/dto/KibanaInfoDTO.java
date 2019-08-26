package net.mmp.center.webapp.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class KibanaInfoDTO {
	
	
	public static class AddKibanaInfoDTO {
		
		@NotBlank
		private String host;
		@NotNull
		private int eport;
		@NotNull
		private int kport;
		
		
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public int getEport() {
			return eport;
		}
		public void setEport(int eport) {
			this.eport = eport;
		}
		public int getKport() {
			return kport;
		}
		public void setKport(int kport) {
			this.kport = kport;
		}
	}
	
	private int kibanaId;
	
	@URL
	private String url;
	private String host;
	private int eport;
	private int kport;
	
	
	public int getKibanaId() {
		return kibanaId;
	}
	public void setKibanaId(int kibanaId) {
		this.kibanaId = kibanaId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getEport() {
		return eport;
	}
	public void setEport(int eport) {
		this.eport = eport;
	}
	public int getKport() {
		return kport;
	}
	public void setKport(int kport) {
		this.kport = kport;
	}
	
}
