package net.mmp.center.webapp.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.mmp.center.webapp.dto.CurrentStatusDTO;
import net.mmp.center.webapp.dto.CurrentStatusDTO.CurrentStatusResultDTO;
import net.mmp.center.webapp.model.ResponseData;
import net.mmp.center.webapp.service.CurrentStatusService;
import net.mmp.center.webapp.service.MessagesService;
import net.mmp.center.webapp.service.impl.CurrentStatusServiceImpl;
import net.mmp.center.webapp.service.impl.MessagesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
			int resultObj = currentStatusService.QualityMeasureRegister(currentStatusDTO);
			responseData.setType(resultObj);
			responseData.setMessage(message.get("responseData.message.insert.ok", response));
			return new ResponseEntity<ResponseData>(responseData, HttpStatus.CREATED);
		}
	}

	/**
	 * 품질 측정 목록 조회
	 * 
	 * @return 측정 목록 List Data
	 */
	@RequestMapping(value = "/current-status", method = RequestMethod.GET)
	public ResponseEntity<ResponseData> currentStatusListPageable(Pageable pageable, HttpServletResponse response) {
		PageImpl<CurrentStatusDTO> resultObj = currentStatusService.currentStatusList(pageable);

		ResponseData responseData = new ResponseData();

		responseData.setType(1);
		responseData.setMessage(message.get("responseData.message.search.pageable.ok", response));
		responseData.setResult(resultObj);

		return new ResponseEntity<ResponseData>(responseData, HttpStatus.OK);

	}

	/**
	 * 품질 측정 중지
	 * 
	 * @param sessId
	 *            중지할 Session ID
	 * @return 중지 결과
	 */
//	@RequestMapping(value = "/current-status/{sessId}/stop-measure", method = RequestMethod.POST)
//	public ResponseEntity<ResponseData> qualityMeasurementStop(@PathVariable int sessId, HttpServletResponse response) {
//		int resultObj = currentStatusService.QualityMeasureStop(sessId);
//
//		ResponseData responseData = new ResponseData();
//
//		responseData.setType(resultObj);
//		responseData.setMessage(message.get("responseData.message.stop.ok", response));
//
//		return new ResponseEntity<ResponseData>(responseData, HttpStatus.OK);
//	}

	/**
	 * 품질측정 완료 처리
	 * 
	 * @param sessId
	 *            Session ID
	 * @param currentStatusDTO
	 *            품질 측정 DTO
	 * @return
	 */
	@RequestMapping(value = "/current-status/complete", method = RequestMethod.PUT)
	public ResponseEntity<ResponseData> PidRegister(
																		@RequestBody @Valid CurrentStatusResultDTO currentStatusResultDTO,
																		final BindingResult result, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		
		if (result.hasErrors()) {
			throw new net.mmp.center.webapp.exception.BadValidationException(result.getFieldError());
		} else {		
			int resultObj = currentStatusService.measurementComplete(currentStatusResultDTO);
			responseData.setType(resultObj);
			responseData.setMessage(message.get("responseData.message.complete.ok", response));
			return new ResponseEntity<ResponseData>(responseData, HttpStatus.OK);
		}
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
}
