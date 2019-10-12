package net.mmp.center.webapp.dto;

import lombok.Data;

@Data
public class WebSocketClientDTO {
	
	private String id;
	private String message;
	private String type;
	private int reflectorTotalNum;
	private int reflectorCompleteNum;

}
