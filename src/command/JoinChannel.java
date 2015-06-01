package command;

import common.Message;
import common.Message.MessageType;
import server.Client;
import server.Server;

public class JoinChannel extends Command {

	@Override
	public void execute(String[] args, Client caller) {
		if (caller.equals(Server.OPClient)) {
			caller.send("Client-only command.");
			return;
		}
		
		try {
			Server.getChannelByName(args[0]).add(caller);
			caller.primaryChannel = Server.getChannelByName(args[0]);
			caller.send(new Message("You are now speaking in channel " + args[0] + ".", MessageType.COMMAND, false));
		} catch (NullPointerException ex) {
			caller.send(new Message("No such channel!", MessageType.ERROR, false));
		}
	}

	@Override
	public String setName() {
		return "join";
	}

	@Override
	public String setPermission() {
		return "noob.channel";
	}

	@Override
	public String writeDescription() {
		return "Sets specified channel as primary (/join <channel>)";
	}

	@Override
	public int setMinArgNumber() {
		return 1;
	}

}
