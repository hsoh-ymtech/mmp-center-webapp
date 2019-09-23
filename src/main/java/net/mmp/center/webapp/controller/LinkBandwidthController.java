package net.mmp.center.webapp.controller;

import net.mmp.center.webapp.domain.LinkBandwidth;
import net.mmp.center.webapp.dto.*;
import net.mmp.center.webapp.repository.LinkBandwidthRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class LinkBandwidthController {

	private static final Logger logger = LogManager.getLogger(LinkBandwidthController.class);

	@Autowired
	private LinkBandwidthRepository repository;

	/**
	 * Bootstrap 등록
	 * 
	 * @param linkBandwidthDTO
	 *            Bootstrap DTO
	 * @return 등록 결과S
	 */
	@RequestMapping(value = "/v1/linkBandwidths", method = RequestMethod.POST)
	public ResponseEntity<LinkBandwidthDTO> postBootstrap(@RequestBody @Valid LinkBandwidthDTO linkBandwidthDTO,
		final BindingResult result, HttpServletResponse request, HttpServletResponse response) {

		LinkBandwidth entity = new LinkBandwidth();
		entity.setId(null);
		entity.setBandwidth(linkBandwidthDTO.getBandwidth());
		entity.setDestIpAddress(linkBandwidthDTO.getDestIpAddress());
		entity.setMeasuredTime(linkBandwidthDTO.getMeasuredTime());//linkBandwidthDTO.getMeasuredTime());
		entity.setMeshId(linkBandwidthDTO.getMeshId());
		entity.setSrcIpAddress(linkBandwidthDTO.getSrcIpAddress());

		LinkBandwidth savedEntity = repository.save(entity);

		LinkBandwidthDTO retval = new LinkBandwidthDTO();
		retval.setId(savedEntity.getId());
		retval.setBandwidth(savedEntity.getBandwidth());
		retval.setDestIpAddress(savedEntity.getDestIpAddress());
		retval.setMeasuredTime(savedEntity.getMeasuredTime());
		retval.setMeshId(savedEntity.getMeshId());
		retval.setSrcIpAddress(savedEntity.getSrcIpAddress());

		return new ResponseEntity<LinkBandwidthDTO>(retval, HttpStatus.CREATED);
	}

    @RequestMapping(value = "/v1/linkBandwidths", method = RequestMethod.GET)
    public ResponseEntity<LinkBandwidthDTO> getBootstrap(@RequestParam(value = "meshId")String meshId,
														 @RequestParam(value = "srcIpAddress")String srcIpAddress,
														 @RequestParam(value = "destIpAddress")String destIpAddress)  {

//        List<LinkBandwidth> list = repository.findAll();
//    	List<LinkBandwidth> list = repository.findBySrcIpAddressAndDestIpAddress(srcIpAddress, destIpAddress);
    	List<LinkBandwidth> list = repository.findTop1000BySrcIpAddressAndDestIpAddressOrderByMeasuredTimeDesc(srcIpAddress, destIpAddress);
        float avgLinkBandwidth = 0.0f;
        
        float totalLinkBandwidth = 0.0f;
        for(LinkBandwidth item : list) {
        	totalLinkBandwidth += item.getBandwidth();
        }
        
        if (list.size() == 0) {
        	avgLinkBandwidth = 0.0f;
        } else {
        	avgLinkBandwidth = totalLinkBandwidth / list.size();
        }
        
        LinkBandwidthDTO retval = new LinkBandwidthDTO();
        retval.setMeshId(meshId);
        retval.setBandwidth(avgLinkBandwidth);
        retval.setSrcIpAddress(srcIpAddress);
        retval.setDestIpAddress(destIpAddress);

        return new ResponseEntity<LinkBandwidthDTO>(retval, HttpStatus.OK);
    }

    @RequestMapping(value = "/v1/linkLossRates", method = RequestMethod.GET)
    public ResponseEntity<LinkLossRateDTO> getLinkLossRate(@RequestParam(value = "meshId")String meshId,
                                                           @RequestParam(value = "srcIpAddress")String srcIpAddress,
                                                           @RequestParam(value = "destIpAddress")String destIpAddress)  {
	    // ES와 연결하여 관련 정보를 가지고 온다.

        // TODO:
        // 계산한다.
        int totalLossPacketCount = 0;
        int totalMeasuredPacketCount = 100;
        float lossRate = 0.1f;

        LinkLossRateDTO retval = new LinkLossRateDTO();
        retval.setMeshId(meshId);
        retval.setLossRate(lossRate);
        retval.setTotalLossPacketCount(totalLossPacketCount);
        retval.setTotalMeasuredPacketCount(totalMeasuredPacketCount);
        retval.setSrcIpAddress(srcIpAddress);
        retval.setDestIpAddress(destIpAddress);

        return new ResponseEntity<LinkLossRateDTO>(retval, HttpStatus.OK);
    }
}
