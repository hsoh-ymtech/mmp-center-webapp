package net.mmp.center.webapp.model;

public class TwampMeasureResult {

	private String session_id;
	private String start_time;
	private double timestamp;
	private String end_time;
	private double elapsed_time;
	private String src_host;
	private String dst_host;
	private int measurement_count;
	private int measurement_index;
	private int try_count;
	private int up_lost_packets;
	private int down_lost_packets;
	private int up_duplicate_packets;
	private int down_duplicate_packets;
	private int up_outoforder_packets;
	private int down_outoforder_packets;
	private int ttl;
	private Delay inter_delay;
	private Delay up_delay;
	private Delay down_delay;
	private Delay rtt;
	private IPDV ipdv;
	private IPDV up_ipdv;
	private IPDV down_ipdv;
	private PDV pdv;
	private PDV up_pdv;
	private PDV down_pdv;
	
	public static class Delay {
		private double avg;
		private double min;
		private double max;
		
		
		public double getAvg() {
			return avg;
		}
		public void setAvg(double avg) {
			this.avg = avg;
		}
		public double getMin() {
			return min;
		}
		public void setMin(double min) {
			this.min = min;
		}
		public double getMax() {
			return max;
		}
		public void setMax(double max) {
			this.max = max;
		}
	}
	
	public static class IPDV {
		private double ipdv;
		private double min;
		private double max;
		
		
		public double getIpdv() {
			return ipdv;
		}
		public void setIpdv(double ipdv) {
			this.ipdv = ipdv;
		}
		public double getMin() {
			return min;
		}
		public void setMin(double min) {
			this.min = min;
		}
		public double getMax() {
			return max;
		}
		public void setMax(double max) {
			this.max = max;
		}
	}
	
	public static class PDV {
		private double pdv;
		private double min;
		private double max;
		
		
		public double getPdv() {
			return pdv;
		}
		public void setPdv(double pdv) {
			this.pdv = pdv;
		}
		public double getMin() {
			return min;
		}
		public void setMin(double min) {
			this.min = min;
		}
		public double getMax() {
			return max;
		}
		public void setMax(double max) {
			this.max = max;
		}
	}

	
	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public double getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public double getElapsed_time() {
		return elapsed_time;
	}

	public void setElapsed_time(double elapsed_time) {
		this.elapsed_time = elapsed_time;
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

	public int getMeasurement_count() {
		return measurement_count;
	}

	public void setMeasurement_count(int measurement_count) {
		this.measurement_count = measurement_count;
	}

	public int getMeasurement_index() {
		return measurement_index;
	}

	public void setMeasurement_index(int measurement_index) {
		this.measurement_index = measurement_index;
	}

	public int getTry_count() {
		return try_count;
	}

	public void setTry_count(int try_count) {
		this.try_count = try_count;
	}

	public int getUp_lost_packets() {
		return up_lost_packets;
	}

	public void setUp_lost_packets(int up_lost_packets) {
		this.up_lost_packets = up_lost_packets;
	}

	public int getDown_lost_packets() {
		return down_lost_packets;
	}

	public void setDown_lost_packets(int down_lost_packets) {
		this.down_lost_packets = down_lost_packets;
	}

	public int getUp_duplicate_packets() {
		return up_duplicate_packets;
	}

	public void setUp_duplicate_packets(int up_duplicate_packets) {
		this.up_duplicate_packets = up_duplicate_packets;
	}

	public int getDown_duplicate_packets() {
		return down_duplicate_packets;
	}

	public void setDown_duplicate_packets(int down_duplicate_packets) {
		this.down_duplicate_packets = down_duplicate_packets;
	}

	public int getUp_outoforder_packets() {
		return up_outoforder_packets;
	}

	public void setUp_outoforder_packets(int up_outoforder_packets) {
		this.up_outoforder_packets = up_outoforder_packets;
	}

	public int getDown_outoforder_packets() {
		return down_outoforder_packets;
	}

	public void setDown_outoforder_packets(int down_outoforder_packets) {
		this.down_outoforder_packets = down_outoforder_packets;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public Delay getInter_delay() {
		return inter_delay;
	}

	public void setInter_delay(Delay inter_delay) {
		this.inter_delay = inter_delay;
	}

	public Delay getUp_delay() {
		return up_delay;
	}

	public void setUp_delay(Delay up_delay) {
		this.up_delay = up_delay;
	}

	public Delay getDown_delay() {
		return down_delay;
	}

	public void setDown_delay(Delay down_delay) {
		this.down_delay = down_delay;
	}

	public Delay getRtt() {
		return rtt;
	}

	public void setRtt(Delay rtt) {
		this.rtt = rtt;
	}

	public IPDV getIpdv() {
		return ipdv;
	}

	public void setIpdv(IPDV ipdv) {
		this.ipdv = ipdv;
	}

	public IPDV getUp_ipdv() {
		return up_ipdv;
	}

	public void setUp_ipdv(IPDV up_ipdv) {
		this.up_ipdv = up_ipdv;
	}

	public IPDV getDown_ipdv() {
		return down_ipdv;
	}

	public void setDown_ipdv(IPDV down_ipdv) {
		this.down_ipdv = down_ipdv;
	}

	public PDV getPdv() {
		return pdv;
	}

	public void setPdv(PDV pdv) {
		this.pdv = pdv;
	}

	public PDV getUp_pdv() {
		return up_pdv;
	}

	public void setUp_pdv(PDV up_pdv) {
		this.up_pdv = up_pdv;
	}

	public PDV getDown_pdv() {
		return down_pdv;
	}

	public void setDown_pdv(PDV down_pdv) {
		this.down_pdv = down_pdv;
	}
}
