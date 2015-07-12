package common;

import server.Client;
import server.Server;

public class InfoPacket implements Packet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String[] usersOnline, permissions;
	public String username, channel;
	
	private InfoPacket() {
		usersOnline = Server.getUsersOnline();
	}
	
	public static InfoPacket of(Client c) {
		InfoPacket info = new InfoPacket();
		
		info.channel = c.primaryChannel.name;
		info.permissions = c.permissions;
		info.username = c.username;
		
		return info;
	}
	
	@Override
	public String toString() {
		return "Username: " + username + '\n'
				+ "Channel: " + channel + '\n'
				+ "Users online: " + usersOnline.length;
	}
}
