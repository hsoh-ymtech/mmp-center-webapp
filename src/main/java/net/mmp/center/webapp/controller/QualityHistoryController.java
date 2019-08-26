package net.mmp.center.webapp.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.mmp.center.webapp.dto.QualityHistoryDTO;
import net.mmp.center.webapp.dto.QualityHistoryDTO.QualityHistorySearchDTO;
import net.mmp.center.webapp.exception.NotFoundException;
import net.mmp.center.webapp.model.ResponseData;
import net.mmp.center.webapp.service.MessagesService;
import net.mmp.center.webapp.service.QualityHistoryService;
import net.mmp.center.webapp.service.impl.MessagesImpl;
import net.mmp.center.webapp.service.impl.QualityHistoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QualityHistoryController {
	private static final Logger logger = LogManager.getLogger(QualityHistoryController.class);
	
	@Autowired
	@Qualifier(MessagesImpl.BEAN_QUALIFIER)
	MessagesService message;

	@Autowired
	@Qualifier(QualityHistoryServiceImpl.BEAN_QUALIFIER)
	private QualityHistoryService qualityHistoryService;

	/**
	 * 품질 이력 등록
	 * 
	 * @param qualityHistoryDTO
	 *            품질 이력 DTO
	 * @return 등록 결과
	 */
	@RequestMapping(value = "/quality-history", method = RequestMethod.POST)
	public ResponseEntity<ResponseData> qualityHistoryRegister(@RequestBody @Valid QualityHistoryDTO qualityHistoryDTO,
			final BindingResult result, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();

		if (result.hasErrors()) {
			throw new net.mmp.center.webapp.exception.BadValidationException(result.getFieldError());
		} else {
			int resultObj = qualityHistoryService.qualityHistoryRegister(qualityHistoryDTO);
			responseData.setType(resultObj);
			responseData.setMessage(message.get("responseData.message.insert.ok", response));
			return new ResponseEntity<ResponseData>(responseData, HttpStatus.CREATED);
		}
	}

	/**
	 * 품질 이력 조회
	 * 
	 * @param qualityHistoryDTO
	 *            품질 이력 DTO
	 * @return 조회 List
	 */
	@RequestMapping(value = "/quality-historys", method = RequestMethod.GET)
	public ResponseEntity<ResponseData> qualityHistoryListPageable(Pageable pageable, @ModelAttribute @Valid QualityHistorySearchDTO qualityHistorySearchDTO, final BindingResult result, HttpServletResponse response) {
		PageImpl<QualityHistoryDTO> resultObj = qualityHistoryService.QualityHistoryList(pageable, qualityHistorySearchDTO);

		ResponseData responseData = new ResponseData();

		if (result.hasErrors()) {
			throw new net.mmp.center.webapp.exception.BadValidationException(result.getFieldError());
		} else {
			responseData.setType(1);
			responseData.setMessage(message.get("responseData.message.search.pageable.ok", response));
			responseData.setResult(resultObj);
			return new ResponseEntity<ResponseData>(responseData, HttpStatus.OK);
		}

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
}
