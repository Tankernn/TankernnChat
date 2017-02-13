package eu.tankernn.chat.server.command;

import java.util.Deque;

import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.MessagePacket.MessageType;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;

@CommandInfo(desc = "Writes the descriptions for all commands.", name = "help", permission = "user.help")
public class Help implements Command {

	@Override
	public void execute(Deque<String> args, Client caller) {
		caller.send(new MessagePacket(Server.getCommReg().getHelp(), MessageType.COMMAND));
	}

}
