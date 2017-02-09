package eu.tankernn.chat.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.reflections.Reflections;

import eu.tankernn.chat.common.MessagePacket;
import eu.tankernn.chat.common.MessagePacket.MessageType;
import eu.tankernn.chat.server.command.Command;
import eu.tankernn.chat.server.command.CommandInfo;
import eu.tankernn.chat.util.ArrayUtil;

public class CommandRegistry  {
	private static final Logger LOG = Logger.getLogger(CommandRegistry.class.getName());
	private Map<String, Command> commands = new HashMap<>();
	
	private static final String COMMAND_PACKAGE = "server.command";
	
	public CommandRegistry() {
		Reflections reflections = new Reflections(COMMAND_PACKAGE);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(CommandInfo.class);
		
		for (Class<?> comm: annotated) {
			try {
				CommandInfo c = comm.getAnnotation(CommandInfo.class);
				
				if (!Arrays.asList(comm.getInterfaces()).contains(Command.class)) {
					LOG.warning(comm.getName() + " is annoteded with " + CommandInfo.class.getName() + ", but does not implement" + Command.class.getName());
					continue;
				}
				
				commands.put(c.name(), (Command) comm.newInstance());
			} catch (ClassCastException | InstantiationException | IllegalAccessException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void executeCommand(String commandStr, Client caller) {
		String[] command = commandStr.substring(1).split(" ");
		
		Command comm;
		if ((comm = commands.get(command[0])) != null) { // Get the command
			CommandInfo info = comm.getClass().getAnnotation(CommandInfo.class);
			if (caller.hasPermission(info.permission())) { // Check if the client has permission
				String[] args = ArrayUtil.removeFirst(command);
				if (args.length >= info.minArg()) { // Check the number of arguments
					try {
						comm.execute(args, caller); // Execute command
					} catch (Exception e) {
						caller.send(new MessagePacket("Error while executing command!", MessageType.ERROR));
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
		} else
			caller.send("No such command! Type '/help' for a list of commands.");
	}
	
	public String getHelp() {
		StringBuilder help = new StringBuilder();
		help.append("Help for all commands:" + "\n");
		
		Stream<Entry<String, Command>> stream = commands.entrySet().stream();
		
		stream.map(c -> c.getValue().getClass().getAnnotation(CommandInfo.class)).map((a) -> a.name() + ": " + "\t" + a.desc() + "\n").forEach(help::append);
		
		return help.toString();
	}
	
}