package net.mmp.center.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.mmp.center.webapp.domain.ReflectorInfo;
import net.mmp.center.webapp.repository.ReflectorInfoRepository;
import net.mmp.center.webapp.service.SenderService;

@Service(SenderServiceImpl.BEAN_QUALIFIER)
@Transactional
public class SenderServiceImpl implements SenderService {
	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.SenderServiceImpl";

	private static final Logger logger = LogManager.getLogger(ReflectorServiceImpl.class);

	@Autowired
	private ReflectorInfoRepository reflectorInfoRepository;
	
	@Override
	public ReflectorInfo updateSenderLiveTime(String senderIp, String meshId) {
		List<ReflectorInfo> senderList = reflectorInfoRepository.findByReflectorIpAndMeshId(senderIp, meshId);
		if (senderList == null || senderList.isEmpty()) {
			logger.info("Sender is Not Exist");
			return null;
		}
		
		ReflectorInfo senderInfo = senderList.get(0);
		senderInfo.setUpdateTime(LocalDateTime.now());
		ReflectorInfo savedSenderInfo = reflectorInfoRepository.save(senderInfo);
		
		return savedSenderInfo;
	}
}
