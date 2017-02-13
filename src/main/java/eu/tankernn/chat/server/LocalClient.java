package eu.tankernn.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.Packet;

public class LocalClient extends Client implements Runnable {
	
	Thread inputThread;
	
	/**
	 * Constructor for local client, the server, with full permissions
	 */
	public LocalClient() {
		super("SERVER", Arrays.asList(new String[] {"*"}), null, null);
		inputThread = new Thread(this);
		inputThread.start();
	}
	
	@Override
	public void run() {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(System.in));
		while (!Thread.interrupted()) {
			try {
				handleMessage(reader.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void disconnect() {}
	
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
