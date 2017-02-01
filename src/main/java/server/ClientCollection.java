package server;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import common.MessagePacket;

/**
 * A collection of clients.
 */
public class ClientCollection {
	
	private List<Client> clients = new CopyOnWriteArrayList<>();

	/**
	 * Gets the user with specified username.
	 * 
	 * @param name
	 *            The username of wanted user.
	 * @return Optional containing found user.
	 */
	public Optional<Client> getClientByName(String name) {
		return clients.stream().filter(c -> c.username.equals(name)).findFirst();
	}

	/**
	 * Sends a message to all users in the collection.
	 * 
	 * @param mess
	 *            The message object to send.
	 */
	public synchronized void broadcast(MessagePacket mess) {
		if (mess.validate()) {
			for (Client c : clients)
				c.send(mess);
			Server.getOPClient().send(mess);
		}
	}

	/**
	 * Adds a user to the collection, checking that the user has not already
	 * been added and that the the collection isn't "full".
	 * 
	 * @param user
	 *            User to be added to collection.
	 * @return 
	 */
	public void add(Client user) {
		if (clients.contains(user))
			return;

		clients.add(user);
	}

	/**
	 * Remove the user without disconnecting them.
	 * 
	 * @param user
	 *            User to remove.
	 */
	public synchronized void remove(Client user) {
		remove(user, false);
	}

	/**
	 * Removes a user from the collection.
	 * 
	 * @param user
	 *            User to remove.
	 * @param disconnect
	 *            Should the user also be disconnected?
	 */
	public synchronized void remove(Client user, boolean disconnect) {
		if (disconnect)
			user.disconnect();
		clients.remove(user);
	}

	/**
	 * Create a string containing usernames of current connected users,
	 * separated by specified separator.
	 * 
	 * @param sep
	 *            The char to put between the names.
	 * @return String containing all usernames separated by separator.
	 */
	public String listClients(CharSequence sep) {
		return String.join(sep, getUsernameArray());
	}

	/**
	 * String array representation of current users by name.
	 * 
	 * @return A String array containing the names of the users in the
	 *         collection.
	 * @see String
	 */
	public String[] getUsernameArray() {
		return clients.stream().map(c -> c.username).toArray(i -> new String[i]);
	}
	
	public void disconnectAll() {
		clients.forEach(c -> c.disconnect());
	}

	/**
	 * Removes disconnected clients from the collection.
	 */
	public void cleanUp() {
		clients.removeIf(c -> !c.isConnected());
	}
}