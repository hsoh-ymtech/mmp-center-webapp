package net.mmp.center.webapp.exception;

@SuppressWarnings("serial")
public class JsonProcessingException extends RuntimeException{
	
	private String information;
	
	public String getInformation() {
		return information;
	}
	
	public void setInformation(String information) {
		this.information = information;
	}
	
	public JsonProcessingException(String fieldError) {
		super();
		this.information = fieldError;
	}
}
