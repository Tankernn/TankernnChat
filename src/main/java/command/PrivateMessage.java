package command;

import server.Client;
import server.Server;
import server.util.StringArrays;

import common.Message;

public class PrivateMessage extends Command {

	@Override
	public void execute(String[] args, Client caller) {
		Client reciever = Server.getUserByName(args[0]);

		if (caller.equals(reciever)) {
			caller.send("Please don't speak with yourself.");
			return;
		} else if (reciever == null) {
			caller.send("No user called " + args[0] + ".");
			return;
		}

		Message mess = new Message("PM", caller.username,
				StringArrays.arrayToString(StringArrays.removeFirst(args)),
				Message.MessageType.PM);

		reciever.send(mess);
		caller.send(mess);

	}

	@Override
	public String getName() {
		return "pm";
	}

	@Override
	public String getPermission() {
		return "noob.pm";
	}

	@Override
	public String getDescription() {
		return "Sends a private message to a user";
	}

	@Override
	public int getMinArgNumber() {
		return 2;
	}

}
