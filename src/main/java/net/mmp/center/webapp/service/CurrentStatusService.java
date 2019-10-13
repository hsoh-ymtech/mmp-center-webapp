package net.mmp.center.webapp.service;

import net.mmp.center.webapp.dto.CurrentStatusDTO;
import net.mmp.center.webapp.model.ESData;

public interface CurrentStatusService {

	/**
	 * 품질 측정 Data 등록
	 * @param currentStatusDTO
	 * 											currentStatus DTO
	 * @return
	 * 											등록 결과
	 */
	ESData QualityMeasureRegister(CurrentStatusDTO currentStatusDTO);
}
