package eu.tankernn.chat.server.command;

import java.util.Arrays;
import java.util.Optional;

import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.MessagePacket.MessageType;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;
import eu.tankernn.chat.util.ArrayUtil;

@CommandInfo(desc = "Sends a private message to a user", name = "pm", permission = "user.pm", minArg = 2)
public class PrivateMessage implements Command {

	@Override
	public void execute(String[] args, Client caller) {
		Client reciever;
		Optional<Client> maybeVictim = Server.getUserByName(args[0]);

		if (maybeVictim.isPresent())
			reciever = maybeVictim.get();
		else {
			caller.send(new MessagePacket("No user called " + args[0] + ".", MessageType.ERROR));
			return;
		}
		if (caller.equals(reciever)) {
			caller.send("Please don't speak with yourself.");
			return;
		} else if (reciever == null) {
			caller.send("No user called " + args[0] + ".");
			return;
		}

		MessagePacket mess = new MessagePacket("PM", caller.username,
				Arrays.toString(ArrayUtil.removeFirst(args)), MessagePacket.MessageType.PM);

		reciever.send(mess);
		caller.send(mess);

	}

}
