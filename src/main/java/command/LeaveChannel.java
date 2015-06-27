package command;

import common.Message;
import common.Message.MessageType;
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
			caller.send(new Message("You left channel " + args[0] + ".", MessageType.COMMAND, false));
			caller.send(new Message("You are now speaking in channel " + caller.primaryChannel.name + ".", MessageType.COMMAND, false));
		} catch(NullPointerException ex) {
			caller.send(new Message("No channel named " + args[0] + ".", MessageType.ERROR, false));
			return;
		}
	}

	@Override
	public String getName() {
		return "leave";
	}

	@Override
	public String getPermission() {
		return "noob.leave";
	}

	@Override
	public String getDescription() {
		return "Removes caller from specified channel. (/leave <channel>)";
	}

	@Override
	public int getMinArgNumber() {
		return 1;
	}

}
