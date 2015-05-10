package command;

import common.Message;

import server.Client;
import server.Server;

public class Channel extends Command {

	@Override
	public void execute(String[] args, Client caller) {
		if (caller.equals(Server.OPClient)) {
			caller.send(new Message("Client-only command."));
			return;
		}
		
		try {
			Server.getChannelByName(args[0]).addUser(caller);
		} catch (NullPointerException ex) {
			caller.send(new Message("No such channel!"));
			return;
		}
		caller.primaryChannel = Server.getChannelByName(args[0]);
	}

	@Override
	public String setName() {
		return "channel";
	}

	@Override
	public String setPermission() {
		return "noob.channel";
	}

	@Override
	public String writeDescription() {
		return "Sets specified channel as primary (/channel <channel>)";
	}

	@Override
	public int setMinArgNumber() {
		return 1;
	}

}
