package eu.tankernn.chat.packets;

public class StringPacket implements Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -884270769061764381L;
	private String data;
	
	public StringPacket(String data) {
		this.data = data;
	}
	
	public String toString() {
		return data;
	}
}
