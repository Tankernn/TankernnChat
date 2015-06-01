package command;

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
				caller.send("No channel named " + channelName + ".");
				return;
			}
		} else
			arr =  Server.listClients();
		
		if (channelName == null)
			caller.send("Users online are:" + "\n" + arr);
		else
			caller.send("Users in channel " + channelName + " are:" + "\n" + arr);
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
	public String writeDescription() {
		return "Lists all users online. (/list [channel])";
	}

	@Override
	public String getPermission() {
		return "noob.list";
	}

}
