package eu.tankernn.chat.packets.filesend;

import eu.tankernn.chat.packets.Packet;

public class FileSendDataPacket implements Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int BUFFER_SIZE = 1024 * 512;

	public byte[] data;

	public FileSendDataPacket(byte[] data) {
		this.data = data;
	}

	public String toString() {
		return "FileSend Data Packet, size: " + data.length;
	}
}
