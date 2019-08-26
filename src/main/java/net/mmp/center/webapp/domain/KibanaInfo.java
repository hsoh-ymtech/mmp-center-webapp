package net.mmp.center.webapp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "kibana_info")
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

	public int getKibanaId() {
		return kibanaId;
	}

	public void setKibanaId(int kibanaId) {
		this.kibanaId = kibanaId;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
