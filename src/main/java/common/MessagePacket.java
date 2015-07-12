package common;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class MessagePacket implements Packet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum MessageType {
		PM, NORMAL, WARNING, ERROR, COMMAND, INFO
	}
	
	public MessageType messType = MessageType.NORMAL;
	public String content = "", channel = "", sender = "";
	public SimpleAttributeSet style = new SimpleAttributeSet();
	
	public MessagePacket(String channel, String send, String con, MessageType messType) {
		this.sender = send;
		this.channel = channel;
		this.content = con;
		this.messType = messType;
	}
	
	public MessagePacket(String con, MessageType messType) {
		this.content = con;
		this.messType = messType;
	}
	
	public MessagePacket(String sender, String con) {
		this("", sender, con, MessageType.NORMAL);
	}
	
	public MessagePacket(String con) {
		this("Info", "SERVER", con, MessageType.INFO);
	}
	
	public boolean validate() {
		return (!content.equals("")) && content != null;
	}
	
	@Override
	public String toString() {
		boolean preInfo = false;
		
		switch (messType) {
		case COMMAND:
			StyleConstants.setForeground(style, Color.GREEN);
			break;
		case ERROR:
			StyleConstants.setForeground(style, Color.RED);
			break;
		case INFO:
			StyleConstants.setForeground(style, Color.BLUE);
			preInfo = true;
			break;
		case NORMAL:
			preInfo = true;
			break;
		case PM:
			StyleConstants.setForeground(style, Color.GRAY);
			preInfo = true;
			break;
		case WARNING:
			StyleConstants.setForeground(style, Color.YELLOW);
			break;
		default:
			break;
		}
		
		if (preInfo) {
			DateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");
			Date time = new Date();
			String timestamp = dateFormat.format(time);
			
			String preInfoStr = timestamp + "<" + channel + ">" + sender + ": ";
			return preInfoStr + content;
		} else
			return content;
	}
}