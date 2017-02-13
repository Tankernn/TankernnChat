package eu.tankernn.chat.common;

import common.Packet;

public class FileSendPacket implements Packet {
	public static final int BUFFER_SIZE = 1024;
	
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
	public byte[] data;
	
	public FileSendPacket(String filename, int fileSize, String destUser, TransferAction action) {
		this.filename = filename;
		this.fileSize = fileSize;
		this.destinationUser = destUser;
		this.action = action;
	}
	
	public FileSendPacket(byte[] data) {
		this.data = data;
	}

}
