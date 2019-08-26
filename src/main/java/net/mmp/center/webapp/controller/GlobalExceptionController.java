package net.mmp.center.webapp.controller;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.mmp.center.webapp.model.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionController {

	private static final Logger logger = LogManager.getLogger(GlobalExceptionController.class);

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ResponseData> exceptionHandle(Exception e) {
		final ResponseData resultResponse = new ResponseData();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

		logger.error("[.....] - TWAMP API Server error=");
		
		StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        
		logger.error(writer.toString());
		resultResponse.setMessage(e.getMessage());
		resultResponse.setType(0);

		return new ResponseEntity<>(resultResponse, status);
	}

}
