package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import common.MessagePacket;
import common.Packet;

public class LocalClient extends Client {
	
	/**
	 * Constructor for local client, the server, with full permissions
	 */
	public LocalClient() {
		super("SERVER");
		in = new BufferedReader(new InputStreamReader(System.in));
		
		isOP = true;
		permissions = new String[] {"*"};
		
		readuser = new ReadUser();
	}
	
	@Override
	public void disconnect() {
		readuser.interrupt();
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void disconnect(boolean bool) {
		disconnect();
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
