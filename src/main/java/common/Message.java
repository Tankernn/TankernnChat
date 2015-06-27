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
	
	public enum MessageType {
		PM, NORMAL, WARNING, ERROR, COMMAND, INFO
	}
	
	public MessageType messType = MessageType.NORMAL;
	public String content = "", channel = "", sender = "";
	public SimpleAttributeSet style = new SimpleAttributeSet();
	public String[] usersOnline;
	public boolean preInfo = true;
	
	public Message(String channel, String send, String con, MessageType messType) {
		this.sender = send;
		this.channel = channel;
		this.content = con;
		this.messType = messType;
		usersOnline = Server.getUsersOnline();
	}
	
	public Message(String sender, String con) {
		this("", sender, con, MessageType.NORMAL);
	}
	
	public Message(String con) {
		this("Info", "SERVER", con, MessageType.INFO);
	}
	
	public Message(String con, MessageType messType, boolean preInfo) { //TODO Needs to include Server.getUsersOnline() to prevent NullPointerException
		this.content = con;
		this.preInfo = preInfo; 
		this.messType = messType;
		if (preInfo)
			usersOnline = Server.getUsersOnline();
		else
			usersOnline = null;
	}
	
	public boolean validate() {
		if (content.equals("") || content == null) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		switch (messType) {
		case COMMAND:
			StyleConstants.setForeground(style, Color.GREEN);
			break;
		case ERROR:
			StyleConstants.setForeground(style, Color.RED);
			break;
		case INFO:
			StyleConstants.setForeground(style, Color.BLUE);
			break;
		case NORMAL:
			break;
		case PM:
			StyleConstants.setForeground(style, Color.GRAY);
			break;
		case WARNING:
			StyleConstants.setForeground(style, Color.YELLOW);
			break;
		default:
			break;
		}	
		
		DateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");
		Date time = new Date();
		String timestamp = dateFormat.format(time);
		
		String preInfoStr = timestamp + "<" + channel + ">" + sender + ": ";
		
		if (preInfo)
			return preInfoStr + content;
		else
			return content;
	}
}