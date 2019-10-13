package net.mmp.center.webapp.dto;

import lombok.Data;

@Data
public class CurrentStatusDTO {
	
	public CurrentStatusDTO() {}
	
	public CurrentStatusDTO(String senderIp, String reflectorIp, int sendCount, String protocol, String measureMode) {
		super();
		this.senderIp = senderIp;
		this.reflectorIp = reflectorIp;
		this.sendCount = sendCount;
		this.protocol = protocol;
		this.measureMode = measureMode;
	}
	

	/**
	 * Sender IP Address
	 */
	private String senderIp;
	
	/**
	 * Reflector Ip Address
	 */
	private String reflectorIp;
	
	/**
	 * 1회 측정 시 송신하는 Packet 수
	 */
	private int sendCount;
	
	/**
	 * protocol
	 */
	private String protocol;

	/**
	 * measure Mode
	 */
	private String measureMode;
}
