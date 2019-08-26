package net.mmp.center.webapp.thread;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.google.gson.Gson;

public class FullmeshThread extends Thread{
	private static final Logger logger = LogManager.getLogger(FullmeshThread.class);
	
	@Autowired
	SimpMessagingTemplate template;

	WebSocketClientDTO webSocketClientDTO;
	
	public static int STATUS_INIT = -1;

	public static int STATUS_FINISH = 0;

	public static int STATUS_RUNNING = 1;

	private int currentStatus = -1;
	
	
	private String url;
	
	List<ReflectorInfo> reflectorData;
	
	boolean ClearCacheMemory = false;
	
	private int executeCount = 0;
	
	private int repeatCount = 0;
	
	private int dstIdx = 0;
	
	private String eshost;
	
	private String esport;
	
	private String vmUsername;
	
	private String vmPassword;
	
	private String vmHostip;
	
	private String twampSenderDir;
	
	public FullmeshThread(String url, List<ReflectorInfo> reflectorData, int dstIdx, String eshost, String esport, String vmUsername, String vmPassword, String vmHostip, String twampSenderDir, int repeatCount, SimpMessagingTemplate template) {
		this.url = url;
		this.reflectorData = reflectorData;
		this.dstIdx = dstIdx;
		this.eshost = eshost;
		this.esport = esport;
		this.vmUsername = vmUsername;
		this.vmUsername = vmPassword;
		this.vmHostip = vmHostip;
		this.twampSenderDir = twampSenderDir;
		this.repeatCount = repeatCount;
		webSocketClientDTO = new WebSocketClientDTO();
		this.template = template;
	}
	
	public FullmeshThread(boolean CleaarCacheMemory) {
		this.ClearCacheMemory = CleaarCacheMemory;
	}
	
	
	public void run() {
		logger.info("=== Thread Start ===");
		currentStatus = STATUS_RUNNING;
		List<TargetServerInfo> result = new ArrayList<TargetServerInfo>();
		String dstIp = reflectorData.get(dstIdx).getReflectorIp();
		for (int i = 0; i < reflectorData.size(); i++) {
			TargetServerInfo ts = new TargetServerInfo();
			
			String reflectorIp = reflectorData.get(i).getReflectorIp();	
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
		}
		
		logger.info("Dst IP = " + dstIp + " , Repeat Count = " + repeatCount);
		
		sendCNC(convertJSONString(result));
		currentStatus = STATUS_FINISH;
		logger.info("=== Thread End ===");
	}
	
	public int getThreadStatus() {
		return currentStatus;
	}

	public void setThreadStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}

	public int getExecuteCount() {
		return executeCount;
	}
	
	
	private String convertJSONString(List<TargetServerInfo> result) {
		Gson gson = new Gson();
		return gson.toJson(result);
	}
	
	private void sendCNC(String data) {
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
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			
			osw.close();
			br.close();
			conn.disconnect();
			
			executeCount += 1;
			
		} catch (ConnectException e) {
			logger.error("Connection refused: connect");
			webSocketClientDTO.setMessage("Fullmesh Error : Connection refused");
			webSocketClientDTO.setType("Connection refused");
			template.convertAndSend("/fullmesh/error", webSocketClientDTO);
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
