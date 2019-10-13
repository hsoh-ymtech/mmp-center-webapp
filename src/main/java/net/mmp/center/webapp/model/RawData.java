package net.mmp.center.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.time.LocalDateTime;

@Document(indexName="${config.twamp.index.name}", type = "doc")
@Data
public class RawData {
    /*
    "inter_delay": 21.2,
    "lost_packets": 0,
    "bandwidth": 0,
    "measurement_count": 5,
    "ipdv": 61.2,
    "pdv": -6.2,
    "ttl": 255,
    "duplicate_packets": 0,
    "mesh_id": "6a4e5d383e69477ec9a4bfdd16cf6315",
    "outoforder_packets": 0,
    "@timestamp": "2019-09-22T02:18:57.171Z",
    "start_time": "2019-09-22 02:20:30.881841034 +0000 UTC m=+29.321003501",
    "src_host": "54.233.138.80",
    "delay": 131488.2,
    "end_time": "2019-09-22 02:20:31.539322381 +0000 UTC m=+29.978484610",
    "@version": "1",
    "elapsed_time": 0.657481109,
    "dst_host": "3.16.89.112"
     */

    @Id
    private String id;
    @JsonProperty("inter_delay")
    private Float interDelay;
    @JsonProperty("lost_packets")
    private Integer lostPackets;
    private Float bandwidth;
    @JsonProperty("measurement_count")
    private Integer measurementCount;
    private Float ipdv;
    private Float pdv;
    private Integer ttl;
    @JsonProperty("duplicate_packets")
    private Integer duplicatePackets;
    @JsonProperty("mesh_id")
    private String meshId;
    @JsonProperty("outoforder_packets")
    private Integer outoforderPackets;
    private LocalDateTime timestamp;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("src_host")
    private String srcHost;
    private Float delay;
    private String version;
    @JsonProperty("end_time")
    private String endTime;
    private Float elapsed_time;
    @JsonProperty("dst_host")
    private String dstHost;
}
