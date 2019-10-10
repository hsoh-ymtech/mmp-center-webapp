package net.mmp.center.webapp.controller;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.mmp.center.webapp.dto.LesionHistoryDTO;
import net.mmp.center.webapp.dto.LesionHistoryDTO.LesionHistorySearchDTO;
import net.mmp.center.webapp.model.ResponseData;
import net.mmp.center.webapp.service.LesionHistoryService;
import net.mmp.center.webapp.service.MessagesService;
import net.mmp.center.webapp.service.impl.LesionHistoryServiceImpl;
import net.mmp.center.webapp.service.impl.MessagesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LesionHistoryController {

	private static final Logger logger = LogManager.getLogger(LesionHistoryController.class);
	
	@Autowired
	@Qualifier(MessagesImpl.BEAN_QUALIFIER)
	MessagesService message;
	
	@Autowired
	@Qualifier(LesionHistoryServiceImpl.BEAN_QUALIFIER)
	private LesionHistoryService lestionHistoryService;
	
	/**
	 * 품질 이력 조회
	 * 
	 * @param lesionHistorySearchDTO
	 *            품질 이력 DTO
	 * @return 조회 List
	 */
	@RequestMapping(value = "/lesion-historys", method = RequestMethod.GET)
	public ResponseEntity<ResponseData> qualityHistoryListPageable(Pageable pageable, @ModelAttribute @Valid LesionHistorySearchDTO lesionHistorySearchDTO, final BindingResult result, HttpServletResponse response) {
		PageImpl<LesionHistoryDTO> resultObj = lestionHistoryService.SearchLesionHistory(pageable, lesionHistorySearchDTO);

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
	
	@ExceptionHandler(value = net.mmp.center.webapp.exception.NotFoundException.class)
	public ResponseEntity<ResponseData> NotFoundException(net.mmp.center.webapp.exception.NotFoundException e, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------NotFoundException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.NotFound.exception", response));
		return new ResponseEntity<>(responseData, NOT_FOUND);
	}
}
