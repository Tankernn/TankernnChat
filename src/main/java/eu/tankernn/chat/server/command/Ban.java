package eu.tankernn.chat.server.command;

import java.io.IOException;
import java.util.Deque;
import java.util.InputMismatchException;
import java.util.Optional;

import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.MessagePacket.MessageType;
import eu.tankernn.chat.server.BanNote;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;

@CommandInfo(desc = "Bans a user. (/ban <username> [seconds] [reason])", name = "ban", permission = "admin.ban", minArg = 1)
public class Ban implements Command {
	
	@Override
	public void execute(Deque<String> args, Client caller) {
		String IP = null;
		int duration = -1;
		Client victim;
		String name = args.pop();
		
		Optional<Client> maybeVictim = Server.getUserByName(name);
		
		if (maybeVictim.isPresent())
			victim = maybeVictim.get();
		else {
			caller.send(new MessagePacket("No user called " + name + ".", MessageType.ERROR));
			return;
		}
		
		try {
			IP = victim.getIP();
		} catch (IOException e) {
			e.printStackTrace();
			caller.send(new MessagePacket("Error getting target IP address.", MessageType.ERROR));
			return;
		}
		
		BanNote bn = new BanNote(IP);
		
		if (!args.isEmpty())
			try {
				duration = Integer.parseInt(args.pop());
				
				if (!args.isEmpty())
					bn = new BanNote(IP, duration, String.join(" ", args));
				else
					bn = new BanNote(IP, duration);
			} catch (InputMismatchException ime) {
				bn = new BanNote(IP, String.join(" ", args));
			}
		
		Server.ban(bn);
	}
	
}
