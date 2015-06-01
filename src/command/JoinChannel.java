package command;

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
			caller.send("You are now speaking in channel " + args[0] + ".");
		} catch (NullPointerException ex) {
			caller.send("No such channel!");
		}
	}

	@Override
	public String getName() {
		return "join";
	}

	@Override
	public String getPermission() {
		return "noob.channel";
	}

	@Override
	public String writeDescription() {
		return "Sets specified channel as primary (/join <channel>)";
	}

	@Override
	public int getMinArgNumber() {
		return 1;
	}

}
