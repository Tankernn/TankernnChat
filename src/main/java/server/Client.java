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

import common.InfoPacket;
import common.MessagePacket;
import common.Packet;

public class Client implements ActionListener {
	ReadUser readuser;
	
	BufferedReader in;
	ObjectOutputStream objOut;
	
	public String username;
	public Socket sock;
	
	public String[] permissions;
	public boolean isOP = false;
	
	int messLastPeriod = 0;
	Timer timer = new Timer(800, this);
	
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
		
		send(new MessagePacket("Welcome to the server! Enjoy your stay!"));
		
		readuser = new ReadUser();
		
		timer.start();
	}
	
	public Client() {}
	
	private boolean validateUser() {
		//No spaces
		if (username.contains(" ")) {
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
			Server.wideBroadcast(new MessagePacket(username + " has disconnected."));
	}
	
	public void disconnect() {
		disconnect(true);
	}
	
	public boolean isConnected() {
		return !sock.isClosed();
	}
	
	public boolean hasPermission(String commandPermission) {
		if (this.isOP)
			return true;
		
		for (int i = 0; i < permissions.length; i++) {
			if (commandPermission.startsWith(permissions[i].replace(".*", ".")) || commandPermission.equalsIgnoreCase(permissions[i]) || permissions[i].equalsIgnoreCase("*"))
				return true;
		}
		return false;
	}
	
	class ReadUser extends Thread {
		ReadUser() {
			super(Client.this.username);
			this.start();
		}
		
		@Override
		public void run() {
			String mess;
			while (!readuser.isInterrupted() && (mess = getNewMessage()) != null) {
				
				if (mess.startsWith("/")) //Command handling
					Server.commReg.executeCommand(mess, Client.this);
				else //Normal message handling
				{
					messLastPeriod++;
					if (messLastPeriod > 1 && !(Client.this instanceof LocalClient)) {
						send("No spamming!");
						disconnect(false);
					} else
						primaryChannel.broadcast(new MessagePacket(Client.this.username, mess));
				}
			}
			disconnect();
		}
		
		public String getNewMessage() {
			try {
				return in.readLine();
			} catch (IOException e) {
				disconnect();
			}
			return null;
		}
	}
	

	
	/**
	 * Sends a packet to the user.
	 * 
	 * @param pack Packet to send to the user
	 */
	public void send(Packet pack) {
		try {
			objOut.writeObject(pack);
			objOut.writeObject(InfoPacket.of(this));
			objOut.flush();
		} catch (IOException e) {
			if (isConnected())
				disconnect();
		}
	}
	
	public void send(String message) {
		send(new MessagePacket(message));
	}
	
	public String getIP() {
		return sock.getInetAddress().toString();
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
