package net.mmp.center.webapp.service;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import net.mmp.center.webapp.dto.QualityHistoryDTO;
import net.mmp.center.webapp.dto.QualityHistoryDTO.QualityHistorySearchDTO;
import net.mmp.center.webapp.model.ESData;

public interface QualityHistoryService {
	/**
	 * 품질 이력 등록
	 * @param qualityHistoryDTO
	 * 											품질 이력 DTO
	 * @return
	 * 											등록 결과
	 */
	int qualityHistoryRegister(QualityHistoryDTO qualityHistoryDTO);
	
	/**
	 * 품질 이력 조회
	 * @return
	 * 											조회 List
	 */
	PageImpl<ESData> QualityHistoryList(Pageable pageable, QualityHistorySearchDTO qualityHistorySearchDTO);
}
