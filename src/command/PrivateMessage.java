package command;

import common.Message;
import server.Client;
import server.CommandHandler;
import server.Server;

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
		
		Message mess = new Message("PM", caller.username, this.stringArrayToString(CommandHandler.removeFirst(args)));
		
		reciever.send(mess); caller.send(mess);
		
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
