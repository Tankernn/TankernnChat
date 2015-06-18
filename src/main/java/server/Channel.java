package server;

import common.Message;

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
	void broadcast(Message mess) {
		mess.channel = name;
		super.broadcast(mess);
	}
}
