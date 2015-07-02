package command;

import java.util.Optional;

import server.Channel;
import server.Client;
import server.Server;

import common.Command;
import common.Message;
import common.Message.MessageType;

public class List extends Command {
	
	@Override
	public void execute(String[] args, Client caller) {
		String arr, channelName = null;
		
		if (args.length >= 1) {
			Optional<Channel> maybeChannel = Server.getChannelByName(args[0]);
			Channel selectedChannel = maybeChannel.isPresent() ? maybeChannel.get() : null;
			try {
				arr = selectedChannel.listClients();
				channelName = selectedChannel.name;
			} catch (NullPointerException ex) {
				caller.send(new Message("No channel named " + channelName + ".", MessageType.ERROR, false));
				return;
			}
		} else
			arr = Server.listClients();
		
		if (channelName == null)
			caller.send(new Message("Users online are:" + "\n" + arr, MessageType.COMMAND, false));
		else
			caller.send(new Message("Users in channel " + channelName + " are:" + "\n" + arr, MessageType.COMMAND, false));
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
