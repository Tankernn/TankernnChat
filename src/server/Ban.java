package server;

public class Ban {
	String ip, reason;
	int duration;
	public Ban (String ip, int duration, String reason) {
		this.ip = ip;
		this.duration = duration;
		this.reason = reason;
	}
}
