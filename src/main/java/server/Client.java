package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import common.InfoPacket;
import common.MessagePacket;
import common.Packet;

public class Client implements Runnable {
	protected Thread readuser = new Thread(this);

	protected BufferedReader in;
	private ObjectOutputStream objOut;
	private Socket sock;

	public final String username;
	protected List<String> permissions = new ArrayList<>();

	private int messLastPeriod = 0;
	private Timer timer = new Timer();

	private Channel primaryChannel = Server.getChannels().get(0);

	public Client(Socket socket) {
		sock = socket;

		String line = null;
		try {
			objOut = new ObjectOutputStream(sock.getOutputStream());
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			line = in.readLine(); // First line contains username
		} catch (IOException e) {
			e.printStackTrace();
		}

		username = line;

		if (!validateUser()) {
			disconnect(false);
			throw new IllegalArgumentException();
		}

		permissions.add("user.*");

		send(new MessagePacket("Welcome to the server, " + username + "! Enjoy your stay!"));

		readuser.start();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				messLastPeriod = 0;
			}
		}, 800, 800);
	}

	public Client(String username, List<String> permissions, BufferedReader in, ObjectOutputStream out) {
		this.username = username;
		this.permissions = permissions;
		this.in = in;
		this.objOut = out;
	}

	private boolean validateUser() {
		// No spaces
		if (username.contains(" ")) {
			send("No spaces in usernames please!");
			return false;
		}

		// Not same username as anyone else
		if (Server.getClients().getClientByName(username).isPresent()) {
			send("Username already taken!");
			return false;
		}

		// No connect if banned
		for (BanNote note : Server.getBanned())
			try {
				if (note.ip.equals(getIP())) {
					if (note.expiry == null) {
						send(note.toString());
						return false;
					} else if (note.expiry.isBefore(LocalDateTime.now())) {
						Server.getBanned().remove(note);
						return true;
					} else {
						send(note.toString());
						return false;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		return true;
	}

	public void disconnect(boolean output) {
		if (!isConnected()) // Already disconnected
			return;

		timer.cancel();
		readuser.interrupt();

		try {
			if (sock != null)
				sock.close();
			if (in != null)
				in.close();
			if (objOut != null)
				objOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (output)
			Server.wideBroadcast(new MessagePacket(username + " has disconnected."));
	}

	public void disconnect() {
		disconnect(true);
	}

	public boolean isConnected() {
		return sock.isConnected() && !sock.isClosed();
	}

	public boolean hasPermission(String commandPermission) {
		long correctPermissions = permissions.stream().filter(perm -> commandPermission.startsWith(perm.replace(".*", "."))
				|| commandPermission.equalsIgnoreCase(perm) || perm.equalsIgnoreCase("*")).count();
		return correctPermissions > 0;
	}

	@Override
	public void run() {
		String mess;
		while (!readuser.isInterrupted() && (mess = getNewMessage()) != null) {
			if (mess.startsWith("/")) // Command handling
				Server.getCommReg().executeCommand(mess, this);
			else // Normal message handling
			{
				messLastPeriod++;
				if (messLastPeriod > 1 && !hasPermission("mod.spam")) {
					send("No spamming!");
					disconnect(false);
				} else
					getPrimaryChannel().broadcast(new MessagePacket(Client.this.username, mess));
			}
		}
		disconnect();
	}

	private String getNewMessage() {
		try {
			return in.readLine();
		} catch (IOException e) {
			disconnect();
			return null;
		}
	}

	/**
	 * Sends a packet to the user.
	 * 
	 * @param pack
	 *            Packet to send to the user
	 */
	public void send(Packet pack) {
		try {
			objOut.writeObject(pack);
			objOut.flush();
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

	public String getIP() throws IOException {
		return ((InetSocketAddress) sock.getRemoteSocketAddress()).getAddress().getHostAddress();
	}

	@Override
	public String toString() {
		return username;
	}

	public Channel getPrimaryChannel() {
		return primaryChannel;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void addPermission(String string) {
		permissions.add(string);
	}

	public void setPrimaryChannel(Channel primaryChannel) {
		this.primaryChannel = primaryChannel;
	}
}
