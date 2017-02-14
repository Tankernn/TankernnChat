package eu.tankernn.chat.server;

import eu.tankernn.chat.packets.MessagePacket;

public class Channel extends ClientCollection {
	public final String name;
	
	public Channel(String name) {
		this.name = name;
	}
	
	@Override
	public synchronized void broadcast(MessagePacket mess) {
		mess.setChannel(name);
		super.broadcast(mess);
	}
}
