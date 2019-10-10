package net.mmp.center.webapp.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import net.mmp.center.webapp.domain.CurrentStatus;
import net.mmp.center.webapp.domain.QualityHistory;
import net.mmp.center.webapp.dto.QualityHistoryDTO;
import net.mmp.center.webapp.dto.QualityHistoryDTO.QualityHistorySearchDTO;
import net.mmp.center.webapp.model.ESData;
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


	@Value("${elasticsearch.host}")
	private int elasticsearchHost;

	@Value("${elasticsearch.transport.port}")
	private int elasticsearchHttpPort;

	public String getUrlofElasticsearch(){
		return "http://"+elasticsearchHost+":"+elasticsearchHttpPort;
	}
	
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
	public PageImpl<ESData> QualityHistoryList(Pageable pageable, QualityHistorySearchDTO qualityHistorySearchDTO) {
		// TODO: 기존 데이터베이스가 아닌 Elasticsearch에서 데이터를 가져와서 넘겨주어야 함
		String query = createElasticsearchQuery(qualityHistorySearchDTO, pageable.getPageNumber(), pageable.getPageSize());
		StringBuffer response = new StringBuffer();
		List<ESData> resultObj = new ArrayList<ESData>();
		PageImpl<ESData> resultData = null;
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		
		try {
			URL esurl = new URL(getUrlofElasticsearch()+"/redis_test-*/_search");
			HttpURLConnection conn = (HttpURLConnection) esurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			osw.write(query);
			osw.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				response.append(line);
			}

			osw.close();
			br.close();
			conn.disconnect();
    		
			ObjectMapper mapper = new ObjectMapper();
			JsonNode arrayNode = mapper.readTree(response.toString()).get("hits").get("hits");
			int totalElements = mapper.readTree(response.toString()).get("hits").get("total").asInt();
			ObjectReader reader = mapper.readerFor(new TypeReference<List<ESData>>() {});
			resultObj = reader.readValue(arrayNode);
    		resultData = new PageImpl<>(resultObj, pageRequest, totalElements);
		} catch(MalformedURLException e){
			throw new net.mmp.center.webapp.exception.MalformedURLException("URL Formet 관련 Error");
		} catch(UnsupportedEncodingException e) {
			throw new net.mmp.center.webapp.exception.UnsupportedEncodingException("Unsupported Encoding Exception Error");
		} catch(JsonProcessingException e) {
			throw new net.mmp.center.webapp.exception.JsonProcessingException("Response Data(JSON String)를 Object로 변환 도중 Error");
		} catch(IOException e) {
			throw new net.mmp.center.webapp.exception.IOException("IO Exception Error");
		}		
		
		return resultData;
		

//		Specifications<QualityHistory> spec = Specifications.where(null);
//		
//		if (Util.checkNullStr(qualityHistorySearchDTO.getSenderIp())) {
//			spec = spec.and((root, query, cb) -> cb.equal(root.get("senderIp"), qualityHistorySearchDTO.getSenderIp()));
//		}
//		if (Util.checkNullStr(qualityHistorySearchDTO.getReflectorIp())) {
//			spec = spec.and((root, query, cb) -> cb.equal(root.get("reflectorIp"), qualityHistorySearchDTO.getReflectorIp()));
//		}
//		PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
//		Page<QualityHistory> findData = qualityHistoryRepository.findAll(spec, pageRequest);
//		
//		if (findData.getContent().isEmpty()) {
//			throw new NotFoundException("Not found pageable By Quality History = " + pageRequest);
//		}
//		logger.info("Total Elements = " + findData.getTotalElements());
//
//		List<QualityHistoryDTO> resultData = new ArrayList<>();
//		
//		resultData = findData.getContent().stream()
//				.map(data -> modelMapper.map(data, QualityHistoryDTO.class))
//				.collect(Collectors.toList());
//		
//		
//		PageImpl<QualityHistoryDTO> resultConvert = new PageImpl<>(resultData, pageRequest, findData.getTotalElements());
//		
//		
//		logger.info("품질 이력 조회 성공");
////		try {
////			get();
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//
//		return resultConvert;
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
	
	private String createElasticsearchQuery(QualityHistorySearchDTO qualityHistorySearchDTO, int page, int size) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("{\r\n");
    	sb.append("\"query\": {\r\n");
    	sb.append("\"bool\": {\r\n");
    	sb.append("\"must\": [\r\n");
    	sb.append(createMatch(qualityHistorySearchDTO));
    	sb.append("]\r\n").append("}\r\n").append("},\r\n");
    	
    	sb.append("\"sort\": [\r\n");
    	sb.append("{\"start_time.keyword\": \"desc\"}\r\n");
    	sb.append("],\r\n");
    	
    	sb.append("\"from\":").append(page * size).append(",\r\n");
    	sb.append("\"size\":").append(size).append("\r\n");
    	sb.append("}\r\n");
    	
    	return sb.toString();
	}
	
	private String createMatch(QualityHistorySearchDTO qualityHistorySearchDTO) {
		StringBuffer sb = new StringBuffer();
		
		if (Util.checkNullStr(qualityHistorySearchDTO.getSenderIp())) {
			sb.append("{\r\n");
			sb.append("\"match\": {\r\n");
			sb.append("\"src_host\": \"" + qualityHistorySearchDTO.getSenderIp() + "\"\r\n");
			sb.append("}\r\n");
			sb.append("}\r\n");
		}
		
		if (Util.checkNullStr(qualityHistorySearchDTO.getReflectorIp())) {
			if (Util.checkNullStr(qualityHistorySearchDTO.getSenderIp())) {
				sb.append(",\r\n");
			}
			
			sb.append("{\r\n");
			sb.append("\"match\": {\r\n");
			sb.append("\"dst_host\": \"" + qualityHistorySearchDTO.getReflectorIp() + "\"\r\n");
			sb.append("}\r\n");
			sb.append("}\r\n");
		}
		
		return sb.toString();
	}
}
