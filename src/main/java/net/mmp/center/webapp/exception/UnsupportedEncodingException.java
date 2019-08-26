package net.mmp.center.webapp.exception;

@SuppressWarnings("serial")
public class UnsupportedEncodingException extends RuntimeException{
	
	private String information;
	
	public String getInformation() {
		return information;
	}
	
	public void setInformation(String information) {
		this.information = information;
	}
	
	public UnsupportedEncodingException(String fieldError) {
		super();
		this.information = fieldError;
	}
}
