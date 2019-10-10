package net.mmp.center.webapp.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.mmp.center.webapp.dto.ExternalQualityHistoryDTO;
import net.mmp.center.webapp.dto.ExternalQualityHistorySearchDTO;
import net.mmp.center.webapp.model.ESData;
import net.mmp.center.webapp.model.ResponseData;
import net.mmp.center.webapp.service.ExternalRequestService;
import net.mmp.center.webapp.service.MessagesService;
import net.mmp.center.webapp.service.impl.ExternalRequestServiceImpl;
import net.mmp.center.webapp.service.impl.MessagesImpl;

@RestController
@CrossOrigin(origins="*")
public class ExternalRequestController {
	private static final Logger logger = LogManager.getLogger(ExternalRequestController.class);
	
	@Autowired
	@Qualifier(MessagesImpl.BEAN_QUALIFIER)
	MessagesService message;
	
	@Autowired
	@Qualifier(ExternalRequestServiceImpl.BEAN_QUALIFIER)
	private ExternalRequestService externalRequestService;
	
	@RequestMapping(value = "/v1/external/quality", method = RequestMethod.GET)
	public ResponseEntity<ResponseData> getQualityHistoryRecent(Pageable pageable, @ModelAttribute ExternalQualityHistorySearchDTO dto, final BindingResult result, HttpServletResponse response) {
		ESData resultObj = externalRequestService.getQualityHistoryRecent(dto);
		
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
	
	@RequestMapping(value = "/v1/external/qualityStatistics", method = RequestMethod.GET)
	public ResponseEntity<ResponseData> getQualityHistoryStatistics(Pageable pageable, @ModelAttribute ExternalQualityHistorySearchDTO dto, final BindingResult result, HttpServletResponse response) {
		ExternalQualityHistoryDTO resultObj = externalRequestService.getQualityHistoryStatistics(pageable, dto);
		
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
}
