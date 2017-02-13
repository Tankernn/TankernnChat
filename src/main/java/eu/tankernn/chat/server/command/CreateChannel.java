package eu.tankernn.chat.server.command;

import java.util.Deque;

import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.server.Channel;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;

@CommandInfo(desc = "Creates a channel with specified settings. (/createchannel <name>)", name = "create", permission = "admin.create.channel", minArg = 1)
public class CreateChannel implements Command {
	
	@Override
	public void execute(Deque<String> args, Client caller) throws Exception {
		Server.getChannels().add(new Channel(args.peek()));
		
		Server.wideBroadcast(new MessagePacket("Channel " + args.peek() + " is now available. Use '/join " + args.peek() + "' to join."));
	}
	
}
