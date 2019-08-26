package net.mmp.center.webapp.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;

@Entity
public class ReflectorInfo {

	@Id
	@Column(name = "reflector_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)//기본키 생성을 db에 위임
	private int reflectorId;
	
	@Column(name = "reflector_ip")
	private String reflectorIp;
	
	@Column(name = "port")
	private int port;
	
	@Column(name = "lat")
	private float lat;
	
	@Column(name = "lng")
	private float lng;
	
	@Column(name = "address")
	private String address;
	
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinTable(name = "reflector_protocol_relationship", joinColumns = @JoinColumn(name = "reflector_id", referencedColumnName = "reflector_id"), inverseJoinColumns = @JoinColumn(name = "protocol_id", referencedColumnName = "protocol_id"))
	private ProtocolInfo  protocolInfo;
	
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

	public ProtocolInfo getProtocolInfo() {
		return protocolInfo;
	}

	public void setProtocolInfo(ProtocolInfo protocolInfo) {
		this.protocolInfo = protocolInfo;
	}
}
