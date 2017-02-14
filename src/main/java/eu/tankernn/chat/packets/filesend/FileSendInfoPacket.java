package eu.tankernn.chat.packets.filesend;

import eu.tankernn.chat.packets.Packet;

public class FileSendInfoPacket implements Packet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 259980127139758876L;

	public enum TransferAction {
		INIT, START, DENY, DATA
	}
	
	public String filename;
	public int fileSize;
	public String destinationUser;
	public TransferAction action;
	
	public FileSendInfoPacket(String filename, int fileSize, String destUser, TransferAction action) {
		this.filename = filename;
		this.fileSize = fileSize;
		this.destinationUser = destUser;
		this.action = action;
	}

}
