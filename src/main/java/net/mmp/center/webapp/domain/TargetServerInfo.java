package net.mmp.center.webapp.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class TargetServerInfo implements Serializable {
	private static final long serialVersionUID = -2342369765995922579L;

	private String targetIP;
	private int targetPort;
	private String userName;
	private String password;
	private String command;
	private int timeout;
}
