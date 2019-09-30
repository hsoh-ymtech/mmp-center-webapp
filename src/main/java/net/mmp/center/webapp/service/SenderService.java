package net.mmp.center.webapp.service;

import net.mmp.center.webapp.domain.ReflectorInfo;

public interface SenderService {
	ReflectorInfo updateSenderLiveTime(String senderIp, String meshId);
}
