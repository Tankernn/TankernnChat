package common;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import server.Server;

@SuppressWarnings("serial")
public class Message implements java.io.Serializable {
	public String content, channel, sender;
	public SimpleAttributeSet style = new SimpleAttributeSet();
	public String[] usersOnline;
	
	public Message(String channel, String send, String con) {
		this.sender = send;
		this.channel = channel;
		this.content = con;
		usersOnline = Server.getUsersOnline();
	}
	
	public Message(String send, String con) {
		this("Info", send, con);
	}
	
	public Message(String con) {
		this("SERVER", con);
	}
	
	public boolean validate() {
		if (content.equals("") || content == null) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		if (channel.equals("PM"))
			StyleConstants.setForeground(style, Color.GRAY);
		else if (channel.equals("Info"))
			StyleConstants.setForeground(style, Color.BLUE);
		
		DateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");
		Date time = new Date();
		String timestamp = dateFormat.format(time);
		
		String messEntry;
		
		messEntry =  timestamp + "<" + channel + ">" + sender + ": " + content;
		
		return messEntry;
	}
}