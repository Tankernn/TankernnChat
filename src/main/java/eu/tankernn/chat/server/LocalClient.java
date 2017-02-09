package eu.tankernn.chat.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.Packet;

public class LocalClient extends Client {
	
	/**
	 * Constructor for local client, the server, with full permissions
	 */
	public LocalClient() {
		super("SERVER", Arrays.asList(new String[] {"*"}), new BufferedReader(new InputStreamReader(System.in)), null);
	}
	
	@Override
	public void disconnect(boolean bool) {
		disconnect(false);
	}
	
	@Override
	public void send(Packet pack) {
		if (pack instanceof MessagePacket)
			send(((MessagePacket) pack).toString(false));
	}
	
	@Override
	public void send(String message) {
		Server.getLogger().info(message);
	}
}
