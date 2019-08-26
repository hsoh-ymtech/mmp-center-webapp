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

public class JudgeUnderMeasureThread extends Thread{
	private static final Logger logger = LogManager.getLogger(JudgeUnderMeasureThread.class);
	
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
	
	
	
	
	private boolean isEnd = false;
	
	
	
	
	public JudgeUnderMeasureThread(String url, List<ReflectorInfo> reflectorData, int dstIdx, String eshost, String esport, String username, String password, String twampSenderDir, int repeatCount, SimpMessagingTemplate template) {
		this.url = url;
		this.reflectorData = reflectorData;
		webSocketClientDTO = new WebSocketClientDTO();
		this.template = template;
	}
	
	public JudgeUnderMeasureThread(boolean CleaarCacheMemory) {
		this.ClearCacheMemory = CleaarCacheMemory;
	}
	
	
	public void run() {
		
		while(true) {
			
			
			
			
			
			
			
			
			
			
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (isEnd) {
				break;
			}
		}
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
