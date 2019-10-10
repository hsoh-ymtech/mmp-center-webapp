package net.mmp.center.webapp.dto;

import lombok.Data;

@Data
public class ExternalQualityHistorySearchDTO {
	private String src_host;
	private String dst_host;
	private String meshId;
}
