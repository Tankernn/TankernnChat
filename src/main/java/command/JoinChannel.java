package command;

import java.util.Optional;

import server.Channel;
import server.Client;
import server.Server;

import common.Command;
import common.MessagePacket;
import common.MessagePacket.MessageType;

public class JoinChannel extends Command {
	
	@Override
	public void execute(String[] args, Client caller) {
		if (caller.equals(Server.OPClient)) {
			caller.send("Client-only command.");
			return;
		}
		
		Optional<Channel> maybeChannel = Server.getChannelByName(args[0]);
		Channel selectedChannel = maybeChannel.isPresent() ? maybeChannel.get() : null;
		
		try {
			selectedChannel.add(caller);
			caller.primaryChannel = selectedChannel;
			caller.send(new MessagePacket("You are now speaking in channel " + args[0] + ".", MessageType.COMMAND));
		} catch (NullPointerException ex) {
			caller.send(new MessagePacket("No such channel!", MessageType.ERROR));
		}
	}
	
	@Override
	public String getName() {
		return "join";
	}
	
	@Override
	public String getPermission() {
		return "noob.channel";
	}
	
	@Override
	public String getDescription() {
		return "Sets specified channel as primary (/join <channel>)";
	}
	
	@Override
	public int getMinArgNumber() {
		return 1;
	}
	
}
