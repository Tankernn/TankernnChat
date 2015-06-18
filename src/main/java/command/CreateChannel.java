package command;

import server.Client;
import server.Server;

import common.Message;

public class CreateChannel extends Command {

	@Override
	public void execute(String[] args, Client caller) throws Exception {
		Server.channels.add(new server.Channel(args[0]));

		Server.wideBroadcast(new Message("Channel " + args[0]
				+ " is now available. Use '/join " + args[0] + "' to join."));
	}

	@Override
	public String getName() {
		return "createchannel";
	}

	@Override
	public String getPermission() {
		return "server.createchannel";
	}

	@Override
	public String getDescription() {
		return "Creates a channel with specified settings. (/createchannel <name>)";
	}

	@Override
	public int getMinArgNumber() {
		return 1;
	}

}
