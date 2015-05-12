package command;

import java.util.InputMismatchException;
import java.util.Scanner;

import server.Client;
import server.CommandHandler;
import server.Server;
import server.BanNote;

public class Ban extends Command {

	@Override
	public void execute(String[] args, Client caller) {
		String IP = null;
		int duration = -1;
		Client victim;
		
		try {
			victim = Server.getUserByName(args[0]);
		}	catch (NullPointerException e) {
			caller.send("No such user!");
			return;
		}
		
		IP = victim.sock.getInetAddress().toString();
		
		
		BanNote bn = new BanNote(IP);
		
		if (args.length == 1)
			bn = new BanNote(IP);
		else
			try {
				duration = new Scanner(args[1]).nextInt();
				
				if (args.length >= 3)
					bn = new BanNote(IP, duration, this.stringArrayToString(CommandHandler.removeFirst(CommandHandler.removeFirst(args))));
				else
					bn = new BanNote(IP, duration);
			} catch (InputMismatchException ime) {
				bn = new BanNote(IP, this.stringArrayToString(CommandHandler.removeFirst(args)));
			}
		
		Server.banNotes.add(bn);
		victim.disconnect(false);
	}

	@Override
	public String setName() {
		return "ban";
	}

	@Override
	public String setPermission() {
		return "server.ban";
	}

	@Override
	public String writeDescription() {
		return "Bans a user. (/ban <username> [seconds] [reason])";
	}

	@Override
	public int setMinArgNumber() {
		return 1;
	}

}
