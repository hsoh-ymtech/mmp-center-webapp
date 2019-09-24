package net.mmp.center.webapp.controller;

import net.mmp.center.webapp.domain.ReflectorInfo;
import net.mmp.center.webapp.dto.BootstrapInfoDTO;
import net.mmp.center.webapp.dto.ProtocolDTO;
import net.mmp.center.webapp.dto.ReflectorInfoDTO;
import net.mmp.center.webapp.service.MessagesService;
import net.mmp.center.webapp.service.ReflectorService;
import net.mmp.center.webapp.service.impl.MessagesImpl;
import net.mmp.center.webapp.service.impl.ReflectorServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
public class BootstrapController {

	private static final Logger logger = LogManager.getLogger(BootstrapController.class);

	@Autowired
	@Qualifier(ReflectorServiceImpl.BEAN_QUALIFIER)
	private ReflectorService reflectormanagementService;

	/**
	 * Bootstrap 등록
	 * 
	 * @param bootstrapInfoDTO
	 *            Bootstrap DTO
	 * @return 등록 결과S
	 */
	@RequestMapping(value = "/v1/bootstrap", method = RequestMethod.POST)
	public ResponseEntity<BootstrapInfoDTO> bootstrap(@RequestBody @Valid BootstrapInfoDTO bootstrapInfoDTO,
		final BindingResult result, HttpServletResponse request, HttpServletResponse response) {

		logger.info(bootstrapInfoDTO);

		List<ReflectorInfo> list = reflectormanagementService.reflectorsList();
		List<ReflectorInfo> flist = list.stream().filter(item -> item.getMeshId().equals(bootstrapInfoDTO.getMeshId()))
				.collect(Collectors.toList());

		if(flist.size() == 0) { // 신규 장치 등록
			ReflectorInfoDTO dto = new ReflectorInfoDTO();
			dto.setAddress("");
			dto.setLat(0.0f);
			dto.setLng(0.0f);
			dto.setPort(bootstrapInfoDTO.getPort());
			dto.setReflectorIp(bootstrapInfoDTO.getPublicIpAddress());
			dto.setMeshId(bootstrapInfoDTO.getMeshId());
			dto.setOs(bootstrapInfoDTO.getOs()==null?"00":bootstrapInfoDTO.getOs());
			dto.setOsVersion(bootstrapInfoDTO.getOsVersion()==null?"00":bootstrapInfoDTO.getOsVersion());
			dto.setMacAddress(bootstrapInfoDTO.getMacAddress()==null?"00":bootstrapInfoDTO.getMacAddress());
			dto.setOutboundIpAddress(bootstrapInfoDTO.getOutboundIpAddress()==null?"00":bootstrapInfoDTO.getOutboundIpAddress());
			dto.setEnabled(bootstrapInfoDTO.getEnabled()==null?Boolean.FALSE:bootstrapInfoDTO.getEnabled());
			ProtocolDTO protocol = new ProtocolDTO("Light TWAMP" );
			dto.setProtocol(protocol);
			int resultObj = reflectormanagementService.reflectorSave(dto);
		} else { // 등록 장치 정보 업데이트
			ReflectorInfoDTO dto = new ReflectorInfoDTO();
			dto.setReflectorId(flist.get(0).getReflectorId());
			dto.setAddress(flist.get(0).getAddress());
			dto.setLat(flist.get(0).getLat());
			dto.setLng(flist.get(0).getLng());
			dto.setPort(bootstrapInfoDTO.getPort());
			dto.setReflectorIp(bootstrapInfoDTO.getPublicIpAddress());
			dto.setMeshId(flist.get(0).getMeshId());
			dto.setOs(bootstrapInfoDTO.getOs()==null?"00":bootstrapInfoDTO.getOs());
			dto.setOsVersion(bootstrapInfoDTO.getOsVersion()==null?"00":bootstrapInfoDTO.getOsVersion());
			dto.setMacAddress(bootstrapInfoDTO.getMacAddress()==null?"00":bootstrapInfoDTO.getMacAddress());
			dto.setOutboundIpAddress(bootstrapInfoDTO.getOutboundIpAddress()==null?"00":bootstrapInfoDTO.getOutboundIpAddress());
			dto.setEnabled(flist.get(0).getEnabled());
			ProtocolDTO protocol = new ProtocolDTO(flist.get(0).getProtocolInfo().getType());
			dto.setProtocol(protocol);
			int resultObj = reflectormanagementService.reflectorSave(dto);
		}

		return new ResponseEntity<BootstrapInfoDTO>(bootstrapInfoDTO, HttpStatus.CREATED);
	}
}
