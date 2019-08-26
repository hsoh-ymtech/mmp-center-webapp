package net.mmp.center.webapp.model;

import java.util.Date;

public class ResponseData {

	private final static int SUCCESS_CODE = 0x00;
	private final static int FAILED_CODE = 0x01;
	
	private String message;
	private int type = this.FAILED_CODE;
	private Object result;
	private Object searchData = null;
	private Date time = new Date();
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public Object getSearchData() {
		return searchData;
	}
	public void setSearchData(Object searchData) {
		this.searchData = searchData;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
}
