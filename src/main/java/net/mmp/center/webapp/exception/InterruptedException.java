package net.mmp.center.webapp.exception;

@SuppressWarnings("serial")
public class InterruptedException extends RuntimeException{
	
	private String information;
	
	public String getInformation() {
		return information;
	}
	
	public void setInformation(String information) {
		this.information = information;
	}
	
	public InterruptedException(String information) {
		super();
		this.information = information;
	}
}
