package net.pakddo.UrlFeatureExtractor;

public class TimedContent {
	/**
	 * data object TimedContent
	 * 
	 * time, code, content, duration, PNvalue  
	 */
	
	@Override
	public String toString() {
		return "TimedContent [time=" + time + ", duration=" + duration
				+ ", code=" + code + ", content=" + content + ", PNvalue="
				+ PNvalue + "]";
	}
	
	private long time;
	private long duration;
	private String code;
	private String content;
	private String PNvalue;


	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPNvalue() {
		return PNvalue;
	}
	public void setPNvalue(String pNvalue) {
		PNvalue = pNvalue;
	}
	
	
}
