package net.mmp.center.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.mmp.center.webapp.domain.ReflectorInfo;
import net.mmp.center.webapp.model.ResponseData;
import net.mmp.center.webapp.service.MessagesService;
import net.mmp.center.webapp.service.SenderService;
import net.mmp.center.webapp.service.impl.MessagesImpl;
import net.mmp.center.webapp.service.impl.SenderServiceImpl;

@RestController
public class SenderController {
	private static final Logger logger = LogManager.getLogger(ReflectorController.class);
	
	@Autowired
	@Qualifier(MessagesImpl.BEAN_QUALIFIER)
	MessagesService message;
	
	@Autowired
	@Qualifier(SenderServiceImpl.BEAN_QUALIFIER)
	private SenderService senderService;
	
	@RequestMapping(value = "/v1/sender/updateLive", method = RequestMethod.GET)
	public ResponseEntity<ResponseData> updateSenderLive(@RequestParam(value="meshId")String meshId, HttpServletRequest request, HttpServletResponse response) {
		
		 // 업데이트를 요청한 Sender의 공인 IP 확인
        String remoteAddr = request.getHeader("X-FORWARDED-FOR");
		if (remoteAddr == null)
			remoteAddr = request.getRemoteAddr();

		String finalRemoteAddr = remoteAddr;
		
		ResponseData responseData = new ResponseData();
		ReflectorInfo updateSenderInfo = senderService.updateSenderLiveTime(finalRemoteAddr, meshId);
		if (updateSenderInfo == null) {
			logger.info("Not Exist Sender");
			return new ResponseEntity<ResponseData>(responseData, HttpStatus.OK);
		}
		
		responseData.setResult(updateSenderInfo);
		logger.info("Update Sender Live Time");
		return new ResponseEntity<ResponseData>(responseData, HttpStatus.OK);
	}
}
