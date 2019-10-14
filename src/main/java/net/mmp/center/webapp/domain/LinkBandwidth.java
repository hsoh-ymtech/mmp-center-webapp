package net.mmp.center.webapp.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "link_bandwidth")
@Data
public class LinkBandwidth {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)//기본키 생성을 db에 위임
	private Integer id;

	@Column(name = "mesh_id")
	private String meshId;

	@Column(name = "measured_time")
	private LocalDateTime measuredTime;

	@Column(name = "src_ip_address")
	private String srcIpAddress;

	@Column(name = "dest_ip_address")
	private String destIpAddress;

	@Column(name = "bandwidth")
	private float bandwidth;
	
	@Column(name = "upload_bandwidth")
	private float uploadBandwidth;
	
	@Column(name = "download_bandwidth")
	private float downloadBandwidth;
}