package net.mmp.center.webapp.service.impl;

import javax.servlet.http.HttpServletResponse;

import net.mmp.center.webapp.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service(MessagesImpl.BEAN_QUALIFIER)
public class MessagesImpl implements MessagesService{
	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.MessagesImpl";
	
	@Autowired
    MessageSource messageSource;
	
	@Override
	public String get(String message, HttpServletResponse response) {
		return messageSource.getMessage(message, (Object[])null, response.getLocale());
	}

}