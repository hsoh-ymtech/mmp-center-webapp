package net.mmp.center.webapp.service;

import net.mmp.center.webapp.dto.LesionHistoryDTO;
import net.mmp.center.webapp.dto.LesionHistoryDTO.LesionHistorySearchDTO;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface LesionHistoryService {

	PageImpl<LesionHistoryDTO> SearchLesionHistory(Pageable pageable, LesionHistorySearchDTO lesionHistorySearchDTO);
	
}
