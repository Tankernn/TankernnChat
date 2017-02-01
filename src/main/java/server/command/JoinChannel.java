package server.command;

import java.util.Optional;

import server.Channel;
import server.Client;
import server.Server;
import common.MessagePacket;
import common.MessagePacket.MessageType;

@CommandInfo(desc = "Sets specified channel as primary (/join <channel>)", name = "join", permission = "user.join", minArg = 1)
public class JoinChannel implements Command {

	@Override
	public void execute(String[] args, Client caller) {
		if (caller.equals(Server.getOPClient())) {
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

}
