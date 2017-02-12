package eu.tankernn.chat.server.command;

import java.util.Optional;

import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.MessagePacket.MessageType;
import eu.tankernn.chat.server.Channel;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;

@CommandInfo(desc = "Removes caller from specified channel. (/leave <channel>)", name = "leave", permission = "user.leave", minArg = 1)
public class LeaveChannel implements Command {

	@Override
	public void execute(String[] args, Client caller) throws Exception {
		if (caller.equals(Server.getLocalClient())) {
			caller.send("Client-only command.");
			return;
		}

		Optional<Channel> maybeChannel = Server.getChannelByName(args[0]);
		Channel selectedChannel = maybeChannel.isPresent() ? maybeChannel.get() : null;

		try {
			selectedChannel.remove(caller);
			if (caller.getPrimaryChannel().equals(selectedChannel))
				caller.setPrimaryChannel(Server.getChannels().get(0));
			caller.send(new MessagePacket("You left channel " + args[0] + ".", MessageType.COMMAND));
			caller.send(new MessagePacket("You are now speaking in channel " + caller.getPrimaryChannel().name + ".",
					MessageType.COMMAND));
		} catch (NullPointerException ex) {
			caller.send(new MessagePacket("No channel named " + args[0] + ".", MessageType.ERROR));
			return;
		}
	}
}