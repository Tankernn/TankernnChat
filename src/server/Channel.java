package server;

import common.Message;

public class Channel {
	Client[] users = new Client[Server.maxUsers];
	
	public String name;
	
	public Channel(String name) {
		this.name = name;
	}
	
	void broadcast(Client sender, String content) {
		Message mess = new Message(name, sender.username, content);
		 if (mess.validate()) {
			for (int i = 0; i < users.length; i++){
				if (!positionFree(i))
					users[i].send(mess);
			}
			Server.OPClient.send(mess.toString());
		}
	}
	 
	boolean hasUser(Client user) {
		for (int i = 0; i < users.length; i++)
			if (!positionFree(i))
				if (users[i].equals(user))
					return true;
		return false;
	}
	
	public boolean addUser(Client user) {
		if (hasUser(user))
			return true;
		
		for (int i = 0; i < users.length; i++)
			if (positionFree(i)) {
				users[i] = user;
				return true;
			}
		return false;
	}
	
	public boolean removeUser(Client user) {
		for (int i = 0; i < users.length; i++)
			if (users[i].equals(user)) {
				users[i] = null;
				return true;
			}
		return false;
	}
	
	public String getUsersInChannel() {
		String usersInChannel = "";
		for (int i = 0; i < users.length; i++)
			if (!positionFree(i)) {
				usersInChannel += users[i].username + "\n" ;
			}
		return usersInChannel;
	}
	
	boolean positionFree(int pos) {
		return users[pos] == null || !users[pos].isConnected();
	}
}
