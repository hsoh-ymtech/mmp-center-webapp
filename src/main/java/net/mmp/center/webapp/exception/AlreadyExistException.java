package net.mmp.center.webapp.exception;

@SuppressWarnings("serial")
public class AlreadyExistException extends RuntimeException{
	
	private String information;
	
	public String getInformation() {
		return information;
	}
	
	public void setInformation(String information) {
		this.information = information;
	}
	
	public AlreadyExistException(String fieldError) {
		super();
		this.information = fieldError;
	}
}
