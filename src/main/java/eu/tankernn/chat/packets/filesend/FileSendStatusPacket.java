package eu.tankernn.chat.packets.filesend;

import eu.tankernn.chat.packets.Packet;

public class FileSendStatusPacket implements Packet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7125935757108193810L;
	
	public enum TransferAction {
		ACCEPT, DENY
	}
	
	public final TransferAction action;
	
	public FileSendStatusPacket(TransferAction action) {
		this.action = action;
	}
}
