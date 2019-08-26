package net.mmp.center.webapp.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.mmp.center.webapp.dto.BigdataAnalysisDTO;
import net.mmp.center.webapp.dto.BigdataAnalysisDTO.BigdataAnalysisResultDTO;
import net.mmp.center.webapp.model.ESData;
import net.mmp.center.webapp.service.BigdataAnalysisService;
import net.mmp.center.webapp.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@Service(BigdataAnalysisServiceImpl.BEAN_QUALIFIER)
public class BigdataAnalysisServiceImpl implements BigdataAnalysisService {
	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.BigdataAnalysisServiceImpl";

	private static final Logger logger = LogManager.getLogger(BigdataAnalysisServiceImpl.class);
	
	@Value("${config.twamp.visualization.host}")
	private String esHost;
	
	@Value("${config.twamp.visualization.port}")
	private String esPort;
	
	@Value("${config.twamp.index.name}")
	private String indexName;

	@Value("${config.elasticsearch.timezone}")
	private int timezone;
	
	@Autowired
	SimpMessagingTemplate template;
	
	public BigdataAnalysisResultDTO AnalysisElasticSearchData(BigdataAnalysisDTO bigdataAnalysisDTO, Pageable pageable) {
		
		StringBuffer url = new StringBuffer();                           
		url.append("http://");
		url.append(esHost);
		url.append(":");
		url.append(esPort);
		url.append("/");
		url.append(indexName);
		url.append("/_search");
		
		StringBuffer requestData = new StringBuffer();

		long startTime = 0;
		long endTime = 0;
		
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		    Date parsedDate1 =  dateFormat.parse(bigdataAnalysisDTO.getStartTime());
		    Date parsedDate2 =  dateFormat.parse(bigdataAnalysisDTO.getEndtime());
		    startTime = new Timestamp(parsedDate1.getTime()).getTime() / 1000;
		    endTime = new Timestamp(parsedDate2.getTime()).getTime() / 1000;
		} catch(ParseException e) { //this generic but you can control another types of exception
		    // look the origin of excption 
			throw new net.mmp.center.webapp.exception.ParseException("측정시작, 측정종료시간값 Parse Error");
		}
		
		requestData = setSearchJSONQuery(bigdataAnalysisDTO, startTime, endTime, pageable.getPageNumber(), pageable.getPageSize());
		
		PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		StringBuffer response = new StringBuffer();
		List<ESData> resultObj = new ArrayList<ESData>();
		PageImpl<ESData> resultConvert = new PageImpl<ESData>(resultObj);
		BigdataAnalysisResultDTO result = new BigdataAnalysisResultDTO();
		try {
			URL esurl = new URL(url.toString());
			HttpURLConnection conn = (HttpURLConnection) esurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			
			long start = System.currentTimeMillis();
			
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			osw.write(requestData.toString());
			osw.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			
			long end = System.currentTimeMillis();
			
			osw.close();
			br.close();
			conn.disconnect();

			ObjectMapper mapper = new ObjectMapper();
			JsonNode arrayNode = mapper.readTree(response.toString()).get("hits").get("hits");
			int totalElements = mapper.readTree(response.toString()).get("hits").get("total").asInt();
			ObjectReader reader = mapper.readerFor(new TypeReference<List<ESData>>() {});
			resultObj = reader.readValue(arrayNode);
			logger.info(resultObj);
			
			resultConvert = new PageImpl<>(resultObj, pageRequest, totalElements);
			result.setSearchAchiveTime((end - start) / 1000.0);
			result.setResultData(resultConvert);
			
		} catch(MalformedURLException e){
			throw new net.mmp.center.webapp.exception.MalformedURLException("URL Formet 관련 Error");
		} catch(UnsupportedEncodingException e) {
			throw new net.mmp.center.webapp.exception.UnsupportedEncodingException("Unsupported Encoding Exception Error");
		} catch(JsonProcessingException e) {
			throw new net.mmp.center.webapp.exception.JsonProcessingException("Response Data(JSON String)를 Object로 변환 도중 Error");
		} catch(IOException e) {
			throw new net.mmp.center.webapp.exception.IOException("IO Exception Error");
		}
		
		return result;
	}
	
	private StringBuffer setSearchJSONQuery(BigdataAnalysisDTO bigdataAnalysisDTO, long startTime, long endTime, int page, int size) {
		
		
		StringBuffer result = new StringBuffer();
		
		result.append("{\r\n");
		result.append("\"query\": {\r\n");
		result.append("\"bool\": {\r\n");
		result.append("\"must\": [\r\n");
		
		result.append(setOneDepthBool(bigdataAnalysisDTO, startTime, endTime));

		result.append("]\r\n");
		result.append("}\r\n");
		result.append("},\r\n");
		
//		result.append(setAggregation(startTime, endTime));
		
		result.append("\"from\": " + page * size + ",\r\n");
		result.append("\"size\": " + size + "\r\n");
		result.append("}");
		return result;
	}
	
	private StringBuffer setOneDepthBool(BigdataAnalysisDTO bigdataAnalysisDTO, long startTime, long endTime) {
		StringBuffer result = new StringBuffer();
		
		List<Integer> idx = new ArrayList<>();
		if (bigdataAnalysisDTO.getLostPacketTH() > 0) {
			idx.add(0);
		}
		if (bigdataAnalysisDTO.getDuplicatePacketTH() > 0) {
			idx.add(1);
		}
		if (bigdataAnalysisDTO.getOutoforderPacketTH() > 0) {
			idx.add(2);
		}
		if (bigdataAnalysisDTO.getPdvTH() > 0) {
			idx.add(3);
		}
		if (bigdataAnalysisDTO.getIpdvTH() > 0) {
			idx.add(4);
		}
		if (idx.size() == 0) {
			result.append("{\r\n");
			result.append("\"bool\": {\r\n");
			result.append("\"should\": [\r\n");
			
			result.append("{\r\n");
			result.append("\"bool\": {\r\n");
			result.append("\"must\": [\r\n");
			
			result.append(setMatch(bigdataAnalysisDTO));
			
			result.append(setTimestamp(bigdataAnalysisDTO, startTime, endTime, idx));
			
			result.append("]\r\n");
			result.append("}\r\n");
			result.append("}\r\n");
			
			result.append("]\r\n");
			result.append("}\r\n");
			result.append("}\r\n");
		} else {			
			for (int a = 0; a < idx.size(); a++) {
				result.append("{\r\n");
				result.append("\"bool\": {\r\n");
				result.append("\"should\": [\r\n");
				
				result.append(setTwoDepthBool(bigdataAnalysisDTO, a, idx, startTime, endTime));
				
				result.append("]\r\n");
				result.append("}\r\n");
				result.append("}\r\n");
				if (a != idx.size() - 1) {
					result.append(",\r\n");
				}
			}
		}
		return result;
	}
	
	private StringBuffer setTwoDepthBool(BigdataAnalysisDTO bigdataAnalysisDTO, int count, List<Integer> idx, long startTime, long endTime) {
		
		StringBuffer result = new StringBuffer();
		if (idx.size() > 0) {
			for (int a = 0; a < 2; a++) {
				result.append("{\r\n");
				result.append("\"bool\": {\r\n");
				result.append("\"must\": [\r\n");
				
				result.append(setMatch(bigdataAnalysisDTO));
				if (idx.size() > 0 && (Util.checkNullStr(bigdataAnalysisDTO.getSenderIp()) || Util.checkNullStr(bigdataAnalysisDTO.getReflectorIp()))) {
					result.append(",\r\n");
				}
				result.append(setRange(bigdataAnalysisDTO, count, a, idx));
				
				result.append(setTimestamp(bigdataAnalysisDTO, startTime, endTime, idx));
				result.append("]\r\n");
				result.append("}\r\n");
				result.append("}\r\n");
				if (a != 1) {
					result.append(",\r\n");
				}
			}
		} else {
			result.append("{\r\n");
			result.append("\"bool\": {\r\n");
			result.append("\"must\": [\r\n");
			
			result.append(setMatch(bigdataAnalysisDTO));
			
			result.append("]\r\n");
			result.append("}\r\n");
			result.append("}\r\n");
		}
		return result;
	}

	private StringBuffer setMatch(BigdataAnalysisDTO bigdataAnalysisDTO) {
		StringBuffer result = new StringBuffer();
		int count = 0;
		if (Util.checkNullStr(bigdataAnalysisDTO.getSenderIp())) {
			result.append("{\r\n");
			result.append("\"match\": {\r\n");
			result.append("\"src_host\": \"" + bigdataAnalysisDTO.getSenderIp() + "\"\r\n");
			result.append("}\r\n");
			result.append("}\r\n");
			count = 1;
		}
		if (Util.checkNullStr(bigdataAnalysisDTO.getReflectorIp())) {
			if (count == 1) {
				result.append(",\r\n");
			}
			result.append("{\r\n");
			result.append("\"match\": {\r\n");
			result.append("\"dst_host\": \"" + bigdataAnalysisDTO.getReflectorIp() + "\"\r\n");
			result.append("}\r\n");
			result.append("}\r\n");
		}
		return result;
	}

	private StringBuffer setRange(BigdataAnalysisDTO bigdataAnalysisDTO, int count, int isupdown, List<Integer> idx) {
		StringBuffer result = new StringBuffer();
		
		result.append("{\r\n");
		result.append("\"range\": {\r\n");
		result.append("\"");
		if (isupdown == 0 && idx.get(count) < 3) {
			result.append("up_");
		}
		if (isupdown == 1 && idx.get(count) < 3) {
			result.append("down_");
		}
		if (idx.get(count) == 0) {
			result.append("lost_packets\": { \"gte\": " + bigdataAnalysisDTO.getLostPacketTH() + " }\r\n");
		}
		if (idx.get(count) == 1) {
			result.append("duplicate_packets\": { \"gte\": " + bigdataAnalysisDTO.getDuplicatePacketTH() + " }\r\n");
		}
		if (idx.get(count) == 2) {
			result.append("outoforder_packets\": { \"gte\": " + bigdataAnalysisDTO.getOutoforderPacketTH() + " }\r\n");
		}
		if (idx.get(count) == 3) {
			result.append("pdv.pdv\": { \"gte\": " + bigdataAnalysisDTO.getPdvTH() + " }\r\n");
		}
		if (idx.get(count) == 4) {
			result.append("ipdv.ipdv\": { \"gte\": " + bigdataAnalysisDTO.getIpdvTH() + " }\r\n");
		}
		result.append("}\r\n");
		result.append("}\r\n");
		
		return result;
	}

//	private StringBuffer setAggregation(long startTime, long endTime) {
//		
//		StringBuffer aggs = new StringBuffer();
//		aggs.append("\"aggs\": {\r\n");
//		aggs.append("\"range\": {\r\n");
//		aggs.append("\"date_range\": {\r\n");
//		aggs.append("\"field\": \"timestamp\",\r\n");
//		aggs.append("\"ranges\": [\r\n");
//		aggs.append("{\r\n");
//		aggs.append("\"from\": \"" + startTime + "\",\r\n");
//		aggs.append("\"to\": \"" + endTime + "\"\r\n");
//		aggs.append("}\r\n");
//		aggs.append("]\r\n");
//		aggs.append("}\r\n");
//		aggs.append("}\r\n");
//		aggs.append("},\r\n");
//		return aggs;
//		
//	}
	
	private StringBuffer setTimestamp(BigdataAnalysisDTO bigdataAnalysisDTO, long startTime, long endTime, List<Integer> idx) {
		
		StringBuffer result = new StringBuffer();
		
		if (Util.checkNullStr(bigdataAnalysisDTO.getSenderIp()) || Util.checkNullStr(bigdataAnalysisDTO.getReflectorIp()) || idx.size() > 0) {
			result.append(",\r\n");
		}
		long st = startTime + timezone;
		long et = endTime + timezone; 
		result.append("{\r\n");
		result.append("\"range\": {\r\n");
		result.append("\"timestamp\": {\r\n");
		result.append("\"gte\": " + st + ",\r\n");
		result.append("\"lte\": " + et + "\r\n");
		result.append("}\r\n");
		result.append("}\r\n");
		result.append("}\r\n");
		
		return result;
	}
}
