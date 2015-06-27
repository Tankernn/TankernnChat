package command;

import java.util.Optional;

import common.Command;
import common.Message;
import common.Message.MessageType;
import server.Channel;
import server.Client;
import server.Server;

public class JoinChannel extends Command {

	@Override
	public void execute(String[] args, Client caller) {
		if (caller.equals(Server.OPClient)) {
			caller.send("Client-only command.");
			return;
		}
		
		Optional<Channel> maybeChannel = Server.getChannelByName(args[0]);
		Channel selectedChannel = maybeChannel.isPresent()? maybeChannel.get() : null;
		
		try {
			selectedChannel.add(caller);
			caller.primaryChannel = selectedChannel;
			caller.send(new Message("You are now speaking in channel " + args[0] + ".", MessageType.COMMAND, false));
		} catch (NullPointerException ex) {
			caller.send(new Message("No such channel!", MessageType.ERROR, false));
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
