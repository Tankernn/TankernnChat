package command;

import java.util.Optional;

import server.Channel;
import server.Client;
import server.Server;

import common.Command;
import common.MessagePacket;
import common.MessagePacket.MessageType;

public class LeaveChannel extends Command {
	
	@Override
	public void execute(String[] args, Client caller) throws Exception {
		if (caller.equals(Server.OPClient)) {
			caller.send("Client-only command.");
			return;
		}
		
		Optional<Channel> maybeChannel = Server.getChannelByName(args[0]);
		Channel selectedChannel = maybeChannel.isPresent() ? maybeChannel.get() : null;
		
		try {
			selectedChannel.remove(caller);
			if (caller.primaryChannel.equals(selectedChannel))
				caller.primaryChannel = Server.channels.get(0);
			caller.send(new MessagePacket("You left channel " + args[0] + ".", MessageType.COMMAND));
			caller.send(new MessagePacket("You are now speaking in channel " + caller.primaryChannel.name + ".", MessageType.COMMAND));
		} catch (NullPointerException ex) {
			caller.send(new MessagePacket("No channel named " + args[0] + ".", MessageType.ERROR));
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
