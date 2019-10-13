package net.mmp.center.webapp.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.mmp.center.webapp.dto.CurrentStatusDTO;
import net.mmp.center.webapp.model.ESData;
import net.mmp.center.webapp.model.ResponseData;
import net.mmp.center.webapp.service.CurrentStatusService;
import net.mmp.center.webapp.service.MessagesService;
import net.mmp.center.webapp.service.impl.CurrentStatusServiceImpl;
import net.mmp.center.webapp.service.impl.MessagesImpl;

@RestController
public class CurrentStatusController {
	private static final Logger logger = LogManager.getLogger(CurrentStatusController.class);

	@Autowired
	@Qualifier(MessagesImpl.BEAN_QUALIFIER)
	MessagesService message;

	@Autowired
	@Qualifier(CurrentStatusServiceImpl.BEAN_QUALIFIER)
	private CurrentStatusService currentStatusService;

	/**
	 * 품질 측정 시작
	 * 
	 * @param currentStatusDTO
	 *            품질 측정 관련 Data
	 * @return 측정 결과
	 */
	@RequestMapping(value = "/current-status", method = RequestMethod.POST)
	public ResponseEntity<ResponseData> qualityMeasurementStart(@RequestBody @Valid CurrentStatusDTO currentStatusDTO,
			final BindingResult result, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();

		if (result.hasErrors()) {
			throw new net.mmp.center.webapp.exception.BadValidationException(result.getFieldError());
		} else {
			ESData resultObj = currentStatusService.QualityMeasureRegister(currentStatusDTO);
			responseData.setType(1);
			responseData.setResult(resultObj);
			responseData.setMessage(message.get("responseData.message.insert.ok", response));
			return new ResponseEntity<ResponseData>(responseData, HttpStatus.CREATED);
		}
	}
	
	@RequestMapping(value = "/serverIp", method = RequestMethod.GET)
	public ResponseEntity<ResponseData> checkServerIpAddress(HttpServletResponse response) throws Exception {
		ResponseData responseData = new ResponseData();
		
		String ipAddress = checkMyIp();
		if (ipAddress == null) {
			InetAddress inet = InetAddress.getLocalHost();
			ipAddress = inet.getHostAddress();
		}
		
		responseData.setType(1);
		responseData.setResult(ipAddress);
		responseData.setMessage(message.get("responseData.message.insert.ok", response));
		return new ResponseEntity<ResponseData>(responseData, HttpStatus.OK);
	}

	@ExceptionHandler(value = net.mmp.center.webapp.exception.NotFoundException.class)
	public ResponseEntity<ResponseData> NotFoundException(net.mmp.center.webapp.exception.NotFoundException e, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------NotFoundException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.NotFound.exception", response));
		return new ResponseEntity<>(responseData, NOT_FOUND);
	}

	@ExceptionHandler(value = net.mmp.center.webapp.exception.NullPointerException.class)
	public ResponseEntity<ResponseData> NullPointerException(net.mmp.center.webapp.exception.NullPointerException e,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------NullPointerException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.NullPointer.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
	}

	@ExceptionHandler(value = net.mmp.center.webapp.exception.IOException.class)
	public ResponseEntity<ResponseData> IOException(net.mmp.center.webapp.exception.IOException e,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------IOException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.IO.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
	}

	@ExceptionHandler(value = net.mmp.center.webapp.exception.BadValidationException.class)
	public ResponseEntity<ResponseData> BadValidationException(net.mmp.center.webapp.exception.BadValidationException e,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------BadValidationException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.BadValidation.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
	}
	
	@ExceptionHandler(value = net.mmp.center.webapp.exception.InterruptedException.class)
	public ResponseEntity<ResponseData> InterruptedException(net.mmp.center.webapp.exception.InterruptedException e,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------InterruptedException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.Interrupted.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
	}
	
	@ExceptionHandler(value = net.mmp.center.webapp.exception.StatusRuntimeException.class)
	public ResponseEntity<ResponseData> StatusRuntimeException(net.mmp.center.webapp.exception.StatusRuntimeException e,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------StatusRuntimeException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.StatusRuntime.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
	}
	
	private String checkMyIp() {
		try {
			URL url = new URL("http://checkip.amazonaws.com");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String externalIp = br.readLine();
			return externalIp;
		} catch (Exception e) {
			return null;
		}
	}
}
