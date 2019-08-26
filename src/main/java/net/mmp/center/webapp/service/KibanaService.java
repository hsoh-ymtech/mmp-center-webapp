package net.mmp.center.webapp.service;

import net.mmp.center.webapp.dto.KibanaInfoDTO;
import net.mmp.center.webapp.dto.WebSocketClientDTO;
import net.mmp.center.webapp.dto.KibanaInfoDTO.AddKibanaInfoDTO;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public interface KibanaService {

	int RegisterOrChangeKibanaInfo(AddKibanaInfoDTO addkibanaInfoDTO);
	
	PageImpl<KibanaInfoDTO> getListKibanaInfo(Pageable pageable);
	
	int addKibanaDashboard(SimpMessagingTemplate template, WebSocketClientDTO webSocketClientDTO);
	
	int addKibanaVisualization(SimpMessagingTemplate template, WebSocketClientDTO webSocketClientDTO);
}
