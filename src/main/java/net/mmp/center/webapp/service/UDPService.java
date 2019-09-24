package net.mmp.center.webapp.service;

import org.springframework.messaging.Message;

public interface UDPService {
    void handleMessage(Message message);
}
