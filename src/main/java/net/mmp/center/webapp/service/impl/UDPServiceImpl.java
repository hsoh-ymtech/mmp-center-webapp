package net.mmp.center.webapp.service.impl;

import net.mmp.center.webapp.service.UDPService;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class UDPServiceImpl implements UDPService {

    public void handleMessage(Message message)
    {
        String data = new String((byte[]) message.getPayload());
        int port = (Integer) message.getHeaders().get("ip_port");
        String ip = (String) message.getHeaders().get("ip_address");
        System.out.print(data);
        System.out.print(port);
        System.out.print(ip);
    }
}
