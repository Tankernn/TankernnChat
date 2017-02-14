package eu.tankernn.chat.server.command;

import java.util.Deque;
import java.util.Optional;

import eu.tankernn.chat.packets.MessagePacket;
import eu.tankernn.chat.packets.MessagePacket.MessageType;
import eu.tankernn.chat.server.Channel;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;

@CommandInfo(desc = "Sets specified channel as primary (/join <channel>)", name = "join", permission = "user.join", minArg = 1)
public class JoinChannel implements Command {

	@Override
	public void execute(Deque<String> args, Client caller) {
		if (caller.equals(Server.getLocalClient())) {
			caller.send("Client-only command.");
			return;
		}
		String name = args.pop();
		Optional<Channel> maybeChannel = Server.getChannelByName(name);
		Channel selectedChannel = maybeChannel.isPresent() ? maybeChannel.get() : null;

		try {
			selectedChannel.add(caller);
			caller.setPrimaryChannel(selectedChannel);
			caller.send(new MessagePacket("You are now speaking in channel " + name + ".", MessageType.COMMAND));
		} catch (NullPointerException ex) {
			caller.send(new MessagePacket("No such channel!", MessageType.ERROR));
		}
	}

}
