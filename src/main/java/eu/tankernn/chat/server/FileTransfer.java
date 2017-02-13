package eu.tankernn.chat.server;

import server.Client;

public class FileTransfer {
	Client sender, reciever;

	public FileTransfer(Client sender, Client reciever) {
		this.sender = sender;
		this.reciever = reciever;
	}
	
}
