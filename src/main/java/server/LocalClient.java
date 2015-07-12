package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LocalClient extends Client {
	
	public LocalClient() { //Constructor for local client, the server, with full permissions
		in = new BufferedReader(new InputStreamReader(System.in));
		
		username = "SERVER";
		permissions = new String[] {"*"};
		
		readuser = new Thread(this, username);
		readuser.start();
	}
	
	@Override
	public void disconnect() {
		readuser.interrupt();
	}
	
	@Override
	public void send(String message) {
		System.out.println(message.toString());
		Server.log.log(message.toString());
	}
}
