package server.command;

import common.Command;
import common.MessagePacket;
import common.MessagePacket.MessageType;
import server.Client;
import server.Server;

public class GiveOP extends Command {

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

	@Override
	public String getName() {
		return "op";
	}

	@Override
	public String getPermission() {
		// TODO Auto-generated method stub
		return "server.op";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Give a client OP permissions.";
	}

	@Override
	public int getMinArgNumber() {
		// TODO Auto-generated method stub
		return 1;
	}

}
