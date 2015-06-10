package server;

import command.*;

public class CommandHandler {
	
	public static Command[] commands;
	
	public CommandHandler() {
		commands = new Command[9];
		commands[0] = new command.Kick();
		commands[1] = new command.List();
		commands[2] = new command.Exit();
		commands[3] = new command.Help();
		commands[4] = new command.PrivateMessage();
		commands[5] = new command.JoinChannel();
		commands[6] = new command.Ban();
		commands[7] = new command.LeaveChannel();
		commands[8] = new command.CreateChannel();
	}
	
	public static void executeCommand(String[] command, Client caller) {
		for (Command comm: commands) { //Go through all commands
			if ((comm.getName()).equals(command[0])) { //Look for command with correct name
				if (caller.hasPermission(comm.getPermission())) //Check if the client has permission
					if (command.length -1 >= comm.getMinArgNumber()) { //Check the number of arguments
						try {
							comm.execute(removeFirst(command), caller); //Execute command
						} catch (Exception e) {
							caller.send("Error while executing command!");
							e.printStackTrace();
						} 
						return;
					} else {
						caller.send("More arguments required!");
						return;
					}
				else {
					caller.send("Not enough permissions!");
					return;
				}
			}
		}
		caller.send("No such command!");
	}
	
	public static String[] removeFirst(String[] command) {
		String[] newCommand = new String[command.length - 1];
		for (int i = 0; i < command.length -1; i++) {
			newCommand[i] = command[i + 1];
		}
		return newCommand;
	}
	
	public static String stringArrayToString(String[] strArr) {
		return strArr.toString().replace("[", "").replace("]", "").replace(", ", " ");
	}
}