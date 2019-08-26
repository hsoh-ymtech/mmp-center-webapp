package net.mmp.center.webapp.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.mmp.center.webapp.domain.CurrentStatus;
import net.mmp.center.webapp.domain.QualityHistory;
import net.mmp.center.webapp.dto.QualityHistoryDTO;
import net.mmp.center.webapp.dto.QualityHistoryDTO.QualityHistorySearchDTO;
import net.mmp.center.webapp.exception.NotFoundException;
import net.mmp.center.webapp.repository.CurrentStatusRepository;
import net.mmp.center.webapp.repository.QualityHistoryRepository;
import net.mmp.center.webapp.service.QualityHistoryService;
import net.mmp.center.webapp.util.Util;

@SuppressWarnings("deprecation")
@Service(QualityHistoryServiceImpl.BEAN_QUALIFIER)
public class QualityHistoryServiceImpl implements QualityHistoryService {
	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.QualityHistoryServiceImpl";

	private static final Logger logger = LogManager.getLogger(QualityHistoryServiceImpl.class);

	@Autowired
	private SimpMessagingTemplate template;
	
    @Autowired
    private ModelMapper modelMapper;
	
    @Autowired
	private CurrentStatusRepository currentStatusRepository;
    
	@Autowired
	private QualityHistoryRepository qualityHistoryRepository;
	
	public final static int RESULT_OK = 1;
	public final static int RESULT_FAIL = 0;

	public int qualityHistoryRegister(QualityHistoryDTO qualityHistoryDTO) {
		Optional<CurrentStatus> csData = currentStatusRepository.findById(qualityHistoryDTO.getSessId());
		
		QualityHistory data = new QualityHistory();
		data.setSessId(qualityHistoryDTO.getSessId());
		data.setSenderIp(csData.get().getSenderIp());
		data.setReflectorIp(csData.get().getReflectorInfo().getReflectorIp());
		data.setSendCount(qualityHistoryDTO.getSendCount());
		data.setRepeatCount(qualityHistoryDTO.getRepeatCount());
		data.setStartTime(qualityHistoryDTO.getStartTime());
		data.setCompleteTime(qualityHistoryDTO.getCompleteTime());
		data.setMeasureProtocol(csData.get().getProtocolInfo().getType());
		data.setMeasureMode(qualityHistoryDTO.getMeasureMode());
		data.setSenderPort(csData.get().getSenderPort());
		data.setReflectorPort(csData.get().getReflectorInfo().getPort());
		
		String action = "";
		
		if (qualityHistoryDTO.getMeasureResult().equals("-2/-2")) {
			action = "중지";
			data.setMeasureResult(action);
		} else if (qualityHistoryDTO.getMeasureResult().equals("-1/-1")) {
			LocalDateTime tmpDate = LocalDateTime.now();
			data.setStartTime(qualityHistoryDTO.getCompleteTime());
			data.setCompleteTime(tmpDate);
			action = "측정중...";
		} else if (qualityHistoryDTO.getMeasureResult().split("/")[0].equals(qualityHistoryDTO.getMeasureResult().split("/")[1])) {
			action = "성공";
		} else {
			action = "실패";
		}
		
		data.setMeasureResult(action);
		logger.info("품질 이력 - 측정 " + action + " 등록");

		/**
		 * 측정중이 아닌 모든 조건
		 */
		if (!qualityHistoryDTO.getMeasureResult().equals("-1/-1")) {
			currentStatusRepository.deleteById(qualityHistoryDTO.getSessId());
			boolean result = (currentStatusRepository.findById(qualityHistoryDTO.getSessId()) == null ? true
					: false);
			if (!result) {
				return RESULT_FAIL;
			}
		}
		if (qualityHistoryRepository.save(data) != null) {
			logger.info("품질 이력 등록 성공");
			template.convertAndSend("/dashboard/measureEnd", true);
			return RESULT_OK;
		} else {
			return RESULT_FAIL;
		}
	}

	@Transactional
	public PageImpl<QualityHistoryDTO> QualityHistoryList(Pageable pageable, QualityHistorySearchDTO qualityHistorySearchDTO) {
		
		Specifications<QualityHistory> spec = Specifications.where(null);
		
		if (Util.checkNullStr(qualityHistorySearchDTO.getSenderIp())) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("senderIp"), qualityHistorySearchDTO.getSenderIp()));
		}
		if (Util.checkNullStr(qualityHistorySearchDTO.getReflectorIp())) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("reflectorIp"), qualityHistorySearchDTO.getReflectorIp()));
		}
		PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		Page<QualityHistory> findData = qualityHistoryRepository.findAll(spec, pageRequest);
		
		if (findData.getContent().isEmpty()) {
			throw new NotFoundException("Not found pageable By Quality History = " + pageRequest);
		}
		logger.info("Total Elements = " + findData.getTotalElements());

		List<QualityHistoryDTO> resultData = new ArrayList<>();
		
		resultData = findData.getContent().stream()
				.map(data -> modelMapper.map(data, QualityHistoryDTO.class))
				.collect(Collectors.toList());

		PageImpl<QualityHistoryDTO> resultConvert = new PageImpl<>(resultData, pageRequest, findData.getTotalElements());
		
		logger.info("품질 이력 조회 성공");
//		try {
//			get();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return resultConvert;
	}
	
	public void get() throws Exception {
		URL url = new URL("http://39.119.118.191:9200/twamp/_count"); // 호출할 url
		Map<String,Object> paramsData = new LinkedHashMap<>(); // 파라미터 세팅
        paramsData.put("session_id", 10026);
        
        Map<String,Object> paramsMatch = new LinkedHashMap<>(); // 파라미터 세팅
        paramsMatch.put("match", paramsData);
        
        Map<String,Object> params = new LinkedHashMap<>(); // 파라미터 세팅
        params.put("query", paramsMatch);
        
        StringBuilder postData = new StringBuilder();
        for(Map.Entry<String,Object> param : params.entrySet()) {
            if(postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
 
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes); // POST 호출
 
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
 
        String inputLine;
        while((inputLine = in.readLine()) != null) { // response 출력
            System.out.println(inputLine);
        }
 
        in.close();
	}
}
