package net.mmp.center.webapp.controller;

import net.mmp.center.webapp.dto.WebSocketClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@MessageMapping("initclientid")
	public void onReceivedMessage(WebSocketClientDTO webSocketClientDTO) {
		
		this.template.convertAndSend("/connect", webSocketClientDTO.getMessage());
	}
}
