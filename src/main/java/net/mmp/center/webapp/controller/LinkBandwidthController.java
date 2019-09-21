package net.mmp.center.webapp.controller;

import net.mmp.center.webapp.domain.LinkBandwidth;
import net.mmp.center.webapp.domain.ReflectorInfo;
import net.mmp.center.webapp.dto.BootstrapInfoDTO;
import net.mmp.center.webapp.dto.LinkBandwidthDTO;
import net.mmp.center.webapp.dto.ProtocolDTO;
import net.mmp.center.webapp.dto.ReflectorInfoDTO;
import net.mmp.center.webapp.repository.LinkBandwidthRepository;
import net.mmp.center.webapp.service.ReflectorService;
import net.mmp.center.webapp.service.impl.ReflectorServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
		entity.setMeasuredTime(LocalDateTime.now());//linkBandwidthDTO.getMeasuredTime());
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

        List<LinkBandwidth> list = repository.findAll();
        float avgLinkBandwidth = 0.0f;

        // TODO:
        for(LinkBandwidth item : list) {
            //평균값 계산
        }

        LinkBandwidthDTO retval = new LinkBandwidthDTO();
        retval.setBandwidth(avgLinkBandwidth);
        retval.setDestIpAddress(destIpAddress);
        retval.setMeshId(meshId);
        retval.setSrcIpAddress(srcIpAddress);

        return new ResponseEntity<LinkBandwidthDTO>(retval, HttpStatus.OK);
    }
}
