package net.mmp.center.webapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LinkLossRateDTO {
	private String meshId;

	private String srcIpAddress;

	private String destIpAddress;

	private Integer totalLossPacketCount;

	private Integer totalMeasuredPacketCount;

	private Float lossRate;
}