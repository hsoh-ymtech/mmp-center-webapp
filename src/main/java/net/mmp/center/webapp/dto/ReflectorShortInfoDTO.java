package net.mmp.center.webapp.dto;

import lombok.Getter;

import java.io.Serializable;

public class ReflectorShortInfoDTO  {
    @Getter
    private String meshId;
    @Getter
    private String ipAddress;
    @Getter
    private int port;

    public ReflectorShortInfoDTO(String meshId, String ipAddress, int port) {
        this.meshId = meshId;
        this.ipAddress = ipAddress;
        this.port = port;
    }
}