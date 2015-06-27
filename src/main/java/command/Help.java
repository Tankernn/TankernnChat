package command;

import common.Message;
import common.Message.MessageType;
import server.Client;
import server.CommandHandler;

public class Help extends Command {

	@Override
	public void execute(String[] args, Client caller) {
		String help = "Help for all commands:" + "\n";
		for (int i = 0; i < CommandHandler.commands.length; i++) {
			help += CommandHandler.commands[i].getName() + ": ";
			help += "\t";
			help += CommandHandler.commands[i].writeDescription();
			if (i + 1 < CommandHandler.commands.length)
				help += "\n";
		}
		caller.send(new Message(help, MessageType.COMMAND, false));
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public int getMinArgNumber() {
		return 0;
	}

	@Override
	public String writeDescription() {
		return "Writes the descriptions for all commands.";
	}

	@Override
	public String getPermission() {
		return "noob.help";
	}

}
