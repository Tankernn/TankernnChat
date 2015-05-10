package command;

import server.Client;
import server.Server;

public class Ban extends Command {

	@Override
	public void execute(String[] args, Client caller) {
		try {
			Server.bannedIps.add(Server.getUserByName(args[0]).sock.getInetAddress().toString());
			Server.getUserByName(args[0]).disconnect(false);
		} catch (NullPointerException e) {
			caller.send("No such user!");
		}
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
		return "Bans a user. (/ban <username>)";
	}

	@Override
	public int setMinArgNumber() {
		return 1;
	}

}
