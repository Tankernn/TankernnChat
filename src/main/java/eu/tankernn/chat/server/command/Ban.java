package eu.tankernn.chat.server.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Optional;

import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.MessagePacket.MessageType;
import eu.tankernn.chat.server.BanNote;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;
import eu.tankernn.chat.util.ArrayUtil;

@CommandInfo(desc = "Bans a user. (/ban <username> [seconds] [reason])", name = "ban", permission = "admin.ban", minArg = 1)
public class Ban implements Command {
	
	@Override
	public void execute(String[] args, Client caller) {
		String IP = null;
		int duration = -1;
		Client victim;
		
		Optional<Client> maybeVictim = Server.getUserByName(args[0]);
		
		if (maybeVictim.isPresent())
			victim = maybeVictim.get();
		else {
			caller.send(new MessagePacket("No user called " + args[0] + ".", MessageType.ERROR));
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
		
		if (args.length != 1)
			try {
				duration = Integer.parseInt(args[1]);
				
				if (args.length >= 3)
					bn = new BanNote(IP, duration, Arrays.toString(ArrayUtil.removeFirst(ArrayUtil.removeFirst(args))));
				else
					bn = new BanNote(IP, duration);
			} catch (InputMismatchException ime) {
				bn = new BanNote(IP, Arrays.toString(ArrayUtil.removeFirst(args)));
			}
		
		Server.ban(bn);
	}
	
}
