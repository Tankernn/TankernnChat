package eu.tankernn.chat.server.command;

import eu.tankernn.chat.server.Client;

public interface Command {
	public void execute(String[] args, Client caller) throws Exception;
}
