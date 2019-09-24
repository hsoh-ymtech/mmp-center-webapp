package net.mmp.center.webapp.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reflector_protocol_relationship")
@Data
public class ReflectorProtocolRelationship {
	
	@Id
	@Column(name = "reflector_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)//기본키 생성을 db에 위임
	private int reflectorId;
	
	@Column(name = "protocol_id")
	private int protocolId;
}
