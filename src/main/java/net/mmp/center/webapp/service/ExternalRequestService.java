package net.mmp.center.webapp.service;

import org.springframework.data.domain.Pageable;

import net.mmp.center.webapp.dto.ExternalQualityHistoryDTO;
import net.mmp.center.webapp.dto.ExternalQualityHistorySearchDTO;
import net.mmp.center.webapp.model.ESData;

public interface ExternalRequestService {
	ESData getQualityHistoryRecent(ExternalQualityHistorySearchDTO dto);
	
	ExternalQualityHistoryDTO getQualityHistoryStatistics(Pageable pageable, ExternalQualityHistorySearchDTO dto);
}
