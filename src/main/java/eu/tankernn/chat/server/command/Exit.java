package eu.tankernn.chat.server.command;

import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;

@CommandInfo(desc = "Stops the server.", name = "stop", permission = "server.exit")
public class Exit implements Command {
	
	@Override
	public void execute(String[] args, Client caller) {
		Server.exit();
	}
	
}
