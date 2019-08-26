package net.mmp.center.webapp.exception;

@SuppressWarnings("serial")
public class BadValidationException extends RuntimeException{
	
	private Object information;
	
	public Object getInformation() {
		return information;
	}
	
	public void setInformation(Object information) {
		this.information = information;
	}
	
	public BadValidationException(Object fieldError) {
		super();
		this.information = fieldError;
	}
}
