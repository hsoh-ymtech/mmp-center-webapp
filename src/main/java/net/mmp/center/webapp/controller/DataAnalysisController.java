package net.mmp.center.webapp.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.mmp.center.webapp.dto.DataAnalysisDTO;
import net.mmp.center.webapp.dto.DataAnalysisDTO.DataataAnalysisResultDTO;
import net.mmp.center.webapp.model.ResponseData;
import net.mmp.center.webapp.service.DataAnalysisService;
import net.mmp.center.webapp.service.MessagesService;
import net.mmp.center.webapp.service.impl.DataAnalysisServiceImpl;
import net.mmp.center.webapp.service.impl.MessagesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataAnalysisController {
	private static final Logger logger = LogManager.getLogger(DataAnalysisController.class);
	
	@Autowired
	@Qualifier(MessagesImpl.BEAN_QUALIFIER)
	MessagesService message;
	
	@Autowired
	@Qualifier(DataAnalysisServiceImpl.BEAN_QUALIFIER)
	private DataAnalysisService dataAnalysisService;
	
	
	/**
	 * ES Server Query Search
	 * 
	 * @param dataAnalysisDTO
	 * @param result
	 * @param response
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/analysis", method = RequestMethod.POST)
	public ResponseEntity<ResponseData> bigdataAnalysis(@RequestBody @Valid DataAnalysisDTO dataAnalysisDTO,
			final BindingResult result, HttpServletResponse response, Pageable pageable) {
		ResponseData responseData = new ResponseData();

		if (result.hasErrors()) {
			throw new net.mmp.center.webapp.exception.BadValidationException(result.getFieldError());
		} else {
			DataataAnalysisResultDTO resultObj = dataAnalysisService.AnalysisElasticSearchData(dataAnalysisDTO, pageable);
			responseData.setType(1);
			responseData.setMessage(message.get("responseData.message.insert.ok", response));
			responseData.setResult(resultObj);
			return new ResponseEntity<ResponseData>(responseData, HttpStatus.CREATED);
		}
	}
	
	
	@ExceptionHandler(value = net.mmp.center.webapp.exception.ParseException.class)
	public ResponseEntity<ResponseData> parseException(net.mmp.center.webapp.exception.ParseException e,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------ParseException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.Parse.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
	}
	

	@ExceptionHandler(value = net.mmp.center.webapp.exception.MalformedURLException.class)
	public ResponseEntity<ResponseData> malformedURLException(net.mmp.center.webapp.exception.MalformedURLException e,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------MalformedURLException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.MalformedURL.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
	}
	
	@ExceptionHandler(value = net.mmp.center.webapp.exception.UnsupportedEncodingException.class)
	public ResponseEntity<ResponseData> unsupportedEncodingException(net.mmp.center.webapp.exception.UnsupportedEncodingException e,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------UnsupportedEncodingException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.UnsupportedEncoding.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
	}
	
	@ExceptionHandler(value = net.mmp.center.webapp.exception.JsonProcessingException.class)
	public ResponseEntity<ResponseData> jsonProcessingException(net.mmp.center.webapp.exception.JsonProcessingException e,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------JsonProcessingException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.JsonProcessing.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
	}
	
	@ExceptionHandler(value = net.mmp.center.webapp.exception.IOException.class)
	public ResponseEntity<ResponseData> ioException(net.mmp.center.webapp.exception.IOException e,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------IOException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.IO.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
	}
}
