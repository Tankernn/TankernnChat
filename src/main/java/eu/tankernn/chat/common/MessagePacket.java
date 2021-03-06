package eu.tankernn.chat.common;

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
	private static final long serialVersionUID = 91062579797334511L;

	public enum MessageType {
		PM, NORMAL, WARNING, ERROR, COMMAND, INFO
	}

	public MessageType messType = MessageType.NORMAL;
	public final String content, sender;
	private String channel;
	public final SimpleAttributeSet style = new SimpleAttributeSet();

	public MessagePacket(String channel, String send, String con, MessageType messType) {
		this.sender = send;
		this.setChannel(channel);
		this.content = con;
		this.messType = messType;
	}

	public MessagePacket(String con, MessageType messType) {
		this("", "", con, messType);
	}

	public MessagePacket(String sender, String con) {
		this("", sender, con, MessageType.NORMAL);
	}

	public MessagePacket(String con) {
		this("Info", "SERVER", con, MessageType.INFO);
	}

	public boolean validate() {
		return content != null && !content.isEmpty();
	}

	@Override
	public String toString() {
		return toString(true);
	}

	public String toString(boolean includeTimeStamp) {
		boolean preInfo = false;

		switch (messType) {
		case COMMAND:
			StyleConstants.setForeground(style, Color.MAGENTA);
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
			String timestamp = "";
			if (includeTimeStamp) {
				DateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");
				Date time = new Date();
				timestamp = dateFormat.format(time);
			}
			String preInfoStr = timestamp + "<" + getChannel() + ">" + sender + ": ";
			return preInfoStr + content;
		} else
			return content;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
}