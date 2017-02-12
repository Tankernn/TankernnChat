package eu.tankernn.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import eu.tankernn.chat.common.InfoPacket;
import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.Packet;
import io.netty.channel.ChannelFuture;

public class Client {
	io.netty.channel.Channel c;
	
	public final String username;
	protected List<String> permissions = new ArrayList<>();
	
	private int messLastPeriod = 0;
	private Timer timer = new Timer();
	
	private Channel primaryChannel = Server.getChannels().get(0);
	
	public Client(io.netty.channel.Channel c, String username) {
		this.c = c;
		this.username = username;
		
		if (!validateUser()) {
			return;
		}
		
		permissions.add("user.*");
		
		send(new MessagePacket(
				"Welcome to the server, " + username + "! Enjoy your stay!"));
		
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
	}
	
	boolean validateUser() {
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
		for (BanNote note: Server.getBanned())
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
		c.close();
		Server.cleanUp();
		
		if (output)
			Server.wideBroadcast(
					new MessagePacket(username + " has disconnected."));
	}
	
	public void disconnect() {
		disconnect(true);
	}
	
	public boolean isConnected() {
		return c.isActive() && c.isWritable();
	}
	
	public boolean hasPermission(String commandPermission) {
		long correctPermissions = permissions.stream()
				.filter(perm -> commandPermission.startsWith(
						perm.replace(".*", ".")) || commandPermission
								.equalsIgnoreCase(
										perm) || perm.equalsIgnoreCase("*"))
				.count();
		return correctPermissions > 0;
	}
	
	public void handleMessage(String message) {
		if (message.startsWith("/")) // Command handling
			Server.getCommReg().executeCommand(message, this);
		else // Normal message handling
		{
			messLastPeriod++;
			if (messLastPeriod > 1 && !hasPermission("mod.spam")) {
				send("No spamming!");
				disconnect(false);
			} else
				getPrimaryChannel().broadcast(
						new MessagePacket(Client.this.username, message));
		}
	}
	
	/**
	 * Sends a packet to the user.
	 * 
	 * @param pack Packet to send to the user
	 */
	public void send(Packet pack) {
		ChannelFuture cf = c.writeAndFlush(pack);
		if (!cf.isSuccess())
			Server.getLogger().log(Level.SEVERE, "Error sending packet.",
					cf.cause());
		c.writeAndFlush(InfoPacket.of(this));
	}
	
	public void send(String message) {
		send(new MessagePacket(message));
	}
	
	public String getIP() throws IOException {
		return ((InetSocketAddress) c.remoteAddress()).getHostString();
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
