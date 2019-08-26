package net.mmp.center.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import net.mmp.center.webapp.domain.LesionHistory;
import net.mmp.center.webapp.dto.LesionHistoryDTO;
import net.mmp.center.webapp.dto.LesionHistoryDTO.LesionHistorySearchDTO;
import net.mmp.center.webapp.exception.NotFoundException;
import net.mmp.center.webapp.repository.LesionHistoryRepository;
import net.mmp.center.webapp.service.LesionHistoryService;
import net.mmp.center.webapp.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(LesionHistoryServiceImpl.BEAN_QUALIFIER)
public class LesionHistoryServiceImpl implements LesionHistoryService {

	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.ObstacleHistoryServiceImpl";

	private static final Logger logger = LogManager.getLogger(LesionHistoryServiceImpl.class);
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private LesionHistoryRepository lesionHistoryRepository;
	
	
	@Transactional
	public PageImpl<LesionHistoryDTO> SearchLesionHistory(Pageable pageable, LesionHistorySearchDTO lesionHistorySearchDTO) {
		
		Specifications<LesionHistory> spec = Specifications.where(null);
		
		if (Util.checkNullStr(lesionHistorySearchDTO.getSenderIp())) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("senderIp"), lesionHistorySearchDTO.getSenderIp()));
		}
		if (Util.checkNullStr(lesionHistorySearchDTO.getReflectorIp())) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("reflectorIp"), lesionHistorySearchDTO.getReflectorIp()));
		}
		PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		Page<LesionHistory> findData = lesionHistoryRepository.findAll(spec, pageRequest);
		
		if (findData.getContent().isEmpty()) {
			throw new NotFoundException("Not found pageable By Lesion History = " + pageRequest);
		}
		logger.info("Total Elements = " + findData.getTotalElements());

		List<LesionHistoryDTO> resultData = new ArrayList<>();
		
		resultData = findData.getContent().stream()
				.map(data -> modelMapper.map(data, LesionHistoryDTO.class))
				.collect(Collectors.toList());

		PageImpl<LesionHistoryDTO> resultConvert = new PageImpl<>(resultData, pageRequest, findData.getTotalElements());
		
		logger.info("장애 이력 조회 성공");
		return resultConvert;
	}
}
