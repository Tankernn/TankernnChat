package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
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
	
	public Channel primaryChannel = Server.channels.get(0);
	
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
			throw new IllegalArgumentException();
		}
		
		permissions = new String[] {"noob.*"};
		
		send(new Message("Welcome to the server! Enjoy your stay!"));
		
		readuser = new Thread(this, username);
		readuser.start();
		
		timer.start();
	}
	
	public Client() {}
	
	private boolean validateUser() {
		//No spaces
		if (username.contains(" ")){
			send("No spaces in usernames please!");
			return false;
		}
		
		//Not same username as anyone else
		if (Server.clients.getClientByName(username).isPresent()) {
			send("Username already taken!");
			return false;
		}
			
		//No connect if banned
		for (BanNote note: Server.banNotes)
			if (note.ip.equals(sock.getInetAddress().toString())) {
				if (note.expiry == null) {
					send(note.toString());
					return false;
				} else if (note.expiry.isBefore(LocalDateTime.now())) {
					Server.banNotes.remove(note);
					return true;
				} else {
					send(note.toString());
					return false;
				}
			}
		return true;
	}
	
	public void disconnect(boolean output) {
		if (!isConnected()) //Already disconnected
			return;
		
		if (timer.isRunning())
			timer.stop();
		
		if (readuser != null)
			readuser.interrupt();
		
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Server.cleanUp();
		
		if (output)
			Server.wideBroadcast(new Message(username + " has disconnected."));
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
				if (lastMess.startsWith("/")) //Command handling
				{
					String[] commandarray = lastMess.substring(1).split(" ");
					Server.commReg.executeCommand(commandarray, this);
				}
				else //Normal message handling
				{
					messLastPeriod++;
					if (messLastPeriod > 5) {
						send("No spamming!");
						disconnect(false);
					} else
						primaryChannel.broadcast(new Message(this.username, lastMess));
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
			objOut.flush();
		} catch (IOException e) {
			if (isConnected())
				disconnect();
		}
	}
	
	@Override
	public String toString() {
		return username;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		messLastPeriod = 0;
	}
}
