package eu.tankernn.chat.server;

import eu.tankernn.chat.common.MessagePacket;

public class Channel extends ClientCollection {
	public String name;
	
	public Channel(String name) {
		this.name = name;
	}
	
	@Override
	public synchronized void broadcast(MessagePacket mess) {
		mess.channel = name;
		super.broadcast(mess);
	}
}
