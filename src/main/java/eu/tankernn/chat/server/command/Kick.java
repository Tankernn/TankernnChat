package eu.tankernn.chat.server.command;

import java.util.Deque;
import java.util.Optional;

import eu.tankernn.chat.packets.MessagePacket;
import eu.tankernn.chat.packets.MessagePacket.MessageType;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;

@CommandInfo(desc = "Kicks a user. (/kick <username>)", name = "kick", permission = "admin.kick")
public class Kick implements Command {

	@Override
	public void execute(Deque<String> args, Client caller) {
		String name = args.pop();
		Optional<Client> maybeVictim = Server.getUserByName(name);

		try {
			maybeVictim.orElseThrow(NullPointerException::new).disconnect();
		} catch (NullPointerException ex) {
			caller.send(new MessagePacket("No user called " + name + "!", MessageType.ERROR));
		}
	}
}
