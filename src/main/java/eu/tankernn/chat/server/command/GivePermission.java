package eu.tankernn.chat.server.command;

import java.util.Deque;

import eu.tankernn.chat.packets.MessagePacket;
import eu.tankernn.chat.packets.MessagePacket.MessageType;
import eu.tankernn.chat.server.Client;
import eu.tankernn.chat.server.Server;

@CommandInfo(desc = "Give a client specified permissions.", name = "perm", permission = "admin.permission", minArg = 2)
public class GivePermission implements Command {

	@Override
	public void execute(Deque<String> args, Client caller) throws Exception {
		String name = args.pop();
		try {
			Client target = Server.getUserByName(name).get();
			target.addPermission(args.pop());
			target.send(new MessagePacket("You are now OP.", MessageType.INFO));
		} catch (NullPointerException ex) {
			caller.send(new MessagePacket("No such user: " + name, MessageType.WARNING));
		}
	}

}
