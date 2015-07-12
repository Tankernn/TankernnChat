package server.command;

import java.util.Optional;

import server.Channel;
import server.Client;
import server.Server;

import common.Command;
import common.MessagePacket;
import common.MessagePacket.MessageType;

public class List extends Command {
	
	@Override
	public void execute(String[] args, Client caller) {
		String arr, channelName = null;
		
		if (args.length >= 1) {
			Optional<Channel> maybeChannel = Server.getChannelByName(args[0]);
			Channel selectedChannel = maybeChannel.isPresent() ? maybeChannel.get() : null;
			try {
				arr = selectedChannel.listClients('\n');
				channelName = selectedChannel.name;
			} catch (NullPointerException ex) {
				caller.send(new MessagePacket("No channel named " + channelName + ".", MessageType.ERROR));
				return;
			}
		} else
			arr = Server.listClients('\n');
		
		if (channelName == null)
			caller.send(new MessagePacket("Users online are:" + "\n" + arr, MessageType.COMMAND));
		else
			caller.send(new MessagePacket("Users in channel " + channelName + " are:" + "\n" + arr, MessageType.COMMAND));
	}
	
	@Override
	public String getName() {
		return "list";
	}
	
	@Override
	public int getMinArgNumber() {
		return 0;
	}
	
	@Override
	public String getDescription() {
		return "Lists all users online. (/list [channel])";
	}
	
	@Override
	public String getPermission() {
		return "noob.list";
	}
	
}
