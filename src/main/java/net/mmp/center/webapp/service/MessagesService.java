package net.mmp.center.webapp.service;

import javax.servlet.http.HttpServletResponse;

public interface MessagesService {
	
	/**
	 * Message GET
	 * @param message
	 * 									Message
	 * @param response
	 * 									Response Data
	 * @return
	 * 									Message
	 */
	public String get(String message, HttpServletResponse response);
}
