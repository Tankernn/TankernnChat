package server;

import java.util.ArrayList;
import java.util.Optional;

import common.MessagePacket;

public class ClientCollection extends ArrayList<Client> {
	/**
	 * A collection of clients.
	 */
	private static final long serialVersionUID = 1L;
	
	int maxClients = Server.maxUsers;
	
	public ClientCollection() {
		this(Server.maxUsers);
	}
	
	public ClientCollection(int maxUsers) {
		super(maxUsers);
		maxClients = maxUsers;
	}
	
	/**
	 * Gets the user with specified username.
	 * 
	 * @param name The username of wanted user.
	 * @return Optional containing found user.
	 */
	public Optional<Client> getClientByName(String name) {
		return stream()
				.filter(c -> c.username.equals(name))
				.findFirst();
	}
	
	/**
	 * Sends a message to all users in the collection.
	 * 
	 * @param mess The message object to send.
	 */
	void broadcast(MessagePacket mess) {
		if (mess.validate()) {
			for (Client c: this)
				c.send(mess);
			Server.OPClient.send(mess.toString());
		}
	}
	
	/**
	 * Adds a user to the collection, checking that the user has not already
	 * been added and that the the collection isn't "full".
	 * 
	 * @param user User to be added to collection.
	 */
	@Override
	public boolean add(Client user) throws ArrayIndexOutOfBoundsException {
		if (contains(user))
			return true;
		
		if (size() >= maxClients)
			throw new ArrayIndexOutOfBoundsException();
		else
			super.add(user);
		return true;
	}
	
	/**
	 * Remove the user without disconnecting them.
	 * 
	 * @param user User to remove.
	 */
	public void remove(Client user) {
		remove(user, false);
	}
	
	/**
	 * Removes a user from the collection.
	 * 
	 * @param user User to remove.
	 * @param disconnect Should the user also be disconnected?
	 */
	public void remove(Client user, boolean disconnect) {
		if (disconnect)
			user.disconnect();
		super.remove(user);
	}
	
	/**
	 * Create a string containing usernames of current connected users,
	 * separated by specified separator.
	 * 
	 * @param sep The char to put between the names.
	 * @return String containing all usernames separated by separator.
	 */
	public String listClients(char sep) {
		String[] names = getUsernameArray();
		String namesString = "";
		for (String name: names)
			namesString += name + sep;
		//Remove last separator
		namesString = namesString.substring(0, namesString.length() - sep);
		return namesString;
	}
	
	/**
	 * String array representation of current users by name.
	 * 
	 * @return A String array containing the names of the users in the
	 *         collection.
	 * @see String
	 */
	public String[] getUsernameArray() {
		String[] names = new String[size()];
		for (int i = 0; i < size(); i++)
			names[i] = this.get(i).username;
		return names;
	}
	
	/**
	 * Removes disconnected clients from the collection.
	 */
	public void cleanUp() {
		for (int i = 0; i < size(); i++)
			//Has to be done with number iteration, otherwise unsafe
			if (!get(i).isConnected())
				remove(i);
	}
}