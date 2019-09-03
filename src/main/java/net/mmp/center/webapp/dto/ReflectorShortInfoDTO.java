package net.mmp.center.webapp.dto;

import java.io.Serializable;

public class ReflectorShortInfoDTO  {
    String ipAddress;
    int port;

    public ReflectorShortInfoDTO(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }
}