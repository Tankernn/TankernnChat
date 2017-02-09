package eu.tankernn.chat.server.command;

import java.util.Optional;

import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.MessagePacket.MessageType;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;

@CommandInfo(desc = "Kicks a user. (/kick <username>)", name = "kick", permission = "admin.kick")
public class Kick implements Command {

	@Override
	public void execute(String[] args, Client caller) {
		Optional<Client> maybeVictim = Server.getUserByName(args[0]);

		try {
			maybeVictim.orElseThrow(NullPointerException::new).disconnect(false);
		} catch (NullPointerException ex) {
			caller.send(new MessagePacket("No user called " + args[0] + "!", MessageType.ERROR));
		}
	}
}
