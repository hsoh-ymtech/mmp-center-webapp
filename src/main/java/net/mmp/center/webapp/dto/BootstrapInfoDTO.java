package net.mmp.center.webapp.dto;

import lombok.Data;

@Data
public class BootstrapInfoDTO {
    private String meshId;
    private int port;
    private String os;
    private String osVersion;
    private String osArch;
    private String publicIpAddress;
    private String outboundIpAddress;
    private String macAddress;
    private Boolean enabled;

    public BootstrapInfoDTO(String meshId, int port, String os, String osVersion, String osArch, String publicIpAddress, String outboundIpAddress, String macAddress, Boolean enabled) {
        this.meshId = meshId;
        this.port = port;
        this.os = os;
        this.osVersion = osVersion;
        this.osArch = osArch;
        this.publicIpAddress = publicIpAddress;
        this.outboundIpAddress = outboundIpAddress;
        this.macAddress = macAddress;
        this.enabled = enabled;
    }
}