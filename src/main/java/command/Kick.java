package command;

import java.util.Optional;

import server.Client;
import server.Server;

import common.Command;
import common.MessagePacket;
import common.MessagePacket.MessageType;

public class Kick extends Command {
	
	@Override
	public void execute(String[] args, Client caller) {
		Optional<Client> maybeVictim = Server.getUserByName(args[0]);
		
		try {
			maybeVictim.orElseThrow(NullPointerException::new).disconnect(false);
		} catch (NullPointerException ex) {
			caller.send(new MessagePacket("No user called " + args[0] + "!", MessageType.ERROR));
		}
	}
	
	@Override
	public String getName() {
		return "kick";
	}
	
	@Override
	public int getMinArgNumber() {
		return 1;
	}
	
	@Override
	public String getDescription() {
		return "Kicks a user. (/kick <username>)";
	}
	
	@Override
	public String getPermission() {
		return "server.kick";
	}
}
