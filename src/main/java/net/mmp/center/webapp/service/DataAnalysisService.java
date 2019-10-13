package net.mmp.center.webapp.service;

import net.mmp.center.webapp.dto.DataAnalysisDTO;
import net.mmp.center.webapp.dto.DataAnalysisDTO.DataataAnalysisResultDTO;
import org.springframework.data.domain.Pageable;

public interface DataAnalysisService {

	DataataAnalysisResultDTO AnalysisElasticSearchData(DataAnalysisDTO dataAnalysisDTO, Pageable pageable);
	
}
