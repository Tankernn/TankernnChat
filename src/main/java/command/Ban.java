package command;

import java.util.InputMismatchException;

import common.Message;
import common.Message.MessageType;
import server.Client;
import server.Server;
import server.BanNote;
import util.Numbers;
import util.StringArrays;

public class Ban extends Command {

	@Override
	public void execute(String[] args, Client caller) {
		String IP = null;
		int duration = -1;
		Client victim;
		
		try {
			victim = Server.getUserByName(args[0]);
		}	catch (NullPointerException e) {
			caller.send(new Message("No such user!", MessageType.WARNING, false));
			return;
		}
		
		IP = victim.sock.getInetAddress().toString();
		
		BanNote bn = new BanNote(IP);
		
		if (args.length == 1)
			bn = new BanNote(IP);
		else
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
		victim.disconnect(false);
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
