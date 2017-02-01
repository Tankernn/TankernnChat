package server.command;

import common.MessagePacket;
import common.MessagePacket.MessageType;
import server.Client;
import server.Server;

@CommandInfo(desc = "Give a client OP permissions.", name = "op", permission = "admin.op", minArg = 1)
public class GiveOP implements Command {

	@Override
	public void execute(String[] args, Client caller) throws Exception {
		try {
			Client target = Server.getUserByName(args[0]).get();
			target.isOP = true;
			target.send(new MessagePacket("You are now OP.", MessageType.INFO));
		} catch (NullPointerException ex) {
			caller.send(new MessagePacket("No such user: " + args[0], MessageType.WARNING));
		}
	}

}
