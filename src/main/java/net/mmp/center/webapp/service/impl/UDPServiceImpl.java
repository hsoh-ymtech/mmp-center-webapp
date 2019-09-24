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

        System.out.println("data:"+data);
        System.out.println("port:"+port);
        System.out.println("ip:"+ip);
        /*
              try {
            int port = (Integer) message.getHeaders().get("ip_port");
            String ip = (String) message.getHeaders().get("ip_address");
            InetAddress IPAddress = InetAddress.getByName(ip);

            byte[] sentence = (byte[]) message.getPayload();
            byte[] sendData = new byte[sentence.length];
            byte[] receiveData = new byte[1024];
            sendData = sentence;
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception ex) {
            throw new RuntimeException("KO");
        }
         */
    }
}
