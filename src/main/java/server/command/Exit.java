package server.command;

import server.Client;
import server.Server;

@CommandInfo(desc = "Stops the server.", name = "stop", permission = "server.exit")
public class Exit implements Command {
	
	@Override
	public void execute(String[] args, Client caller) {
		Server.exit();
	}
	
}
