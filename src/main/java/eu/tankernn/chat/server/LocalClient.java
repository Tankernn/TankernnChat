package eu.tankernn.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import eu.tankernn.chat.packets.MessagePacket;
import eu.tankernn.chat.packets.Packet;

public class LocalClient extends Client implements Runnable {

	Thread inputThread = new Thread(this);
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * Constructor for local client, the server, with full permissions
	 */
	public LocalClient() {
		super("SERVER", Arrays.asList(new String[] { "*" }), null, null);
		inputThread.start();
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				handleMessage(reader.readLine());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	@Override
	public void disconnect() {
		inputThread.interrupt();
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
