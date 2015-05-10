package command;

import common.Message;

import server.Client;
import server.Server;

public class PrivateMessage extends Command {

	@Override
	public void execute(String[] args, Client caller) {
		if (caller.equals(Server.getUserByName(args[0])))
			return;
		
		String content = "";
		for (int i = 1; i < args.length -1; i++) {
			content += args[i] + " ";
		}
		Message mess = new Message("PM", caller.username, content);
		try {
			Client reciever = Server.getUserByName(args[0]);
			reciever.send(mess); caller.send(mess);
		} catch (Exception ex) {
			caller.send("No such user!");
		}
	}

	@Override
	public String setName() {
		return "pm";
	}

	@Override
	public String setPermission() {
		return "noob.pm";
	}

	@Override
	public String writeDescription() {
		return "Sends a private message to a user";
	}

	@Override
	public int setMinArgNumber() {
		return 2;
	}

}
