package command;

import common.Message;
import common.Message.MessageType;
import server.Client;
import server.Server;

public class List extends Command {
	
	@Override
	public void execute(String[] args, Client caller) {
		String arr, channelName = null;
		
		if (args.length >= 1) {
			channelName = args[0];
			try {
				arr = Server.getChannelByName(channelName).listClients();
			} catch (NullPointerException ex) {
				caller.send(new Message("No channel named " + channelName + ".", MessageType.ERROR, false));
				return;
			}
		} else
			arr =  Server.listClients();
		
		if (channelName == null)
			caller.send(new Message("Users online are:" + "\n" + arr, MessageType.COMMAND, false));
		else
			caller.send(new Message("Users in channel " + channelName + " are:" + "\n" + arr, MessageType.COMMAND, false));
	}

	@Override
	public String setName() {
		return "list";
	}

	@Override
	public int setMinArgNumber() {
		return 0;
	}

	@Override
	public String writeDescription() {
		return "Lists all users online. (/list [channel])";
	}

	@Override
	public String setPermission() {
		return "noob.list";
	}

}
