package server.command;

import common.MessagePacket;
import common.MessagePacket.MessageType;
import server.Client;
import server.Server;

@CommandInfo(desc = "Writes the descriptions for all commands.", name = "help", permission = "user.help")
public class Help implements Command {

	@Override
	public void execute(String[] args, Client caller) {
		caller.send(new MessagePacket(Server.getCommReg().getHelp(), MessageType.COMMAND));
	}

}
