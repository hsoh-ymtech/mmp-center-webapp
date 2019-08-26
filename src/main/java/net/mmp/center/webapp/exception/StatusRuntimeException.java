package net.mmp.center.webapp.exception;

@SuppressWarnings("serial")
public class StatusRuntimeException extends RuntimeException{
	
	private String information;
	
	public String getInformation() {
		return information;
	}
	
	public void setInformation(String information) {
		this.information = information;
	}
	
	public StatusRuntimeException(String information) {
		super();
		this.information = information;
	}
}