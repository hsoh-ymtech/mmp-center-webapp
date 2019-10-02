package net.mmp.center.webapp.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class ReflectorInfoDTO {
	
	/**
	 * Reflector ID
	 */
	@NotNull
	private Integer reflectorId;
	
	/**
	 * Reflector IP Address
	 */
	@NotBlank
	private String reflectorIp;
	
	/**
	 * 프로토콜 종류
	 */
	private ProtocolDTO protocol;
	
	/**
	 * Lat 
	 */
	private float lat;
	
	/**
	 * Lng
	 */
	private float lng;
	
	/**
	 * Address
	 */
	private String address;

	/**
	 * meshId
	 */
	private String meshId;

	private String os;

	private String osVersion;

	private String osArch;
	
	private String mac;
	
	private String country;

	private Boolean enabled;

	private String macAddress;

	private String outboundIpAddress;

	/**
	 * Reflector Port
	 * 	
	 * Registered port = 1024 ~ 49151
	 * 
	 */
	@NotNull
	@Max(value = 65535)
	@Min(value = 1)
	private int port;
	private boolean alive;

	public static class ReflectorInfoSearchDTO {
		
		private String reflectorIp;
		private String protocol;
		private int alive;
		
		public String getReflectorIp() {
			return reflectorIp;
		}
		public void setReflectorIp(String reflectorIp) {
			this.reflectorIp = reflectorIp;
		}
		public String getProtocol() {
			return protocol;
		}
		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
		public int getAlive() {
			return alive;
		}
		public void setAlive(int alive) {
			this.alive = alive;
		}
	}
}
