package net.mmp.center.webapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
public class LinkBandwidthDTO {
	private Integer id;

	private String meshId;

	private LocalDateTime measuredTime;

	private String srcIpAddress;

	private String destIpAddress;

	private float bandwidth;
}