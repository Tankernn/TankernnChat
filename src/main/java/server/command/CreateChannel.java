package server.command;

import common.MessagePacket;
import server.Channel;
import server.Client;
import server.Server;

@CommandInfo(desc = "Creates a channel with specified settings. (/createchannel <name>)", name = "create", permission = "admin.create.channel", minArg = 1)
public class CreateChannel implements Command {
	
	@Override
	public void execute(String[] args, Client caller) throws Exception {
		Server.getChannels().add(new Channel(args[0]));
		
		Server.wideBroadcast(new MessagePacket("Channel " + args[0] + " is now available. Use '/join " + args[0] + "' to join."));
	}
	
}
