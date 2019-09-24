package net.mmp.center.webapp.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "kibana_info")
@Data
public class KibanaInfo {

	@Id
	@Column(name = "kibana_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)//기본키 생성을 db에 위임
	private int kibanaId;

	@Column(name = "host")
	private String host;

	@Column(name = "elastic_port")
	private int eport;

	@Column(name = "kibana_port")
	private int kport;

	@Column(name = "url")
	private String url;
}
