package net.mmp.center.webapp.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.mmp.center.webapp.domain.ReflectorInfo;
import net.mmp.center.webapp.dto.ReflectorShortInfoDTO;
import net.mmp.center.webapp.dto.ReflectorStatisticsDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import net.mmp.center.webapp.dto.ReflectorInfoDTO;
import net.mmp.center.webapp.dto.ReflectorInfoDTO.ReflectorInfoSearchDTO;
import net.mmp.center.webapp.exception.AlreadyExistException;
import net.mmp.center.webapp.exception.NotFoundException;
import net.mmp.center.webapp.model.ResponseData;
import net.mmp.center.webapp.service.MessagesService;
import net.mmp.center.webapp.service.ReflectorService;
import net.mmp.center.webapp.service.impl.MessagesImpl;
import net.mmp.center.webapp.service.impl.ReflectorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ReflectorController {

	private static final Logger logger = LogManager.getLogger(ReflectorController.class);

	@Autowired
	@Qualifier(MessagesImpl.BEAN_QUALIFIER)
	MessagesService message;

	@Autowired
	@Qualifier(ReflectorServiceImpl.BEAN_QUALIFIER)
	private ReflectorService reflectormanagementService;

	/**
	 * Reflector 등록
	 * 
	 * @param reflectorInfoDTO
	 *            Reflector DTO
	 * @return 등록 결과
	 */
	@RequestMapping(value = "/reflector", method = RequestMethod.POST)
	public ResponseEntity<ResponseData> reflectorRegister(@RequestBody @Valid ReflectorInfoDTO reflectorInfoDTO,
			final BindingResult result, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();

		if (result.hasErrors()) {
			throw new net.mmp.center.webapp.exception.BadValidationException(result.getFieldError());
		} else {
			int resultObj = reflectormanagementService.reflectorRegister(reflectorInfoDTO);
			responseData.setType(resultObj);
			responseData.setMessage(message.get("responseData.message.insert.ok", response));
			return new ResponseEntity<ResponseData>(responseData, HttpStatus.CREATED);
		}
	}

	/**
	 * Reflector 조회
	 * 
	 * @return Reflectors Data
	 */
	@RequestMapping(value = "/reflectors", method = RequestMethod.GET)
	public ResponseEntity<ResponseData> reflectorListPageable(Pageable pageable, @ModelAttribute @Valid ReflectorInfoSearchDTO reflectorInfoSearchDTO, final BindingResult result, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		
		if (result.hasErrors()) {
			throw new net.mmp.center.webapp.exception.BadValidationException(result.getFieldError());
		} else {
			PageImpl<ReflectorInfoDTO> resultObj = reflectormanagementService.reflectorsListPageable(pageable, reflectorInfoSearchDTO);
			responseData.setType(1);
			responseData.setMessage(message.get("responseData.message.search.pageable.ok", response));
			responseData.setResult(resultObj);

			return new ResponseEntity<ResponseData>(responseData, HttpStatus.OK);
		}
	}

	/**
	 * Reflector 조회
	 *
	 * @return Reflectors Data
	 */
	@RequestMapping(value = "/v1/reflectors", method = RequestMethod.GET)
	public ResponseEntity<List<ReflectorInfo>> reflectorList(HttpServletRequest request,HttpServletResponse response) {
        List<ReflectorInfo> results = reflectormanagementService.reflectorsList();
		if(results == null) {
			results = new ArrayList<ReflectorInfo>();
		}
		return new ResponseEntity<List<ReflectorInfo>>(results, HttpStatus.OK);
	}

    /**
     * 현재 동작하고 있는 Reflector 조회
     *
     * @return Reflectors Data
     */
    @RequestMapping(value = "/v1/activeReflectors", method = RequestMethod.GET)
    public ResponseEntity<List<ReflectorShortInfoDTO>> activeReflectorList(HttpServletRequest request, HttpServletResponse response) {

        List<ReflectorInfo> list = reflectormanagementService.reflectorsList();
        if(list == null) {
            list = new ArrayList<ReflectorInfo>();
        }

        list= list.stream().filter(result -> result.getReflectorIp()!= request.getRemoteAddr())
                    .collect(Collectors.toList());
        List<ReflectorShortInfoDTO> retvals = new ArrayList<ReflectorShortInfoDTO>();

        for (ReflectorInfo info : list) {
            retvals.add(new ReflectorShortInfoDTO(info.getReflectorIp(), info.getPort()));
        }
        return new ResponseEntity<List<ReflectorShortInfoDTO>>(retvals, HttpStatus.OK);
    }

	@RequestMapping(value = "/v1/reflectorStatistics", method = RequestMethod.GET)
	public ResponseEntity<ReflectorStatisticsDTO> reflectorStatistics(HttpServletRequest request, HttpServletResponse response) {

		List<ReflectorInfo> list = reflectormanagementService.reflectorsList();
		if(list == null) {
			list = new ArrayList<ReflectorInfo>();
		}

		int countTotal = list.size();

		int countKR = list.stream().filter(result -> result.getCountry().equals("KR"))
				.collect(Collectors.toList()).size();
		int countUnknown = list.stream().filter(result -> result.getCountry().equals("00"))
				.collect(Collectors.toList()).size();
		int countNotKR = countTotal - countKR - countUnknown;

		ReflectorStatisticsDTO retval = new ReflectorStatisticsDTO(countKR, countNotKR, countUnknown);

		return new ResponseEntity<ReflectorStatisticsDTO>(retval, HttpStatus.OK);
	}

	/**
	 * Remote Ip Address 조회
	 *
	 * @return Remote Ip Address
	 */
	@RequestMapping(value = "/v1/checkip", method = RequestMethod.GET)
	public ResponseEntity<String> checkIpAddress(HttpServletRequest request, HttpServletResponse response) {
		//String result = request.getRemoteAddr();
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null)
			ip = request.getRemoteAddr();
		return new ResponseEntity<String>(ip, HttpStatus.OK);
	}

	/**
	 * Reflector List - IP 조회
	 * 
	 * @return Reflectors Data
	 */
	@RequestMapping(value = "/reflectors/ip", method = RequestMethod.GET)
	public ResponseEntity<ResponseData> reflectorIPList(HttpServletRequest request, HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		
		JSONObject resultObj = reflectormanagementService.reflectorsIPList(request, response);
		responseData.setType(1);
		responseData.setMessage(message.get("responseData.message.search.pageable.ok", response));
		responseData.setResult(resultObj);

		return new ResponseEntity<ResponseData>(responseData, HttpStatus.OK);
	}
	
	/**
	 * Reflector 수정
	 * 
	 * @param reflectorInfoDTO
	 *            Reflector DTO
	 * @return 수정 결과
	 */
	@RequestMapping(value = "/reflector/{reflectorId}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseData> reflectorChange(@PathVariable int reflectorId,
			@RequestBody @Valid ReflectorInfoDTO reflectorInfoDTO, final BindingResult result,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();

		if (result.hasErrors()) {
			throw new net.mmp.center.webapp.exception.BadValidationException(result.getFieldError());
		} else {
			int resultObj = reflectormanagementService.reflectorChange(reflectorInfoDTO);
			responseData.setType(resultObj);
			responseData.setMessage(message.get("responseData.message.update.ok", response));
			return new ResponseEntity<ResponseData>(responseData, HttpStatus.OK);
		}
	}

	/**
	 * Reflector 삭제
	 * 
	 * @param reflectorId
	 *            Reflector Id
	 * @return 삭제 결과
	 */
	@RequestMapping(value = "/reflector/{reflectorId}", method = RequestMethod.DELETE)
	public ResponseEntity<ResponseData> reflectorDelete(@PathVariable int reflectorId, HttpServletResponse response) {

		ResponseData responseData = new ResponseData();

		int resultObj = reflectormanagementService.reflectorDelete(reflectorId);

		responseData.setType(resultObj);
		responseData.setMessage(message.get("responseData.message.delete.ok", response));

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
	
	@ExceptionHandler(value = AlreadyExistException.class)
	public ResponseEntity<ResponseData> AlreadyExistException(AlreadyExistException e,
			HttpServletResponse response) {
		ResponseData responseData = new ResponseData();
		logger.error("--------------AlreadyExistException----------------");
		responseData.setType(0);
		responseData.setResult(e.getInformation());
		responseData.setMessage(message.get("responseData.message.Alreadyexist.exception", response));
		return new ResponseEntity<>(responseData, BAD_REQUEST);
	}
}
