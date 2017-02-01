package server.command;

import java.util.Optional;

import common.MessagePacket;
import common.MessagePacket.MessageType;
import server.Client;
import server.Server;

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
