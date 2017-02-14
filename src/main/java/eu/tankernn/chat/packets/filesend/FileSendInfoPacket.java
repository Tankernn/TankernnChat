package eu.tankernn.chat.packets.filesend;

import eu.tankernn.chat.packets.Packet;

public class FileSendInfoPacket implements Packet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 259980127139758876L;
	
	public final String filename;
	public final int fileSize;
	public final String destinationUser;
	
	public FileSendInfoPacket(String filename, int fileSize, String destUser) {
		this.filename = filename;
		this.fileSize = fileSize;
		this.destinationUser = destUser;
	}

}
