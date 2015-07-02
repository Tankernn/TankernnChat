package server;

import java.util.ArrayList;
import java.util.Optional;

import common.Message;

public class ClientCollection extends ArrayList<Client> {
	/**
	 * 
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
	
	public Optional<Client> getClientByName(String name) {
		return stream()
				.filter(c -> c.username.equals(name))
				.findFirst();
	}
	
	void broadcast(Message mess) { //Broadcast to all
		if (mess.validate()) {
			for (Client c: this)
				c.send(mess);
			Server.OPClient.send(mess.toString());
		}
	}
	
	@Override
	public boolean add(Client user) throws ArrayIndexOutOfBoundsException { //Add user
		if (contains(user))
			return true;
		
		if (size() >= maxClients)
			throw new ArrayIndexOutOfBoundsException();
		else
			super.add(user);
		return true;
	}
	
	public void remove(Client user) { //Remove without DC
		remove(user, false);
	}
	
	public void remove(Client user, boolean disconnect) { //Remove and disconnect if needed
		if (disconnect)
			user.disconnect();
		super.remove(user);
	}
	
	public String listClients() { //String from array
		return toString().replace(", ", "\n").replace("[", "").replace("]", "");
	}
	
	public String[] listClientsArray() { //Array instead of string
		return listClients().split("\n");
	}
	
	public void cleanUp() { //Remove unused clients, has to be done with number iteration, otherwise unsafe
		for (int i = 0; i < size(); i++)
			if (!get(i).isConnected())
				remove(i);
	}
}