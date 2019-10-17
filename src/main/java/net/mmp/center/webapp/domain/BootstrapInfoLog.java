package net.mmp.center.webapp.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class BootstrapInfoLog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "meshId")
    private String meshId;

    @Column(name = "public_ip")
    private String publicIpAddress;

    @Column(name = "mac")
    private String macAddress;

    @Column(name = "outbound_ip")
    private String outboundIpAddress;

    @Column(name = "write_time")
    private LocalDateTime writeTime;
}