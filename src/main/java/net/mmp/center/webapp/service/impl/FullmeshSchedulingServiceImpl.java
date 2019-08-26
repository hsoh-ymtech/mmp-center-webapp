package net.mmp.center.webapp.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.mmp.center.webapp.domain.ReflectorInfo;
import net.mmp.center.webapp.domain.TargetServerInfo;
import net.mmp.center.webapp.dto.WebSocketClientDTO;
import net.mmp.center.webapp.repository.ReflectorInfoRepository;
import net.mmp.center.webapp.service.FullmeshSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;

@Service(FullmeshSchedulingServiceImpl.BEAN_QUALIFIER)
public class FullmeshSchedulingServiceImpl implements FullmeshSchedulingService{
	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.FullmeshSchedulingServiceImpl";
	
	private static final Logger logger = LogManager.getLogger(FullmeshSchedulingServiceImpl.class);
	
	@Autowired
	private ReflectorInfoRepository reflectorInfoRepository;
	
	@Autowired
	SimpMessagingTemplate template;

	WebSocketClientDTO webSocketClientDTO;
	
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
	
	
	private int repeatCount = 0;
	private int allCount = 0;
	private int successCount = 0;

	private boolean isrunning = false;

	/**
	 * Full Mesh Scheduling
	 * fixedRate = 10000
	 */
//	@Scheduled(fixedRate = 5000)
	public void startScheduling() {
		this.isrunning = true;
		
		List<ReflectorInfo> reflectorData = reflectorInfoRepository.findAll();
		int dstIdx = repeatCount % (reflectorData.size() - 1);
		
		runScheduling(reflectorData, dstIdx);
		
		repeatCount++;
	}
	
	@Scheduled(fixedRate = 5000)
	public void isScheduling() {
		webSocketClientDTO = new WebSocketClientDTO();
		if (this.isrunning) {
			webSocketClientDTO.setMessage("Fullmesh is Running");
			webSocketClientDTO.setReflectorTotalNum(allCount);
			webSocketClientDTO.setReflectorCompleteNum(successCount);
			template.convertAndSend("/fullmesh/running", webSocketClientDTO);
			logger.info("FullMesh is Running ...");
		} else {
			webSocketClientDTO.setMessage("Fullmesh is Stop");
			template.convertAndSend("/fullmesh/stop", webSocketClientDTO);
			logger.info("FullMesh is Stop ...");
		}
	}
	
	public boolean getSchedulingisRunning() {
		return this.isrunning;
	}
	
	public void setSchedulingisRunning(boolean isrunning) {
		this.isrunning = isrunning;
	}
	
	public void runScheduling(List<ReflectorInfo> reflectorData, int dstIdx) {
		logger.info("=== Scheduling Start ===");
		List<TargetServerInfo> result = new ArrayList<TargetServerInfo>();
		int check = 0;
		
		for (int i = 0; i < reflectorData.size(); i++) {
			TargetServerInfo ts = new TargetServerInfo();
			int count = i + dstIdx + 1;
			if (count >= 120) {
				count -= reflectorData.size();
			}
			if (i != count) {				
				String reflectorIp = reflectorData.get(i).getReflectorIp();
				String dstIp = reflectorData.get(count).getReflectorIp();
				StringBuffer srcIp = new StringBuffer();
				srcIp.append(vmHostip);
				srcIp.append(reflectorIp.split("\\.")[2]);
				srcIp.append(".");
				srcIp.append(reflectorIp.split("\\.")[3]);
				
				StringBuffer sid = new StringBuffer();
				sid.append("30");
				sid.append(reflectorIp.split("\\.")[2]);
				sid.append(reflectorIp.split("\\.")[3]);
				sid.append(dstIp.split("\\.0.")[1].split("\\.")[0]);
				sid.append(dstIp.split("\\.0.")[1].split("\\.")[1]);
				
				StringBuffer command = new StringBuffer();
				command.append(twampSenderDir);
				command.append("/run-sender-reflectorip.sh");
				command.append(" -s " + sid.toString());
				command.append(" -H " + eshost);
				command.append(" -E " + esport);
				command.append(" -R " + dstIp);
				
				ts.setTargetIP(srcIp.toString());
				ts.setTargetPort(22);
				ts.setTimeout(5000);
				ts.setUserName(vmUsername);
				ts.setPassword(vmPassword);
				
				ts.setCommand(command.toString());
				
				result.add(ts);
			} else {
				logger.info("Source IP와 Destination IP가 같습니다.");
				check = 1;
				break;
			}
		}
		if (check == 0) {			
			logger.info("Dst Index = " + dstIdx + " , Repeat Count = " + repeatCount);
			String url = "http://" + cnchost + ":" + cncport + "/execute";
			StringBuffer response = sendCNC(convertJSONString(result), url);
			
			allCount = reflectorData.size();
			successCount = StringUtils.countOccurrencesOf(response.toString(), "[OK] Start");
			
		}
		logger.info("=== Scheduling End ===");
	}
	
	private String convertJSONString(List<TargetServerInfo> result) {
		Gson gson = new Gson();
		return gson.toJson(result);
	}
	
	private StringBuffer sendCNC(String data, String url) {
		StringBuffer response = new StringBuffer();
		try {
			URL esurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) esurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			osw.write(data);
			osw.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			
			osw.close();
			br.close();
			conn.disconnect();
			
		} catch (ConnectException e) {
			logger.error("Connection refused: connect");
			webSocketClientDTO = new WebSocketClientDTO();
			webSocketClientDTO.setMessage("Fullmesh Error : Connection refused");
			webSocketClientDTO.setType("Connection refused");
			template.convertAndSend("/fullmesh/error", webSocketClientDTO);
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
		return response;
	}
	
}
