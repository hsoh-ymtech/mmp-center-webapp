package net.mmp.center.webapp.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import net.mmp.center.webapp.dto.CurrentStatusDTO;
import net.mmp.center.webapp.dto.ExternalQualityHistorySearchDTO;
import net.mmp.center.webapp.model.ESData;
import net.mmp.center.webapp.service.CurrentStatusService;
import net.mmp.center.webapp.service.QualityHistoryService;
import net.mmp.center.webapp.util.Util;

@Service(CurrentStatusServiceImpl.BEAN_QUALIFIER)
public class CurrentStatusServiceImpl implements CurrentStatusService {
	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.CurrentStatusServiceImpl";

	private static final Logger logger = LogManager.getLogger(CurrentStatusServiceImpl.class);
	
	@Autowired
	@Qualifier(QualityHistoryServiceImpl.BEAN_QUALIFIER)
	private QualityHistoryService qualityHistoryService;

	public final static int RESULT_OK = 1;
	public final static int RESULT_FAIL = 0;

	@Value("${config.twamp.visualization.host}")
	private String eshost;

	@Value("${config.twamp.visualization.port}")
	private String esport;
	
	@Value("${config.cnc.server.ip}")
	private String cnchost;
	
	@Value("${config.cnc.server.port}")
	private String cncport;
	
	@Value("${config.twampsender.dir}")
	private String twampSenderDir;
	
	@Value("${config.firecracker.username}")
	private String vmUsername;
	
	@Value("${config.firecracker.password}")
	private String vmPassword;
	
	@Value("${config.firecracker.hostip}")
	private String vmHostip;
	
	@Value("${server.port}")
	private String port;
	
	@Value("${elasticsearch.host}")
	private String elasticsearchHost;

	@Value("${elasticsearch.http.port}")
	private int elasticsearchHttpPort;
	
	public ESData QualityMeasureRegister(CurrentStatusDTO currentStatusDTO) {
		// Center에 설치되어 있는 nqmstwamp-client 실행
		// 경로는 /root/HOME/bin
		String senderIp = currentStatusDTO.getSenderIp();
		String reflectorIp = currentStatusDTO.getReflectorIp();
		int packetCount = currentStatusDTO.getSendCount();
		ESData result = null;
		
		ProcessBuilder builder = new ProcessBuilder();
		List<String> cmdList = new ArrayList<String>();
		// Windows 테스트 용
//		cmdList.add("cmd.exe");
//		cmdList.add("/c");
//		cmdList.add("D:\\WORKSPACE-GO\\mmp-go-twamp-tools\\twampclient.exe");
		// Linux 테스트 용
		cmdList.add("/bin/sh");
		cmdList.add("-c");
		cmdList.add("\"/root/HOME/bin/nqmstwamp-client -count " + packetCount + " " + reflectorIp + "\"");
		builder.command(cmdList);
		try {
			Process process = builder.start();
			process.getInputStream().close();
			process.getOutputStream().close();
			process.getErrorStream().close();
			process.waitFor(1, TimeUnit.MINUTES);
			
			Thread.sleep(2000);
			
			ExternalQualityHistorySearchDTO dto = new ExternalQualityHistorySearchDTO();
			dto.setSrc_host(senderIp);
			dto.setDst_host(reflectorIp);
			result = getQualityHistory(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	private ESData getQualityHistory(ExternalQualityHistorySearchDTO dto) {
		String query = createElasticsearchQueryForRecent(dto);
		List<ESData> resultObj = new ArrayList<ESData>();
		ESData resultData = null;
		
		try {
			String response = requestElasticsearch(query);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode arrayNode = mapper.readTree(response).get("hits").get("hits");
			
			ObjectReader reader = mapper.readerFor(new TypeReference<List<ESData>>() {});
			resultObj = reader.readValue(arrayNode);
			
			if (!resultObj.isEmpty()) {
			resultData = resultObj.get(0);
			}
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
	
	private String requestElasticsearch(String query) {
		StringBuffer response = new StringBuffer();
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
	
	public String getUrlofElasticsearch(){
		return "http://"+elasticsearchHost+":"+elasticsearchHttpPort;
	}
	
	public static void main(String[] args) throws Exception {
		// Center에 설치되어 있는 nqmstwamp-client 실행
		// 경로는 /root/HOME/bin
		String senderIp = "10.10.1.6";
		String reflectorIp = "52.79.239.211";
		int packetCount = 10;
		
		ProcessBuilder builder = new ProcessBuilder();
		List<String> cmdList = new ArrayList<String>();
		// Windows 테스트 용
		cmdList.add("cmd.exe");
		cmdList.add("/c");
		cmdList.add("D:\\WORKSPACE-GO\\mmp-go-twamp-tools\\twampclient.exe");
		// Linux 테스트 용
//		cmdList.add("bash");
//		cmdList.add("-c");
//		cmdList.add("/root/HOME/bin/nqmstwamp-client");
		cmdList.add("-count");
		cmdList.add(packetCount + "");
		cmdList.add(reflectorIp);
		builder.command(cmdList);
		Process process = builder.start();
		process.waitFor(30, TimeUnit.MINUTES);
		System.out.println("Execute");
	}
}
