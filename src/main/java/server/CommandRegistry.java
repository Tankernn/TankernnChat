package server;

import java.util.HashMap;

import util.StringArrays;

import command.Command;

public class CommandRegistry extends HashMap<String, Command> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CommandRegistry() {
		Command[] commands = new Command[9];
		commands[0] = new command.Kick();
		commands[1] = new command.List();
		commands[2] = new command.Exit();
		commands[3] = new command.Help();
		commands[4] = new command.PrivateMessage();
		commands[5] = new command.JoinChannel();
		commands[6] = new command.Ban();
		commands[7] = new command.LeaveChannel();
		commands[8] = new command.CreateChannel();

		for (Command comm : commands)
			put(comm.getName(), comm);
	}

	public void executeCommand(String[] command, Client caller) {
		Command comm;
		if ((comm = get(command[0])) != null) // Get the command
			if (caller.hasPermission(comm.getPermission())) { // Check if the client has permission
				String[] args = StringArrays.removeFirst(command);
				if (args.length >= comm.getMinArgNumber()) { // Check the number of arguments
					try {
						comm.execute(args, caller); // Execute command
					} catch (Exception e) {
						caller.send("Error while executing command!");
						e.printStackTrace();
					}
					return;
				} else {
					caller.send("More arguments required!");
					return;
				}
			} else {
				caller.send("Not enough permissions!");
				return;
			}
		else
			caller.send("No such command! Type '/help' for a list of commands.");
	}

}