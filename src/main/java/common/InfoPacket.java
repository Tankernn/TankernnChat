package common;

import java.util.List;

import server.Client;
import server.Server;

public class InfoPacket implements Packet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String[] usersOnline;
	List<String> permissions;
	public String username, channel;
	
	private InfoPacket() {
		usersOnline = Server.getUsersOnline();
	}
	
	public static InfoPacket of(Client c) {
		InfoPacket info = new InfoPacket();
		
		info.channel = c.getPrimaryChannel().name;
		info.permissions = c.getPermissions();
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
