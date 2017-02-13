package eu.tankernn.chat.server.command;

import java.util.Deque;
import java.util.Optional;

import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.MessagePacket.MessageType;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;

@CommandInfo(desc = "Sends a private message to a user", name = "pm", permission = "user.pm", minArg = 2)
public class PrivateMessage implements Command {

	@Override
	public void execute(Deque<String> args, Client caller) {
		Client reciever;
		String name = args.pop();
		Optional<Client> maybeVictim = Server.getUserByName(name);

		if (maybeVictim.isPresent())
			reciever = maybeVictim.get();
		else {
			caller.send(new MessagePacket("No user called " + name + ".", MessageType.ERROR));
			return;
		}
		if (caller.equals(reciever)) {
			caller.send("Please don't speak with yourself.");
			return;
		}

		MessagePacket mess = new MessagePacket("PM", caller.username,
				String.join(" ", args), MessagePacket.MessageType.PM);

		reciever.send(mess);
		caller.send(mess);

	}

}
