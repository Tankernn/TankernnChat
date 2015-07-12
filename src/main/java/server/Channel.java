package server;

import common.MessagePacket;

public class Channel extends ClientCollection {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String name;
	
	public Channel(String name) {
		super();
		this.name = name;
	}
	
	@Override
	void broadcast(MessagePacket mess) {
		mess.channel = name;
		super.broadcast(mess);
	}
}
