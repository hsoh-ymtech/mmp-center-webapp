package net.mmp.center.webapp.dto;

public class WebSocketClientDTO {
	
	private String id;
	private String message;
	private String type;
	private int reflectorTotalNum;
	private int reflectorCompleteNum;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getReflectorTotalNum() {
		return reflectorTotalNum;
	}
	public void setReflectorTotalNum(int reflectorTotalNum) {
		this.reflectorTotalNum = reflectorTotalNum;
	}
	public int getReflectorCompleteNum() {
		return reflectorCompleteNum;
	}
	public void setReflectorCompleteNum(int reflectorCompleteNum) {
		this.reflectorCompleteNum = reflectorCompleteNum;
	}
}
