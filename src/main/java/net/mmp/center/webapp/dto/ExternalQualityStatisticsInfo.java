package net.mmp.center.webapp.dto;

import lombok.Data;

@Data
public class ExternalQualityStatisticsInfo {
	private String time;
	private int lost_packets;
	private int duplicate_packets;
	private int outoforder_packets;
	private float delay;
	private float pdv;
	private float ipdv;
	private float inter_delay;
	private float bandwidth;
}
