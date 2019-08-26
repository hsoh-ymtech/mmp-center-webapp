package net.mmp.center.webapp.domain;

import java.io.Serializable;

public class TargetServerInfo implements Serializable {
	private static final long serialVersionUID = -2342369765995922579L;

	private String targetIP;
	private int targetPort;
	private String userName;
	private String password;
	private String command;
	private int timeout;

	public String getTargetIP() {
		return targetIP;
	}

	public void setTargetIP(String targetIP) {
		this.targetIP = targetIP;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
