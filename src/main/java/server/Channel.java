package server;

import common.MessagePacket;

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
