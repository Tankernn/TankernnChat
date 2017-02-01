package server.command;

import server.Client;

public interface Command {
	public void execute(String[] args, Client caller) throws Exception;
}
