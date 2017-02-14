package eu.tankernn.chat.server.command;

import java.util.Deque;
import java.util.Optional;

import eu.tankernn.chat.packets.MessagePacket;
import eu.tankernn.chat.packets.MessagePacket.MessageType;
import eu.tankernn.chat.server.Channel;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;

@CommandInfo(desc = "Lists all users online. (/list [channel])", name = "list", permission = "user.list")
public class List implements Command {

	@Override
	public void execute(Deque<String> args, Client caller) {
		String arr, channelName = null;

		if (!args.isEmpty()) {
			String name = args.pop();
			Optional<Channel> maybeChannel = Server.getChannelByName(name);
			Channel selectedChannel = maybeChannel.isPresent() ? maybeChannel.get() : null;
			try {
				arr = selectedChannel.listClients("\n");
				channelName = selectedChannel.name;
			} catch (NullPointerException ex) {
				caller.send(new MessagePacket("No channel named " + channelName + ".", MessageType.ERROR));
				return;
			}
		} else
			arr = Server.listClients("\n");

		if (channelName == null)
			caller.send(new MessagePacket("Users online are:" + "\n" + arr, MessageType.COMMAND));
		else
			caller.send(
					new MessagePacket("Users in channel " + channelName + " are:" + "\n" + arr, MessageType.COMMAND));
	}

}
