package common;

public class FileSendPacket implements Packet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum TransferAction {
		INIT, START, DENY
	}
	
	public String filename;
	public int fileSize;
	public String originUser;
	public String destinationUser;
	public TransferAction action;
	
	public FileSendPacket(String filename, int fileSize, String originUser, String destinationUser, TransferAction action) {
		this.filename = filename;
		this.fileSize = fileSize;
		this.originUser = originUser;
		this.destinationUser = destinationUser;
		this.action = action;
	}

}
