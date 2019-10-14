package net.mmp.center.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TwampMeasureResult {
	@JsonProperty(value = "@timestamp")
	private String timestamp;
	@JsonProperty(value = "@version")
	private String version;
	private String mesh_id;
	private String src_host;
	private String dst_host;
	private String start_time;
	private String end_time;
	private int measurement_count;
	private double elapsed_time;
	private int lost_packets;
	private int duplicate_packets;
	private int outoforder_packets;
	private double delay;
	private double pdv;
	private double ipdv;
	private double inter_delay;
	private double bandwidth;
	private double download_bandwidth;
	private double upload_bandwidth;
	private double ttl;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMesh_id() {
		return mesh_id;
	}

	public void setMesh_id(String mesh_id) {
		this.mesh_id = mesh_id;
	}

	public String getSrc_host() {
		return src_host;
	}

	public void setSrc_host(String src_host) {
		this.src_host = src_host;
	}

	public String getDst_host() {
		return dst_host;
	}

	public void setDst_host(String dst_host) {
		this.dst_host = dst_host;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public int getMeasurement_count() {
		return measurement_count;
	}

	public void setMeasurement_count(int measurement_count) {
		this.measurement_count = measurement_count;
	}

	public double getElapsed_time() {
		return elapsed_time;
	}

	public void setElapsed_time(double elapsed_time) {
		this.elapsed_time = elapsed_time;
	}

	public int getLost_packets() {
		return lost_packets;
	}

	public void setLost_packets(int lost_packets) {
		this.lost_packets = lost_packets;
	}

	public int getDuplicate_packets() {
		return duplicate_packets;
	}

	public void setDuplicate_packets(int duplicate_packets) {
		this.duplicate_packets = duplicate_packets;
	}

	public int getOutoforder_packets() {
		return outoforder_packets;
	}

	public void setOutoforder_packets(int outoforder_packets) {
		this.outoforder_packets = outoforder_packets;
	}

	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		this.delay = delay;
	}

	public double getPdv() {
		return pdv;
	}

	public void setPdv(double pdv) {
		this.pdv = pdv;
	}

	public double getIpdv() {
		return ipdv;
	}

	public void setIpdv(double ipdv) {
		this.ipdv = ipdv;
	}

	public double getInter_delay() {
		return inter_delay;
	}

	public void setInter_delay(double inter_delay) {
		this.inter_delay = inter_delay;
	}

	public double getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}

	public double getTtl() {
		return ttl;
	}

	public void setTtl(double ttl) {
		this.ttl = ttl;
	}

	public double getDownload_bandwidth() {
		return download_bandwidth;
	}

	public void setDownload_bandwidth(double download_bandwidth) {
		this.download_bandwidth = download_bandwidth;
	}

	public double getUpload_bandwidth() {
		return upload_bandwidth;
	}

	public void setUpload_bandwidth(double upload_bandwidth) {
		this.upload_bandwidth = upload_bandwidth;
	}
}
