package net.mmp.center.webapp.service.impl;

import net.mmp.center.webapp.service.UDPService;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class UDPServiceImpl implements UDPService {

    public void handleMessage(Message message)
    {
        String data = new String((byte[]) message.getPayload());
        System.out.print(data);
    }
}
