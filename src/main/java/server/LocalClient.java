package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import common.MessagePacket;
import common.Packet;

public class LocalClient extends Client {
	
	public LocalClient() { //Constructor for local client, the server, with full permissions
		in = new BufferedReader(new InputStreamReader(System.in));
		
		username = "SERVER";
		permissions = new String[] {"*"};
		
		readuser = new ReadUser();
	}
	
	@Override
	public void disconnect() {
		readuser.interrupt();
	}
	
	@Override
	public void disconnect(boolean bool) {
		disconnect();
	}
	
	@Override
	public void send(Packet pack) {
		if (pack instanceof MessagePacket)
			send(pack.toString());
	}
	
	@Override
	public void send(String message) {
		System.out.println(message.toString());
		Server.log.log(message.toString());
	}
}
