package net.mmp.center.webapp.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import net.mmp.center.webapp.dto.DataAnalysisDTO;
import net.mmp.center.webapp.dto.DataAnalysisDTO.DataataAnalysisResultDTO;
import net.mmp.center.webapp.model.ESData;
import net.mmp.center.webapp.service.DataAnalysisService;
import net.mmp.center.webapp.util.Util;

@Service(DataAnalysisServiceImpl.BEAN_QUALIFIER)
public class DataAnalysisServiceImpl implements DataAnalysisService {
	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.DataAnalysisServiceImpl";

	private static final Logger logger = LogManager.getLogger(DataAnalysisServiceImpl.class);
	
	@Value("${config.twamp.visualization.host}")
	private String esHost;
	
	@Value("${config.twamp.visualization.port}")
	private String esPort;
	
	@Value("${config.twamp.index.name}")
	private String indexName;

	@Value("${config.elasticsearch.timezone}")
	private int timezone;

	@Value("${config.twamp.index.name}")
	private String elasticsearchIndex;

	@Autowired
	SimpMessagingTemplate template;
	
	@Value("${elasticsearch.host}")
	private String elasticsearchHost;

	@Value("${elasticsearch.http.port}")
	private int elasticsearchHttpPort;
	
	private static SimpleDateFormat originDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat convertDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	public String getUrlofElasticsearch(){
		return "http://"+elasticsearchHost+":"+elasticsearchHttpPort;
	}
	public String searcIndexUrl() {
		return getUrlofElasticsearch()+"/"+elasticsearchIndex+"/_search";
	}
	public DataataAnalysisResultDTO AnalysisElasticSearchData(DataAnalysisDTO dataAnalysisDTO, Pageable pageable) {
		String query = createElasticSearchQuery(dataAnalysisDTO, pageable.getPageNumber(), pageable.getPageSize());
		PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		StringBuffer response = new StringBuffer();
		List<ESData> resultObj = new ArrayList<ESData>();
		PageImpl<ESData> resultConvert = new PageImpl<ESData>(resultObj);
		DataataAnalysisResultDTO result = new DataataAnalysisResultDTO();
		try {
			URL esurl = new URL(searcIndexUrl());
			HttpURLConnection conn = (HttpURLConnection) esurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			osw.write(query);
			osw.flush();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			
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
	
	public static String createElasticSearchQuery(DataAnalysisDTO dto, int page, int size) {
		StringBuffer sb = new StringBuffer();
		
		try {
			sb.append("{\r\n");
			sb.append("\"query\": {\r\n");
			sb.append("\"bool\": {\r\n");
			sb.append("\"must\": [\r\n");
			
			String match = createMatch(dto);
			sb.append(match);
			
			String timeRange = createTimeRange(dto);
			if (!timeRange.isEmpty()) {
				sb.append(",\r\n");
			}
			sb.append(timeRange);
			
			String thresholdRange = createThresholdRange(dto);
			if (!thresholdRange.isEmpty()) {
				sb.append(",\r\n");
			}
			sb.append(thresholdRange);
			sb.append("]\r\n").append("}\r\n").append("},\r\n");

			sb.append("\"sort\": [\r\n");
			sb.append("{\"@timestamp\": \"desc\"}\r\n");
			sb.append("],\r\n");

			sb.append("\"from\":").append(page * size).append(",\r\n");
			sb.append("\"size\":").append(size).append("\r\n");
			sb.append("}\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	private static String createMatch(DataAnalysisDTO dto) {
		StringBuffer sb = new StringBuffer();

		if (Util.checkNullStr(dto.getSenderIp())) {
			sb.append("{\r\n");
			sb.append("\"match\": {\r\n");
			sb.append("\"src_host\": \"" + dto.getSenderIp() + "\"\r\n");
			sb.append("}\r\n");
			sb.append("}\r\n");
		}

		if (Util.checkNullStr(dto.getReflectorIp())) {
			if (Util.checkNullStr(dto.getSenderIp())) {
				sb.append(",\r\n");
			}

			sb.append("{\r\n");
			sb.append("\"match\": {\r\n");
			sb.append("\"dst_host\": \"" + dto.getReflectorIp() + "\"\r\n");
			sb.append("}\r\n");
			sb.append("}\r\n");
		}

		return sb.toString();
	}
	
	private static String createTimeRange(DataAnalysisDTO dto) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (Util.checkNullStr(dto.getStartTime()) && Util.checkNullStr(dto.getEndtime())) {
			String from = convertDateFormat.format(originDateFormat.parse(dto.getStartTime()));
			String to = convertDateFormat.format(originDateFormat.parse(dto.getEndtime()));
			
			sb.append("{\r\n");
			sb.append("\"range\": {\r\n");
			sb.append("\"@timestamp\": {\r\n");
			
			if (Util.checkNullStr(from)) {
				sb.append("\"gte\": \"" + from + "\"");
			}
			
			if (Util.checkNullStr(to)) {
				if (Util.checkNullStr(from)) {
					sb.append(",\r\n");
				}
				
				sb.append("\"lt\": \"" + to + "\"");
			}
			
			sb.append("}\r\n").append("}\r\n").append("}\r\n");
		}
		
		return sb.toString();
	}
	
	private static String createThresholdRange(DataAnalysisDTO dto) {
		int lost_packets_th = dto.getLostPacketTH();
		int duplicate_packets_th = dto.getDuplicatePacketTH();
		int outoforder_packets_th = dto.getOutoforderPacketTH();
		float pdv_th = dto.getPdvTH();
		float ipdv_th = dto.getIpdvTH();
		
		StringBuffer sb = new StringBuffer();
		
		if (lost_packets_th != 0) {
			if (!sb.toString().isEmpty()) {
				sb.append(",\r\n");
			}
			
			sb.append("{\r\n");
			sb.append("\"range\": {\r\n");
			sb.append("\"lost_packets\": {\r\n");
			sb.append("\"gte\": \"" + lost_packets_th + "\"}\r\n");
			sb.append("}\r\n").append("}\r\n");
		}
		
		if (duplicate_packets_th != 0) {
			if (!sb.toString().isEmpty()) {
				sb.append(",\r\n");
			}
			
			sb.append("{\r\n");
			sb.append("\"range\": {\r\n");
			sb.append("\"duplicate_packets\": {\r\n");
			sb.append("\"gte\": \"" + duplicate_packets_th + "\"}\r\n");
			sb.append("}\r\n").append("}\r\n");
		}
		
		if (outoforder_packets_th != 0) {
			if (!sb.toString().isEmpty()) {
				sb.append(",\r\n");
			}
			
			sb.append("{\r\n");
			sb.append("\"range\": {\r\n");
			sb.append("\"outoforder_packets\": {\r\n");
			sb.append("\"gte\": \"" + outoforder_packets_th + "\"}\r\n");
			sb.append("}\r\n").append("}\r\n");
		}
		
		if (pdv_th != 0) {
			if (!sb.toString().isEmpty()) {
				sb.append(",\r\n");
			}
			
			sb.append("{\r\n");
			sb.append("\"range\": {\r\n");
			sb.append("\"pdv\": {\r\n");
			sb.append("\"gte\": \"" + pdv_th + "\"}\r\n");
			sb.append("}\r\n").append("}\r\n");
		}
		
		if (ipdv_th != 0) {
			if (!sb.toString().isEmpty()) {
				sb.append(",\r\n");
			}
			
			sb.append("{\r\n");
			sb.append("\"range\": {\r\n");
			sb.append("\"ipdv\": {\r\n");
			sb.append("\"gte\": \"" + ipdv_th + "\"}\r\n");
			sb.append("}\r\n").append("}\r\n");
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		DataAnalysisDTO dto = new DataAnalysisDTO();
		dto.setDuplicatePacketTH(0);
		dto.setLostPacketTH(1);
		dto.setPdvTH(-100f);
		dto.setIpdvTH(0);
		dto.setOutoforderPacketTH(0);
		dto.setStartTime("2019-10-01 00:00:00");
		dto.setEndtime("2019-10-12 23:59:59");
		dto.setSenderIp("52.79.239.211");
		dto.setReflectorIp("13.114.206.157");
		
		System.out.println(createElasticSearchQuery(dto, 0, 10));
	}
}
