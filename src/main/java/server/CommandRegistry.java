package server;

import java.util.HashMap;
import java.util.List;

import util.ClassFinder;
import util.StringArrays;

import common.Command;

public class CommandRegistry extends HashMap<String, Command> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CommandRegistry() {
		List<Class<?>> classes = ClassFinder.find("command");
		
		for (Class<?> comm: classes) {
			try {
				Command commInstance = (Command) comm.newInstance();
				put(commInstance.getName(), commInstance);
			} catch (ClassCastException | InstantiationException | IllegalAccessException ex) {
				ex.printStackTrace();
			}
		}
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