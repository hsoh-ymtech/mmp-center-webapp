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
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import net.mmp.center.webapp.dto.ExternalQualityHistoryDTO;
import net.mmp.center.webapp.dto.ExternalQualityHistorySearchDTO;
import net.mmp.center.webapp.dto.ExternalQualityStatisticsInfo;
import net.mmp.center.webapp.model.ESData;
import net.mmp.center.webapp.service.ExternalRequestService;
import net.mmp.center.webapp.util.Util;

@Service(ExternalRequestServiceImpl.BEAN_QUALIFIER)
public class ExternalRequestServiceImpl implements ExternalRequestService {
	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.ExternalRequestServiceImpl";

	private static final Logger logger = LogManager.getLogger(QualityHistoryServiceImpl.class);
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Value("${elasticsearch.host}")
	private String elasticsearchHost;

	@Value("${elasticsearch.http.port}")
	private int elasticsearchHttpPort;

	@Value("${config.twamp.index.name}")
	private String elasticsearchIndex;

	public String getUrlofElasticsearch(){
		return "http://"+elasticsearchHost+":"+elasticsearchHttpPort;
	}

	public String searcIndexUrl() {
		return getUrlofElasticsearch()+"/"+elasticsearchIndex+"/_search";
	}

	@Transactional
	public ESData getQualityHistoryRecent(ExternalQualityHistorySearchDTO dto) {
		String query = createElasticsearchQueryForRecent(dto);
		List<ESData> resultObj = new ArrayList<ESData>();
		ESData resultData = null;
		
		try {
			String response = requestElasticsearch(query);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode arrayNode = mapper.readTree(response).get("hits").get("hits");
			
			ObjectReader reader = mapper.readerFor(new TypeReference<List<ESData>>() {});
			resultObj = reader.readValue(arrayNode);
			
			resultData = resultObj.get(0);
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
	}

	public ExternalQualityHistoryDTO getQualityHistoryStatistics(Pageable pageable, ExternalQualityHistorySearchDTO dto) {
		ExternalQualityHistoryDTO result = new ExternalQualityHistoryDTO();
		Calendar now = Calendar.getInstance();
		result = getQualityHistoryStatisticsDay(pageable, now, dto, result);
		result = getQualityHistoryStatisticsWeek(pageable, now, dto, result);
		result = getQualityHistoryStatisticsMonth(pageable, now, dto, result);
		
		return result;
	}
	
	private ExternalQualityHistoryDTO getQualityHistoryStatisticsDay(Pageable pageable, Calendar now, ExternalQualityHistorySearchDTO dto, ExternalQualityHistoryDTO result) {
		try {
			
			PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
			
			Calendar toCalendar = (Calendar) now.clone();
			Calendar fromCalendar = (Calendar) now.clone();
			fromCalendar.add(Calendar.DAY_OF_YEAR, -1);
			
			String dayFrom = sdf.format(fromCalendar.getTime());
			String dayTo = sdf.format(toCalendar.getTime());
			String dayQuery = createElasticsearchQueryForStatistics(dto, dayFrom, dayTo);
			
			// 1일 통계 데이터 1건
			String dayResult = requestElasticsearch(dayQuery);
		
			ObjectMapper mapper = new ObjectMapper();
			JsonNode aggNode = mapper.readTree(dayResult).get("aggregations");
			ExternalQualityStatisticsInfo dayResultInfo = parseAggregations(dayTo, aggNode);
			result.setDayStatistics(dayResultInfo);
			
			// 시간 통계 데이터 24건
			Calendar toListCalendar = (Calendar) now.clone();
			Calendar fromListCalendar = (Calendar) now.clone();
			fromListCalendar.add(Calendar.HOUR_OF_DAY, -1);
			List<ExternalQualityStatisticsInfo> dayList = new ArrayList<ExternalQualityStatisticsInfo>();
			
			for (int idx = 0; idx < 24; idx++) {
				String dayListTo = sdf.format(toListCalendar.getTime());
				String dayListFrom = sdf.format(fromListCalendar.getTime());
				String dayListQuery = createElasticsearchQueryForStatistics(dto, dayListFrom, dayListTo);
				
				// 시간별 통계 데이터 1건
				String hourResult = requestElasticsearch(dayListQuery);
				mapper = new ObjectMapper();
				JsonNode hourAggNode = mapper.readTree(hourResult).get("aggregations");
				ExternalQualityStatisticsInfo hourResultInfo = parseAggregations(dayListTo, hourAggNode);
				dayList.add(hourResultInfo);
				
				toListCalendar.add(Calendar.HOUR_OF_DAY, -1);
				fromListCalendar.add(Calendar.HOUR_OF_DAY, -1);
			}
			
			PageImpl<ExternalQualityStatisticsInfo> dayListResult = new PageImpl<>(dayList, pageRequest, dayList.size());
			result.setDayStatisticsList(dayListResult);
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
	
	private ExternalQualityHistoryDTO getQualityHistoryStatisticsWeek(Pageable pageable, Calendar now, ExternalQualityHistorySearchDTO dto, ExternalQualityHistoryDTO result) {
		try {
			PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
			
			Calendar toCalendar = (Calendar) now.clone();
			Calendar fromCalendar = (Calendar) now.clone();
			fromCalendar.add(Calendar.DAY_OF_YEAR, -7);
			
			String weekFrom = sdf.format(fromCalendar.getTime());
			String weekTo = sdf.format(toCalendar.getTime());
			String weekQuery = createElasticsearchQueryForStatistics(dto, weekFrom, weekTo);
			
			// 1주일 통계 데이터 1건
			String weekResult = requestElasticsearch(weekQuery);
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode aggNode = mapper.readTree(weekResult).get("aggregations");
			ExternalQualityStatisticsInfo weekResultInfo = parseAggregations(weekTo, aggNode);
			result.setWeekStatistics(weekResultInfo);
			
			// 일 통계 데이터 7건
			Calendar toListCalendar = (Calendar) now.clone();
			Calendar fromListCalendar = (Calendar) now.clone();
			fromListCalendar.add(Calendar.DAY_OF_YEAR, -1);
			List<ExternalQualityStatisticsInfo> weekList = new ArrayList<ExternalQualityStatisticsInfo>();
			
			for (int idx = 0; idx < 7; idx++) {
				String weekListTo = sdf.format(toListCalendar.getTime());
				String weekListFrom = sdf.format(fromListCalendar.getTime());
				String weekListQuery = createElasticsearchQueryForStatistics(dto, weekListFrom, weekListTo);
				
				// 일별 통계 데이터 1건
				String dayResult = requestElasticsearch(weekListQuery);
				mapper = new ObjectMapper();
				JsonNode dayAggNode = mapper.readTree(dayResult).get("aggregations");
				ExternalQualityStatisticsInfo dayResultInfo = parseAggregations(weekListTo, dayAggNode);
				weekList.add(dayResultInfo);
				
				toListCalendar.add(Calendar.DAY_OF_YEAR, -1);
				fromListCalendar.add(Calendar.DAY_OF_YEAR, -1);
			}
			
			PageImpl<ExternalQualityStatisticsInfo> weekListResult = new PageImpl<>(weekList, pageRequest, weekList.size());
			result.setWeekStatisticsList(weekListResult);
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
	
	private ExternalQualityHistoryDTO getQualityHistoryStatisticsMonth(Pageable pageable, Calendar now, ExternalQualityHistorySearchDTO dto, ExternalQualityHistoryDTO result) {
		try {
			PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
			Calendar toCalendar = (Calendar) now.clone();
			Calendar fromCalendar = (Calendar) now.clone();
			fromCalendar.add(Calendar.DAY_OF_YEAR, -28);
			
			String monthFrom = sdf.format(fromCalendar.getTime());
			String monthTo = sdf.format(toCalendar.getTime());
			String monthQuery = createElasticsearchQueryForStatistics(dto, monthFrom, monthTo);
			
			// 4주 통계 데이터 1건
			String monthResult = requestElasticsearch(monthQuery);
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode aggNode = mapper.readTree(monthResult).get("aggregations");
			ExternalQualityStatisticsInfo monthResultInfo = parseAggregations(monthTo, aggNode);
			result.setMonthStatistics(monthResultInfo);
			
			// 주별 통계 데이터 4건
			Calendar toListCalendar = (Calendar) now.clone();
			Calendar fromListCalendar = (Calendar) now.clone();
			fromListCalendar.add(Calendar.DAY_OF_YEAR, -7);
			List<ExternalQualityStatisticsInfo> monthList = new ArrayList<ExternalQualityStatisticsInfo>();
			
			for (int idx = 0; idx < 4; idx++) {
				String monthListTo = sdf.format(toListCalendar.getTime());
				String monthListFrom = sdf.format(fromListCalendar.getTime());
				String monthListQuery = createElasticsearchQueryForStatistics(dto, monthListFrom, monthListTo);
				
				// 주 통계 데이터 1건
				String weekResult = requestElasticsearch(monthListQuery);
				mapper = new ObjectMapper();
				JsonNode weekAggNode = mapper.readTree(weekResult).get("aggregations");
				ExternalQualityStatisticsInfo weekResultInfo = parseAggregations(monthListTo, weekAggNode);
				monthList.add(weekResultInfo);
				
				toListCalendar.add(Calendar.DAY_OF_YEAR, -7);
				fromListCalendar.add(Calendar.DAY_OF_YEAR, -7);
			}
			
			PageImpl<ExternalQualityStatisticsInfo> monthListResult = new PageImpl<>(monthList, pageRequest, monthList.size());
			result.setMonthStatisticsList(monthListResult);
			
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
	
	private ExternalQualityStatisticsInfo parseAggregations(String time, JsonNode node) {
		
		int lost_packets = node.get("lost_packets").get("value").asInt();
		int duplicate_packets = node.get("duplicate_packets").get("value").asInt();
		int outoforder_packets = node.get("outoforder_packets").get("value").asInt();
		float delay = Double.valueOf(node.get("delay").get("value").asDouble()).floatValue();
		float pdv = Double.valueOf(node.get("pdv").get("value").asDouble()).floatValue();
		float ipdv = Double.valueOf(node.get("ipdv").get("value").asDouble()).floatValue();
		float inter_delay = Double.valueOf(node.get("inter_delay").get("value").asDouble()).floatValue();
		float bandwidth = Double.valueOf(node.get("bandwidth").get("value").asDouble()).floatValue();
		
		ExternalQualityStatisticsInfo info = new ExternalQualityStatisticsInfo();
		info.setTime(time);
		info.setLost_packets(lost_packets);
		info.setDuplicate_packets(duplicate_packets);
		info.setOutoforder_packets(outoforder_packets);
		info.setDelay(delay);
		info.setPdv(pdv);
		info.setIpdv(ipdv);
		info.setInter_delay(inter_delay);
		info.setBandwidth(bandwidth);
		
		return info;
	}
	
	private String requestElasticsearch(String query) {
		StringBuffer response = new StringBuffer();
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
			String line;
			while ((line = br.readLine()) != null) {
				response.append(line);
			}

			osw.close();
			br.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response.toString();
	}
	
	private String createElasticsearchQueryForRecent(ExternalQualityHistorySearchDTO dto) {
		StringBuffer sb = new StringBuffer();

		sb.append("{\r\n");
		sb.append("\"query\": {\r\n");
		sb.append("\"bool\": {\r\n");
		sb.append("\"must\": [\r\n");
		sb.append(createMatch(dto));
		sb.append("]\r\n").append("}\r\n").append("},\r\n");

		sb.append("\"sort\": [\r\n");
		sb.append("{\"@timestamp\": \"desc\"}\r\n");
		sb.append("],\r\n");

		sb.append("\"from\":").append(0).append(",\r\n");
		sb.append("\"size\":").append(1).append("\r\n");
		sb.append("}\r\n");

		return sb.toString();
	}

	private String createElasticsearchQueryForStatistics(ExternalQualityHistorySearchDTO dto, String from, String to) {
		StringBuffer sb = new StringBuffer();

		sb.append("{\r\n");
		sb.append("\"query\": {\r\n");
		sb.append("\"bool\": {\r\n");
		sb.append("\"must\": [\r\n");
		
		String match = createMatch(dto);
		sb.append(match);
		
		String range = createRange(from, to);
		if (!match.isEmpty()) {
			sb.append(",\r\n");
		}
		sb.append(range);
		
		sb.append("]\r\n").append("}\r\n").append("},\r\n");

		sb.append("\"sort\": [\r\n");
		sb.append("{\"@timestamp\": \"desc\"}\r\n");
		sb.append("],\r\n");

		String aggregation = createAggregation();
		sb.append(aggregation);
		sb.append("}\r\n");

		return sb.toString();
	}
	
	private String createMatch(ExternalQualityHistorySearchDTO dto) {
		StringBuffer sb = new StringBuffer();

		if (Util.checkNullStr(dto.getSrc_host())) {
			sb.append("{\r\n");
			sb.append("\"match\": {\r\n");
			sb.append("\"src_host\": \"" + dto.getSrc_host() + "\"\r\n");
			sb.append("}\r\n");
			sb.append("}\r\n");
		}

		if (Util.checkNullStr(dto.getDst_host())) {
			if (Util.checkNullStr(dto.getSrc_host())) {
				sb.append(",\r\n");
			}

			sb.append("{\r\n");
			sb.append("\"match\": {\r\n");
			sb.append("\"dst_host\": \"" + dto.getDst_host() + "\"\r\n");
			sb.append("}\r\n");
			sb.append("}\r\n");
		}

		return sb.toString();
	}
	
	private String createRange(String from, String to) {
		StringBuffer sb = new StringBuffer();

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
		
		return sb.toString();
	}
	
	private String createAggregation() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("\"size\":").append("\"0\",\r\n");
		
		sb.append("\"aggs\":{\r\n");
		sb.append("\"measurement_count\": {").append("\"sum\": {\"field\": \"ipdv\", \"script\": \"_value\"} },\r\n");
    	sb.append("\"ipdv\": {").append("\"avg\": {\"field\": \"ipdv\", \"script\": \"_value\"} },\r\n");
    	sb.append("\"lost_packets\": {").append("\"sum\": {\"field\": \"lost_packets\", \"script\": \"_value\"} },\r\n");
    	sb.append("\"duplicate_packets\": {").append("\"sum\": {\"field\": \"duplicate_packets\", \"script\": \"_value\"} },\r\n");
    	sb.append("\"delay\": {").append("\"avg\": {\"field\": \"delay\", \"script\": \"_value\"} },\r\n");
    	sb.append("\"outoforder_packets\": {").append("\"sum\": {\"field\": \"outoforder_packets\", \"script\": \"_value\"} },\r\n");
    	sb.append("\"inter_delay\": {").append("\"avg\": {\"field\": \"inter_delay\", \"script\": \"_value\"} },\r\n");
    	sb.append("\"pdv\": {").append("\"avg\": {\"field\": \"pdv\", \"script\": \"_value\"} },\r\n");
    	sb.append("\"bandwidth\": {").append("\"avg\": {\"field\": \"bandwidth\", \"script\": \"_value\"} }\r\n");
    	sb.append("}\r\n");
    	
    	return sb.toString();
	}
}
