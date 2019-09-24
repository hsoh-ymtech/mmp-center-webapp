package net.mmp.center.webapp.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

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

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}




	public String getMeshId() {
		return meshId;
	}

	public void setMeshId(String meshId) {
		this.meshId = meshId;
	}

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
	
	
	public int getReflectorId() {
		return reflectorId;
	}
	public void setReflectorId(int reflectorId) {
		this.reflectorId = reflectorId;
	}
	public String getReflectorIp() {
		return reflectorIp;
	}
	public void setReflectorIp(String reflectorIp) {
		this.reflectorIp = reflectorIp;
	}
	
	public ProtocolDTO getProtocol() {
		return protocol;
	}
	public void setProtocol(ProtocolDTO protocol) {
		this.protocol = protocol;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLng() {
		return lng;
	}
	public void setLng(float lng) {
		this.lng = lng;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public static class ReflectorInfoSearchDTO {
		
		private String reflectorIp;
		private String protocol;
		
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
	}
}
