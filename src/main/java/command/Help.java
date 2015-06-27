package command;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import common.Message;
import common.Message.MessageType;
import server.Client;
import server.Server;

public class Help extends Command {

	@Override
	public void execute(String[] args, Client caller) {
		String help = "Help for all commands:" + "\n";
		Iterator<Entry<String, Command>> it = Server.commReg.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, Command> pair = it.next();

			help += pair.getKey() + ": " + "\t" + pair.getValue().getDescription();
			if (it.hasNext())
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
	public String getDescription() {
		return "Writes the descriptions for all commands.";
	}

	@Override
	public String getPermission() {
		return "noob.help";
	}

}
