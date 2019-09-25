package net.mmp.center.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.mmp.center.webapp.domain.ProtocolInfo;
import net.mmp.center.webapp.domain.ReflectorInfo;
import net.mmp.center.webapp.dto.ReflectorInfoDTO;
import net.mmp.center.webapp.dto.ReflectorInfoDTO.ReflectorInfoSearchDTO;
import net.mmp.center.webapp.exception.NotFoundException;
import net.mmp.center.webapp.repository.ProtocolInfoRepository;
import net.mmp.center.webapp.repository.ReflectorInfoRepository;
import net.mmp.center.webapp.service.ReflectorService;
import net.mmp.center.webapp.util.Util;

@SuppressWarnings("deprecation")
@Service(ReflectorServiceImpl.BEAN_QUALIFIER)
@Transactional
public class ReflectorServiceImpl implements ReflectorService {
	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.ReflectorManagementServiceImpl";

	private static final Logger logger = LogManager.getLogger(ReflectorServiceImpl.class);

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ReflectorInfoRepository reflectorInfoRepository;

	@Autowired
	private ProtocolInfoRepository prtoocolInfoRepository;

	public final static int RESULT_OK = 1;
	public final static int RESULT_FAIL = 0;

	/*
	신규 등록 없으면 업데이트
	 */
	public int reflectorSave(ReflectorInfoDTO reflectorInfoDTO) {

		ReflectorInfo reflectorInfoDB = new ReflectorInfo();

		ProtocolInfo protocolData = new ProtocolInfo();
		ReflectorInfo reflectorData = new ReflectorInfo();

		Boolean enabled = reflectorInfoDTO.getEnabled();

		// enable 값을 true로 변경하는 경우 같은 IP를 갖고 있는 다른 Reflector 들의 enable 값을 false로 변경
		if (enabled != null && enabled.booleanValue() == true) {
			List<ReflectorInfo> sameIpReflector = reflectorInfoRepository
					.findByReflectorIp(reflectorInfoDTO.getReflectorIp());
			if (sameIpReflector != null && !sameIpReflector.isEmpty()) {
				for (ReflectorInfo reflector : sameIpReflector) {
					reflector.setEnabled(Boolean.FALSE);
					reflectorInfoRepository.save(reflector);
				}
			}
		}
		
		List<ReflectorInfo> flist = reflectorInfoRepository.findByMeshId(reflectorInfoDTO.getMeshId());
		protocolData = prtoocolInfoRepository
				.findByType(reflectorInfoDTO.getProtocol().getType() == null ? "Light TWAMP"
						: reflectorInfoDTO.getProtocol().getType());

		if (protocolData == null) {
			throw new NotFoundException("Not found Protocol Type = " + reflectorInfoDTO.getProtocol());
		}

		if (flist.size() > 0) {
			reflectorInfoDB.setCountry(flist.get(0).getCountry());
			reflectorInfoDB.setReflectorId(flist.get(0).getReflectorId());
		} else {
			reflectorInfoDB.setCountry("00");
		}
		reflectorInfoDB.setReflectorIp(reflectorInfoDTO.getReflectorIp());
		reflectorInfoDB.setPort(reflectorInfoDTO.getPort());
		reflectorInfoDB.setProtocolInfo(protocolData);
		reflectorInfoDB.setLat(reflectorInfoDTO.getLat());
		reflectorInfoDB.setLng(reflectorInfoDTO.getLng());
		reflectorInfoDB.setAddress(reflectorInfoDTO.getAddress());
		reflectorInfoDB.setMeshId(reflectorInfoDTO.getMeshId());
		reflectorInfoDB.setOs(reflectorInfoDTO.getOs() == null ? "00" : reflectorInfoDTO.getOs());
		reflectorInfoDB.setOsVersion(reflectorInfoDTO.getOsVersion() == null ? "00" : reflectorInfoDTO.getOsVersion());
		reflectorInfoDB.setMacAddress(reflectorInfoDTO.getMacAddress() == null ? "00" : reflectorInfoDTO.getMacAddress());
		reflectorInfoDB.setOutboundIpAddress(reflectorInfoDTO.getOutboundIpAddress() == null ? "00" : reflectorInfoDTO.getOutboundIpAddress());
		reflectorInfoDB.setEnabled(reflectorInfoDTO.getEnabled() == null ? Boolean.FALSE : reflectorInfoDTO.getEnabled());

		reflectorData = reflectorInfoRepository.save(reflectorInfoDB);

		if (reflectorData == null) {
			return RESULT_FAIL;
		}
		
		logger.info("Reflector 저장 성공");
		return RESULT_OK;
	}

	public PageImpl<ReflectorInfoDTO> reflectorsListPageable(Pageable pageable, ReflectorInfoSearchDTO reflectorInfoSearchDTO) {
		
		PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

		Specifications<ReflectorInfo> spec = Specifications.where(null);

		if (Util.checkNullStr(reflectorInfoSearchDTO.getReflectorIp())) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("reflectorIp"), reflectorInfoSearchDTO.getReflectorIp()));
		}
		
		if (Util.checkNullStr(reflectorInfoSearchDTO.getProtocol()) && !"all".equalsIgnoreCase(reflectorInfoSearchDTO.getProtocol())) {
			spec = spec.and((root, query, cb) -> cb.equal(root.join("protocolInfo").get("type"), reflectorInfoSearchDTO.getProtocol()));
		}
		if (pageable.getPageSize() == 2000) {
			pageRequest = new PageRequest(pageable.getPageNumber(), 1000000000, pageable.getSort());
		}
		Page<ReflectorInfo> reflData = reflectorInfoRepository.findAll(spec, pageRequest);
		
		if (reflData.getContent().isEmpty()) {
			throw new NotFoundException("Not found pageable By Reflector Info = " + pageRequest);
		}
		
		logger.info("Total Elements = " + reflData.getTotalElements());
		
		List<ReflectorInfoDTO> resultData = new ArrayList<>();
		
		resultData = reflData.getContent().stream()
				.map(data -> modelMapper.map(data, ReflectorInfoDTO.class))
				.collect(Collectors.toList());

		PageImpl<ReflectorInfoDTO> resultConvert = new PageImpl<>(resultData, pageRequest, reflData.getTotalElements());
		
		logger.info("Reflector 조회 성공");
		return resultConvert;
		
	}

	@SuppressWarnings("unchecked")
	public List<ReflectorInfo> reflectorsList() {
		List<ReflectorInfo> reflData = reflectorInfoRepository.findAll();
		return reflData;
	}

	@SuppressWarnings("unchecked")
	public JSONObject reflectorsIPList(HttpServletRequest request, HttpServletResponse response) {
		
		String addr = request.getRemoteAddr();
		String port = request.getParameter("port");
//		String defaultPort = "862";
		logger.info("Reflector IP List를 요청한 IP : " + addr);
		List<ReflectorInfo> reflData = reflectorInfoRepository.findAll();
		JSONObject result = new JSONObject();
		List<String> arr = new ArrayList<>();
		if (reflData.isEmpty()) {
			throw new NotFoundException("Reflector Data가 없습니다.");
		} else {
			for (int a = 0; a < reflData.size(); a++) {
				if (!(addr + ":" + port).equals(reflData.get(a).getReflectorIp() + ":" + String.valueOf(reflData.get(a).getPort()))) {
					arr.add(reflData.get(a).getReflectorIp() + ":" + reflData.get(a).getPort());
				}
			}
		}
		result.put("result", arr);
		result.put("clientip", addr);
		return result;
	}
	/*
	public int reflectorChange(ReflectorInfoDTO reflectorInfoDTO) {

		ProtocolInfo protocolData = new ProtocolInfo();

		Optional<ReflectorInfo> reflectorData = reflectorInfoRepository.findById(reflectorInfoDTO.getReflectorId());
		if (reflectorData == null) {
			throw new NotFoundException("Not Found Reflector Id = " + reflectorInfoDTO.getReflectorId());
		}
		List<ReflectorInfo> existCheckData = reflectorInfoRepository.findByReflectorIpAndPortAndProtocolInfo_Type(
				reflectorInfoDTO.getReflectorIp(), reflectorInfoDTO.getPort(), reflectorInfoDTO.getProtocol().getType());
		if ((existCheckData != null && existCheckData.size() > 1) || (existCheckData.size() == 1 && existCheckData.get(0).getReflectorId() != reflectorData.get().getReflectorId())) {
			throw new AlreadyExistException("Reflector Info is Exist !! = IP : " + reflectorInfoDTO.getProtocol()
					+ ", PORT : " + reflectorInfoDTO.getPort() + ", PROTOCOL : " + reflectorInfoDTO.getProtocol());
		}
		protocolData = prtoocolInfoRepository.findByType(reflectorInfoDTO.getProtocol().getType());
		if (protocolData == null) {
			throw new NotFoundException("Not found Protocol Type = " + reflectorInfoDTO.getProtocol());
		}
		reflectorData.get().setReflectorId(reflectorInfoDTO.getReflectorId());
		reflectorData.get().setReflectorIp(reflectorInfoDTO.getReflectorIp());
		reflectorData.get().setPort(reflectorInfoDTO.getPort());
		reflectorData.get().setProtocolInfo(protocolData);
		reflectorData.get().setLat(reflectorInfoDTO.getLat());
		reflectorData.get().setLng(reflectorInfoDTO.getLng());
		reflectorData.get().setAddress(reflectorInfoDTO.getAddress());
		ReflectorInfo reflSaveResult = reflectorInfoRepository.save(reflectorData.get());

		if (reflSaveResult == null) {
			return RESULT_FAIL;
		}
		
		logger.info("Reflector 수정 성공");
		return RESULT_OK;
	}
	*/
	public int reflectorDelete(int reflectorId) {
		Optional<ReflectorInfo> reflector = reflectorInfoRepository.findById(reflectorId);
		if (reflector == null) {
			throw new NotFoundException("Not found Reflector Id = " + reflectorId);
		}
		reflectorInfoRepository.deleteById(reflectorId);

		int result = (reflectorInfoRepository.findById(reflectorId) == null ? RESULT_OK : RESULT_FAIL);
		logger.info("Reflector 삭제 성공");
		return result;
	}
	
	public ReflectorInfo getRequestReflectorInfo(String meshId) {
		List<ReflectorInfo> reflectorList = reflectorInfoRepository.findByMeshId(meshId);
		if (reflectorList != null && !reflectorList.isEmpty()) {
			return reflectorList.get(0);
		}
		
		return null;
	}
}
