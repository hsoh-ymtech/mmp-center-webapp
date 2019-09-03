package net.mmp.center.webapp.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.mmp.center.webapp.domain.ReflectorInfo;
import org.json.simple.JSONObject;
import net.mmp.center.webapp.dto.ReflectorInfoDTO;
import net.mmp.center.webapp.dto.ReflectorInfoDTO.ReflectorInfoSearchDTO;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReflectorService {
	
	/**
	 * Reflector 등록
	 * @param reflectorInfoDTO
	 * 										Reflector DTO
	 * @return
	 * 										등록 결과
	 */
	int reflectorRegister(ReflectorInfoDTO reflectorInfoDTO);
	
	/**
	 * Reflector 조회
	 * @param pageable
	 * 										Paging 정보
	 * @return
	 * 										Reflectors Data
	 */
	PageImpl<ReflectorInfoDTO> reflectorsListPageable(Pageable pageable, ReflectorInfoSearchDTO reflectorInfoSearchDTO);

	/**
	 * Reflector 조회
	 * @return
	 * 										Reflectors Data
	 */
	List<ReflectorInfo> reflectorsList();

	/**
	 * Reflector - IP 리스트 조회
	 * @param request
	 * @param response
	 * @return
	 */
	JSONObject reflectorsIPList(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Reflector 수정
	 * @param reflectorInfoDTO
	 * 										Reflector DTO
	 * @return
	 * 										수정 결과
	 */
	int reflectorChange(ReflectorInfoDTO reflectorInfoDTO);
	
	/**
	 * Reflector 삭제
	 * @param reflectorInfoDTO
	 * 										Reflector DTO
	 * @return
	 * 										삭제 결과
	 */
	int reflectorDelete(int reflectorId);
}
