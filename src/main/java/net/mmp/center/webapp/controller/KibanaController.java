package net.mmp.center.webapp.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.mmp.center.webapp.dto.KibanaInfoDTO;
import net.mmp.center.webapp.dto.KibanaInfoDTO.AddKibanaInfoDTO;
import net.mmp.center.webapp.dto.WebSocketClientDTO;
import net.mmp.center.webapp.exception.NotFoundException;
import net.mmp.center.webapp.model.ResponseData;
import net.mmp.center.webapp.service.KibanaService;
import net.mmp.center.webapp.service.MessagesService;
import net.mmp.center.webapp.service.impl.KibanaServiceImpl;
import net.mmp.center.webapp.service.impl.MessagesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KibanaController {
	private static final Logger logger = LogManager.getLogger(KibanaController.class);
	
	public final static int RESULT_OK = 1;
	public final static int RESULT_FAIL = 0;
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@Autowired
	@Qualifier(MessagesImpl.BEAN_QUALIFIER)
	MessagesService message;

	@Autowired
	@Qualifier(KibanaServiceImpl.BEAN_QUALIFIER)
	private KibanaService kibanaService;
	
	
	/**
	 * Kibana Info 저장
	 * 
	 * @param kibanaInfoDTO
	 *            							Kibana DTO
	 * @param result
	 * 										Validation result
	 * @return 
	 * 										등록 결과
	 */
	@RequestMapping(value = "/kibana", method = RequestMethod.POST)
	public ResponseEntity<ResponseData> RegisterKibanaInfoAndDashboard(@RequestBody @Valid AddKibanaInfoDTO addkibanaInfoDTO,
			final BindingResult result, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();

		if (result.hasErrors()) {
			throw new net.mmp.center.webapp.exception.BadValidationException(result.getFieldError());
		} else {
			int resultObj = kibanaService.RegisterOrChangeKibanaInfo(addkibanaInfoDTO);
			responseData.setType(resultObj);
			responseData.setMessage(message.get("responseData.message.insert.ok", response));
			return new ResponseEntity<ResponseData>(responseData, HttpStatus.CREATED);
		}
	}
	
	/**
	 * Kibana Dashboard 생성
	 * @param response
	 * @return
	 * 									ResponseEntity
	 */
	@RequestMapping(value = "/kibana/dashboard", method = RequestMethod.POST)
	public ResponseEntity<ResponseData> AddDashboard(@RequestBody WebSocketClientDTO webSocketClientDTO, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();

		int resultObj = kibanaService.addKibanaDashboard(template, webSocketClientDTO);
		responseData.setType(resultObj);
		responseData.setMessage(message.get("responseData.message.insert.ok", response));
		return new ResponseEntity<ResponseData>(responseData, HttpStatus.CREATED);
		
	}
	
	/**
	 * Kibana Visualization 생성
	 * @param response
	 * @return
	 *  								ResponseEntity
	 */
	@RequestMapping(value = "/kibana/visualization", method = RequestMethod.POST)
	public ResponseEntity<ResponseData> AddVisualization(@RequestBody WebSocketClientDTO webSocketClientDTO, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();

		int resultObj = kibanaService.addKibanaVisualization(template, webSocketClientDTO);
		responseData.setType(resultObj);
		responseData.setMessage(message.get("responseData.message.insert.ok", response));
		return new ResponseEntity<ResponseData>(responseData, HttpStatus.CREATED);
		
	}
	
	/**
	 * Kibana Info 조회
	 * @param pageable
	 * 									Pageable 속성
	 * @param response
	 * 									Response
	 * @return
	 * 									Data List
	 * 
	 */
	@RequestMapping(value = "/kibana", method = RequestMethod.GET)
	public ResponseEntity<ResponseData> getAllKibanaUrl(Pageable pageable, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();

		PageImpl<KibanaInfoDTO> resultObj = kibanaService.getListKibanaInfo(pageable);
		responseData.setType(RESULT_OK);
		responseData.setMessage(message.get("responseData.message.insert.ok", response));
		responseData.setResult(resultObj);
		return new ResponseEntity<ResponseData>(responseData, HttpStatus.OK);
	}
	
	@ExceptionHandler(value = NotFoundException.class)
	public ResponseEntity<ResponseData> NotFoundException(NotFoundException e, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------NotFoundException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.NotFound.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
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
}
