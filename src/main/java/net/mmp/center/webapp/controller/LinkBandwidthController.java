package net.mmp.center.webapp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.mmp.center.webapp.domain.LinkBandwidth;
import net.mmp.center.webapp.dto.LinkBandwidthDTO;
import net.mmp.center.webapp.dto.LinkLossRateDTO;
import net.mmp.center.webapp.repository.LinkBandwidthRepository;

@RestController
public class LinkBandwidthController {

	private static final Logger logger = LogManager.getLogger(LinkBandwidthController.class);

	@Autowired
	private LinkBandwidthRepository repository;

	/**
	 * Bootstrap 등록
	 * 
	 * @param linkBandwidthDTO
	 *            Bootstrap DTO
	 * @return 등록 결과S
	 */
	@RequestMapping(value = "/v1/linkBandwidths", method = RequestMethod.POST)
	public ResponseEntity<LinkBandwidthDTO> postBootstrap(@RequestBody @Valid LinkBandwidthDTO linkBandwidthDTO,
		final BindingResult result, HttpServletResponse request, HttpServletResponse response) {

		LinkBandwidth entity = new LinkBandwidth();
		entity.setId(null);
		entity.setBandwidth(linkBandwidthDTO.getBandwidth());
		entity.setDestIpAddress(linkBandwidthDTO.getDestIpAddress());
		entity.setMeasuredTime(linkBandwidthDTO.getMeasuredTime());//linkBandwidthDTO.getMeasuredTime());
		entity.setMeshId(linkBandwidthDTO.getMeshId());
		entity.setSrcIpAddress(linkBandwidthDTO.getSrcIpAddress());

		LinkBandwidth savedEntity = repository.save(entity);

		LinkBandwidthDTO retval = new LinkBandwidthDTO();
		retval.setId(savedEntity.getId());
		retval.setBandwidth(savedEntity.getBandwidth());
		retval.setDestIpAddress(savedEntity.getDestIpAddress());
		retval.setMeasuredTime(savedEntity.getMeasuredTime());
		retval.setMeshId(savedEntity.getMeshId());
		retval.setSrcIpAddress(savedEntity.getSrcIpAddress());

		return new ResponseEntity<LinkBandwidthDTO>(retval, HttpStatus.CREATED);
	}

    @RequestMapping(value = "/v1/linkBandwidths", method = RequestMethod.GET)
    public ResponseEntity<LinkBandwidthDTO> getBootstrap(@RequestParam(value = "meshId")String meshId,
														 @RequestParam(value = "srcIpAddress")String srcIpAddress,
														 @RequestParam(value = "destIpAddress")String destIpAddress)  {

//        List<LinkBandwidth> list = repository.findAll();
//    	List<LinkBandwidth> list = repository.findBySrcIpAddressAndDestIpAddress(srcIpAddress, destIpAddress);
    	List<LinkBandwidth> list = repository.findTop1000BySrcIpAddressAndDestIpAddressOrderByMeasuredTimeDesc(srcIpAddress, destIpAddress);
        float avgLinkBandwidth = 0.0f;
        
        float totalLinkBandwidth = 0.0f;
        for(LinkBandwidth item : list) {
        	totalLinkBandwidth += item.getBandwidth();
        }
        
        if (list.size() == 0) {
        	avgLinkBandwidth = 0.0f;
        } else {
        	avgLinkBandwidth = totalLinkBandwidth / list.size();
        }
        
        LinkBandwidthDTO retval = new LinkBandwidthDTO();
        retval.setMeshId(meshId);
        retval.setBandwidth(avgLinkBandwidth);
        retval.setSrcIpAddress(srcIpAddress);
        retval.setDestIpAddress(destIpAddress);

        return new ResponseEntity<LinkBandwidthDTO>(retval, HttpStatus.OK);
    }

    @RequestMapping(value = "/v1/linkLossRates", method = RequestMethod.GET)
    public ResponseEntity<LinkLossRateDTO> getLinkLossRate(@RequestParam(value = "meshId")String meshId,
                                                           @RequestParam(value = "srcIpAddress")String srcIpAddress,
                                                           @RequestParam(value = "destIpAddress")String destIpAddress)  {
	    // ES와 연결하여 관련 정보를 가지고 온다.
        // 계산한다.
    	String query = createElasticsearchQueryForLossRate(srcIpAddress, destIpAddress);
    	StringBuffer response = new StringBuffer();
    	
    	int totalLossPacketCount = 0;
        int totalMeasuredPacketCount = 0;
        float lossRate = 0.0f;
    	
    	try {
    		URL esurl = new URL("http://escluster.happylife.io:9200/redis_test-*/_search");
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
    		JsonNode totalMeasurementCountNode = mapper.readTree(response.toString()).get("aggregations").get("total_measurement_count").get("value");
    		JsonNode totalLostPacketsCountNode = mapper.readTree(response.toString()).get("aggregations").get("total_lost_packets_count").get("value");
    		totalMeasuredPacketCount = totalMeasurementCountNode.asInt();
    		totalLossPacketCount = totalLostPacketsCountNode.asInt();
    		if (totalMeasuredPacketCount > 0) {
    			lossRate = (float) totalLossPacketCount / (float) totalMeasuredPacketCount;
    		}
    	} catch(MalformedURLException e){
			throw new net.mmp.center.webapp.exception.MalformedURLException("URL Formet 관련 Error");
		} catch(UnsupportedEncodingException e) {
			throw new net.mmp.center.webapp.exception.UnsupportedEncodingException("Unsupported Encoding Exception Error");
		} catch(JsonProcessingException e) {
			throw new net.mmp.center.webapp.exception.JsonProcessingException("Response Data(JSON String)를 Object로 변환 도중 Error");
		} catch(IOException e) {
			throw new net.mmp.center.webapp.exception.IOException("IO Exception Error");
		} finally {
			
		}
    	
        LinkLossRateDTO retval = new LinkLossRateDTO();
        retval.setMeshId(meshId);
        retval.setLossRate(lossRate);
        retval.setTotalLossPacketCount(totalLossPacketCount);
        retval.setTotalMeasuredPacketCount(totalMeasuredPacketCount);
        retval.setSrcIpAddress(srcIpAddress);
        retval.setDestIpAddress(destIpAddress);

        return new ResponseEntity<LinkLossRateDTO>(retval, HttpStatus.OK);
    }
    
    private String createElasticsearchQueryForLossRate(String srcIpAddress, String destIpAddress) {
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append("{\r\n");
    	sb.append("\"query\": {\r\n");
    	sb.append("\"bool\": {\r\n");
    	sb.append("\"must\": [\r\n");
    	sb.append("{ \"match\": {").append("\"src_host\":").append("\"").append(srcIpAddress).append("\"} },\r\n");
    	sb.append("{ \"match\": {").append("\"dst_host\":").append("\"").append(destIpAddress).append("\"} }\r\n");
    	sb.append("]\r\n").append("}\r\n").append("},\r\n");
    	
    	sb.append("\"sort\": [\r\n");
    	sb.append("{\"start_time.keyword\": \"desc\"}\r\n");
    	sb.append("],\r\n");
    	
    	sb.append("\"size\":").append("0,\r\n");
    	
    	sb.append("\"aggs\":{\r\n");
    	sb.append("\"total_measurement_count\": {").append("\"sum\": {\"field\": \"measurement_count\", \"script\": \"_value\"} },\r\n");
    	sb.append("\"total_lost_packets_count\": {").append("\"sum\": {\"field\": \"lost_packets\", \"script\": \"_value\"} }\r\n");
    	sb.append("}\r\n");
    	
    	sb.append("}\r\n");
    	
    	return sb.toString();
    }
}
