package eu.tankernn.chat.server.command;

import java.util.Deque;

import eu.tankernn.chat.server.Client;

public interface Command {
	public void execute(Deque<String> args, Client caller) throws Exception;
}
