package net.mmp.center.webapp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import net.mmp.center.webapp.dto.WebSocketClientDTO;

public class ProcessRun implements Runnable{
	
	private static final Logger logger = LogManager.getLogger(ProcessRun.class);
	
	List<String> command;
	String directory;
	
	@Autowired
	SimpMessagingTemplate template;
	WebSocketClientDTO webSocketClientDTO;
	
	public ProcessRun(List<String> command, String directory) {
		this.command = command;
		this.directory = directory;
	}
	
	public ProcessRun(List<String> command, String directory, SimpMessagingTemplate template, WebSocketClientDTO webSocketClientDTO) {
		this.command = command;
		this.directory = directory;
		this.webSocketClientDTO = webSocketClientDTO;
		this.template = template;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Process process = null;
		ProcessBuilder builder = new ProcessBuilder(this.command);
		logger.debug("command = " + this.command);
		logger.debug("directory = " + this.directory);
		if (this.directory != null) {
			builder.directory(new File(this.directory));
		}
		try {
			process = builder.start();
			System.out.println("Process Start");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("IO Exception Process = " + e.getMessage());
			throw new net.mmp.center.webapp.exception.IOException("IO Exception Process = " + process);
		}
		logger.info(command);

		
		String line;
		boolean serverRefused = false;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
			while ((line = reader.readLine()) != null) {
				if (line.indexOf("Connection refused") != -1) {
					serverRefused = true;
					logger.info("Server Connection refused");
					process.destroy();
				}
				logger.info(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (serverRefused) {
				webSocketClientDTO.setMessage("서버와 연결이 거부되었습니다.");
				template.convertAndSend("/process/serverrefused", webSocketClientDTO);
				throw new net.mmp.center.webapp.exception.IOException("서버와 연결이 거부되었습니다.");
			}
			throw new net.mmp.center.webapp.exception.IOException("IO Exception Locate BufferedReader");
		}

		try {
			if (0 != process.waitFor()) {
				logger.info("Command Execute Fail");
				logger.info(command);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new net.mmp.center.webapp.exception.InterruptedException("InterruptedException Process = " + process);
		}
		int processExitValue = process.exitValue();
		
		if (processExitValue != 0) {
			
		}
		if (null != process) {
			process.destroy();
		}
		if (webSocketClientDTO != null) {
			webSocketClientDTO.setMessage("정상적으로 생성되었습니다.");
			template.convertAndSend("/process/success", webSocketClientDTO);
		}
	}
	
	
}
