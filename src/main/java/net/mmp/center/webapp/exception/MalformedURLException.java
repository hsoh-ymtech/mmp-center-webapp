package net.mmp.center.webapp.exception;

@SuppressWarnings("serial")
public class MalformedURLException extends RuntimeException{
	
	private String information;
	
	public String getInformation() {
		return information;
	}
	
	public void setInformation(String information) {
		this.information = information;
	}
	
	public MalformedURLException(String fieldError) {
		super();
		this.information = fieldError;
	}
}
