package command;

import server.Client;
import server.Server;

import common.Command;

public class Exit extends Command {
	
	@Override
	public void execute(String[] args, Client caller) {
		Server.exit();
	}
	
	@Override
	public String getName() {
		return "exit";
	}
	
	@Override
	public int getMinArgNumber() {
		return 0;
	}
	
	@Override
	public String getDescription() {
		return "Exits the server.";
	}
	
	@Override
	public String getPermission() {
		return "server.exit";
	}
	
}
