package eu.tankernn.chat.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BanNote {
	String ip, reason;
	LocalDateTime expiry;
	
	public BanNote(String ip, int duration, String reason) {
		this.ip = ip;
		if (duration == -1)
			this.expiry = null;
		
		else
			this.expiry = LocalDateTime.now().plus(duration, ChronoUnit.SECONDS);
		this.reason = reason;
	}
	
	public BanNote(String ip, String reason) {
		this(ip, -1, reason);
	}
	
	public BanNote(String ip, int duration) {
		this(ip, duration, "You are banned.");
	}
	
	public BanNote(String ip) {
		this(ip, -1);
	}
	
	@Override
	public String toString() {
		String expStr = "Never";
		if (expiry != null)
			expStr = expiry.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		
		return "You are banned from this server." + "\n" + "Reason: " + reason + "\n" + "Expiry: " + expStr;
	}
}
