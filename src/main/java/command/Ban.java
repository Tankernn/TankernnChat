package command;

import java.util.InputMismatchException;
import java.util.Optional;

import server.BanNote;
import server.Client;
import server.Server;
import util.Numbers;
import util.StringArrays;

import common.Command;
import common.Message;
import common.Message.MessageType;

public class Ban extends Command {
	
	@Override
	public void execute(String[] args, Client caller) {
		String IP = null;
		int duration = -1;
		Client victim;
		
		Optional<Client> maybeVictim = Server.getUserByName(args[0]);
		
		if (maybeVictim.isPresent())
			victim = maybeVictim.get();
		else {
			caller.send(new Message("No user called " + args[0] + ".", MessageType.ERROR, false));
			return;
		}
		
		IP = victim.sock.getInetAddress().toString();
		
		BanNote bn = new BanNote(IP);
		
		if (args.length != 1)
			try {
				duration = Numbers.CInt(args[1]);
				
				if (args.length >= 3)
					bn = new BanNote(IP, duration, StringArrays.arrayToString(StringArrays.removeFirst(StringArrays.removeFirst(args))));
				else
					bn = new BanNote(IP, duration);
			} catch (InputMismatchException ime) {
				bn = new BanNote(IP, StringArrays.arrayToString(StringArrays.removeFirst(args)));
			}
		
		Server.banNotes.add(bn);
	}
	
	@Override
	public String getName() {
		return "ban";
	}
	
	@Override
	public String getPermission() {
		return "server.ban";
	}
	
	@Override
	public String getDescription() {
		return "Bans a user. (/ban <username> [seconds] [reason])";
	}
	
	@Override
	public int getMinArgNumber() {
		return 1;
	}
	
}
