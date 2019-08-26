package net.mmp.center.webapp.service;

import net.mmp.center.webapp.domain.CurrentStatus;
import net.mmp.center.webapp.dto.CurrentStatusDTO;
import net.mmp.center.webapp.dto.CurrentStatusDTO.CurrentStatusResultDTO;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface CurrentStatusService {

	/**
	 * 품질 측정 Data 등록
	 * @param currentStatusDTO
	 * 											currentStatus DTO
	 * @return
	 * 											등록 결과
	 */
	int QualityMeasureRegister(CurrentStatusDTO currentStatusDTO);
	
	/**
	 * 품질 측정 프로세스 실행
	 * @param saveData
	 * 											측정 시작 Data
	 * @return
	 * 											실행 결과
	 */
//	int QualityMeasureProcessRun(CurrentStatus saveData);
	
	/**
	 * 품질 측정 중지
	 * @param sessId
	 * 											Session ID
	 * @return
	 * 											중지 결과
	 */
//	int QualityMeasureStop(int sessId);
	
	/**
	 * 품질 측정 조회
	 * @param pageable
	 * 											Pageable
	 * @return
	 * 											조회 결과 Data
	 */
	PageImpl<CurrentStatusDTO> currentStatusList(Pageable pageable);
	
	
	/**
	 * Process ID 등록
	 * @param sessId
	 * 											Session ID
	 * @param currentStatusDTO
	 * 											Reflector DTO
	 * @return
	 * 											등록 결과
	 */
	int measurementComplete(CurrentStatusResultDTO currentStatusResultDTO);
}
