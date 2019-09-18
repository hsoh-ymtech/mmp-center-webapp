package net.mmp.center.webapp.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class BootstrapInfoDTO {
    private String meshId;
    private int port;
    private String os;
    private String osVersion;
    private String publicIpAddress;
    private String privateIpAddress;

    public BootstrapInfoDTO(String meshId, int port, String os, String osVersion, String publicIpAddress, String privateIpAddress) {
        this.meshId = meshId;
        this.port = port;
        this.os = os;
        this.osVersion = osVersion;
        this.publicIpAddress = publicIpAddress;
        this.privateIpAddress = privateIpAddress;
    }
}