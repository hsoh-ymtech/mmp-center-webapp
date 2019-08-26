package net.mmp.center.webapp.service;

import net.mmp.center.webapp.dto.BigdataAnalysisDTO;
import net.mmp.center.webapp.dto.BigdataAnalysisDTO.BigdataAnalysisResultDTO;
import org.springframework.data.domain.Pageable;

public interface BigdataAnalysisService {

	BigdataAnalysisResultDTO AnalysisElasticSearchData(BigdataAnalysisDTO bigdataAnalysisDTO, Pageable pageable);
	
}
