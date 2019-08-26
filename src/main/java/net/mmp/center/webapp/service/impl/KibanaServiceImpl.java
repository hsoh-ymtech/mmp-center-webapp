package net.mmp.center.webapp.service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import net.mmp.center.webapp.domain.KibanaInfo;
import net.mmp.center.webapp.dto.KibanaInfoDTO;
import net.mmp.center.webapp.dto.WebSocketClientDTO;
import net.mmp.center.webapp.dto.KibanaInfoDTO.AddKibanaInfoDTO;
import net.mmp.center.webapp.exception.NotFoundException;
import net.mmp.center.webapp.repository.KibanaInfoRepository;
import net.mmp.center.webapp.service.KibanaService;
import net.mmp.center.webapp.util.ProcessRun;
import net.mmp.center.webapp.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(KibanaServiceImpl.BEAN_QUALIFIER)
public class KibanaServiceImpl implements KibanaService{
	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.KibanaServiceImpl";
	
	private static final Logger logger = LogManager.getLogger(KibanaServiceImpl.class);
	
	private static String directory = System.getProperty("user.dir") + File.separator + "src" + File.separator
			+ "twamp-visualization";
	
	public final static int RESULT_OK = 1;
	public final static int RESULT_FAIL = 0;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private KibanaInfoRepository kibanaInfoRepository;
	
	@Transactional
	public int RegisterOrChangeKibanaInfo(AddKibanaInfoDTO addkibanaInfoDTO) {
		
		KibanaInfo kdata = new KibanaInfo();
		
		/**
		 * Kibana url 생성 - Kibana Version 6.3.1 기준 입니다.
		 */
		
		String frontHttp = "http://";
		String nearPort = "/app/kibana#/dashboard/";
		String dashboardId = "twamp_empty";
		String dashboardTitle = "twamp-empty";
		String behindFilter = "?_g=()&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!t,useMargins:!t),panels:!(),query:(language:lucene,query:''),timeRestore:!f,title:" + dashboardTitle + ",viewMode:view)";
		
		String url = frontHttp + addkibanaInfoDTO.getHost() + ":" + addkibanaInfoDTO.getKport() + nearPort + dashboardId + behindFilter;
		/**
		 * Kibana url 생성 - Kibana Version 6.3.1 기준 입니다.
		 */
		
		kdata.setHost(addkibanaInfoDTO.getHost());
		kdata.setEport(addkibanaInfoDTO.getEport());
		kdata.setKport(addkibanaInfoDTO.getKport());
		kdata.setUrl(url);
		// data.setUrl(kibanaInfoDTO.getUrl());
		kibanaInfoRepository.deleteAll();
		return (kibanaInfoRepository.save(kdata) != null ? RESULT_OK : RESULT_FAIL);
	}
	
	public int addKibanaDashboard(SimpMessagingTemplate template, WebSocketClientDTO webSocketClientDTO) {
		
		List<KibanaInfo> data = kibanaInfoRepository.findAll();
		if (data.isEmpty()) {
			throw new net.mmp.center.webapp.exception.NullPointerException("먼저 Host, Port를 저장해 주세요.");
		}
		String dir = Util.convertDirectory(directory);
		
		List<String> command = Arrays.asList("-H", data.get(0).getHost(), "-P", Integer.toString(data.get(0).getEport()));

		webSocketClientDTO.setType("dashboard");
		Thread t = new Thread(new ProcessRun(Util.convertFileNameFromBinByOS(command, "addDashboard.sh"), dir, template, webSocketClientDTO));
		t.start();
		
		return RESULT_OK;
	}
	
	public int addKibanaVisualization(SimpMessagingTemplate template, WebSocketClientDTO webSocketClientDTO) {
		
		List<KibanaInfo> data = kibanaInfoRepository.findAll();
		if (data.isEmpty()) {
			throw new net.mmp.center.webapp.exception.NullPointerException("먼저 Host, Port를 저장해 주세요.");
		}
		String dir = Util.convertDirectory(directory);
		
		List<String> command = Arrays.asList("-H", data.get(0).getHost(), "-P", Integer.toString(data.get(0).getEport()));
		
		webSocketClientDTO.setType("visualization");
		Thread t = new Thread(new ProcessRun(Util.convertFileNameFromBinByOS(command, "addVisualization.sh"), dir, template, webSocketClientDTO));
		t.start();
		
		return RESULT_OK;
	}

	public PageImpl<KibanaInfoDTO> getListKibanaInfo(Pageable pageable) {
		
		PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		
		Page<KibanaInfo> findData = kibanaInfoRepository.findAll(pageRequest);
		
		if (findData.getContent().isEmpty()) {
			throw new NotFoundException("Not found pageable By Kibana Info = " + pageRequest);
		}
		
		List<KibanaInfoDTO> convertData = findData.getContent().stream()
				.map(data -> modelMapper.map(data, KibanaInfoDTO.class))
				.collect(Collectors.toList());
		
		PageImpl<KibanaInfoDTO> resultConvert = new PageImpl<>(convertData, pageRequest, findData.getTotalElements());
		
		logger.info("Kibana URL 조회 성공");
		return resultConvert;
	}
}
