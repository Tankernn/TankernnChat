package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.Timer;

import common.Message;

public class Client implements Runnable, ActionListener {
	Thread readuser;
	
	BufferedReader in;
	ObjectOutputStream objOut;
	
	public String username;
	public Socket sock;
	
	String[] permissions;
	
	int messLastPeriod = 0;
	Timer timer = new Timer(3000, this);
	
	public Channel primaryChannel = Server.channels[0];
	
	public Client(Socket s) {
		sock = s;
		
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			objOut = new ObjectOutputStream(sock.getOutputStream());
			username = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (!validateUser()) {
			disconnect(false);
			return;
		}
		
		permissions = new String[] {"noob.*"};
		
		send(new Message("Welcome to the server! Enjoy your stay!"));
		
		readuser = new Thread(this, username);
		readuser.start();
		
		timer.start();
	}
	
	public Client() {}
	
	private boolean validateUser() {
		if (username.contains(" ")){
			send("No spaces in usernames please!");
			return false;
		}
		
		for (int i = 0; i < Server.clients.length; i++) {
			if (!Server.positionFree(i))
				if (Server.clients[i].username.equalsIgnoreCase(username)) {
					send("Username already taken!");
					return false;
				}
		}
		
		if (Server.bannedIps.contains(sock.getInetAddress().toString())) {
			send("You are banned from this server!");
			return false;
		}
		
		return true;
	}
	
	public void disconnect(boolean output) {
		if (timer.isRunning())
			timer.stop();
		
		if (readuser != null)
			readuser.interrupt();
		
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (output)
			Server.broadcast(new Message(username + " has disconnected."));
	}
	
	public void disconnect() {
		disconnect(true);
	}
	
	public boolean isConnected() {
		return !sock.isClosed();
	}
	
	boolean hasPermission(String commandPermission) {
		for (int i = 0; i < permissions.length; i++) {
			if (commandPermission.startsWith(permissions[i].replace(".*", ".")) || commandPermission.equalsIgnoreCase(permissions[i]) || permissions[i].equalsIgnoreCase("*"))
				return true;
		}
		return false;
	}

	@Override
	public void run() {
		String lastMess;
		try {
			while (!readuser.isInterrupted() && ((lastMess = in.readLine()) != null)) {
				if (lastMess.startsWith("/")) {
					String[] commandarray = lastMess.substring(1).split(" ");
					CommandHandler.executeCommand(commandarray, this);
				} else {
					messLastPeriod++;
					if (messLastPeriod > 5) {
						send("No spamming!");
						disconnect(false);
					} else
						primaryChannel.broadcast(this, lastMess);
				}
			}
			disconnect();
		} catch (IOException e) {
			disconnect();
		}
	}
	
	public void send(Object message) {
		try {
			objOut.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		messLastPeriod = 0;
	}
}