package eu.tankernn.chat.server;

public class FileTransfer {
	Client sender, reciever;

	public FileTransfer(Client sender, Client reciever) {
		this.sender = sender;
		this.reciever = reciever;
	}
	
}
