package command;

import server.Client;

public abstract class Command {
	public abstract void execute (String[] args, Client caller) throws Exception;
	public abstract String getName ();
	public abstract String getPermission ();
	public abstract String getDescription ();
	public abstract int getMinArgNumber ();
}
