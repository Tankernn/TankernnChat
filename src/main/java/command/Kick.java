package command;

import server.Client;
import server.Server;

import common.Message;
import common.Message.MessageType;

public class Kick extends Command {

	@Override
	public void execute(String[] args, Client caller) {
		try {
			Server.getUserByName(args[0]).disconnect(false);
		} catch (NullPointerException ex) {
			caller.send(new Message("No user called " + args[0] + "!",
					MessageType.ERROR, false));
		}
	}

	@Override
	public String getName() {
		return "kick";
	}

	@Override
	public int getMinArgNumber() {
		return 1;
	}

	@Override
	public String getDescription() {
		return "Kicks a user. (/kick <username>)";
	}

	@Override
	public String getPermission() {
		return "server.kick";
	}
}
