package eu.tankernn.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import eu.tankernn.chat.packets.InfoPacket;
import eu.tankernn.chat.packets.MessagePacket;
import eu.tankernn.chat.packets.Packet;
import io.netty.channel.ChannelFuture;

public class Client {
	io.netty.channel.Channel c;
	private Optional<Client> filePartner = Optional.empty();
	
	public final String username;
	protected List<String> permissions = new ArrayList<>();
	
	private int messLastPeriod = 0;
	
	private Channel primaryChannel = Server.getChannels().get(0);
	
	public Client(io.netty.channel.Channel c, String username) {
		this.c = c;
		this.username = username;
		
		permissions.add("user.*");
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
	
	public void disconnect() {
		c.close();
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
		if (message.startsWith("/")) { // Command handling
			Server.getCommReg().executeCommand(message, this);
		} else // Normal message handling
		{
			messLastPeriod++;
			if (messLastPeriod > 1 && !hasPermission("mod.spam")) {
				send("No spamming!");
				disconnect();
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
		ChannelFuture cf;
		cf = c.writeAndFlush(pack);
		if (!cf.isSuccess())
			if (!(cf.cause() instanceof ClosedChannelException))
			Server.getLogger().log(Level.SEVERE, "Error sending packet.",
					cf.cause());
		
	}
	
	public void send(String message) {
		send(new MessagePacket(message));
	}
	
	public String getIP() throws IOException {
		return ((InetSocketAddress) c.remoteAddress()).getHostString();
	}
	
	public void spamReset() {
		messLastPeriod = 0;
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
	
	public void setFilePartner(Client c) {
		filePartner = c == null ? Optional.empty() : Optional.of(c);
	}
	
	public Optional<Client> getFilePartner() {
		return filePartner;
	}
}
