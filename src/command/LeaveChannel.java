package command;

import server.Client;
import server.Server;

public class LeaveChannel extends Command{

	@Override
	public void execute(String[] args, Client caller) throws Exception {
		if (caller.equals(Server.OPClient)){
			caller.send("Client-only command.");
			return;
		}
		
		try {
			Server.getChannelByName(args[0]).remove(caller);
			if (caller.primaryChannel.equals(Server.getChannelByName(args[0])))
				caller.primaryChannel = Server.channels.get(0);
			caller.send("You left channel " + args[0] + ".");
			caller.send("You are now speaking in channel " + caller.primaryChannel.name + ".");
		} catch(NullPointerException ex) {
			caller.send("No channel named " + args[0] + ".");
			return;
		}
	}

	@Override
	public String setName() {
		return "leave";
	}

	@Override
	public String setPermission() {
		return "noob.leave";
	}

	@Override
	public String writeDescription() {
		return "Removes caller from specified channel. (/leave <channel>)";
	}

	@Override
	public int setMinArgNumber() {
		return 1;
	}

}
