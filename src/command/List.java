package command;

import server.Client;
import server.Server;

public class List extends Command {
	
	@Override
	public void execute(String[] args, Client caller) {
		caller.send("Users online are:" + "\n" + Server.getUsersOnlineString());
	}

	@Override
	public String setName() {
		return "list";
	}

	@Override
	public int setMinArgNumber() {
		return 0;
	}

	@Override
	public String writeDescription() {
		return "Lists all users online.";
	}

	@Override
	public String setPermission() {
		return "noob.list";
	}

}
