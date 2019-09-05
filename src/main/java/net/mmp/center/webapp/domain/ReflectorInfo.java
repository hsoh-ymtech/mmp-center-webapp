package net.mmp.center.webapp.domain;

import lombok.Data;

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
@Data
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

/*	@Column(name = "country")
	private String country;
*/
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinTable(name = "reflector_protocol_relationship", joinColumns = @JoinColumn(name = "reflector_id", referencedColumnName = "reflector_id"), inverseJoinColumns = @JoinColumn(name = "protocol_id", referencedColumnName = "protocol_id"))
	private ProtocolInfo  protocolInfo;

}
