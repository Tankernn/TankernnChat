package command;

import server.Client;

public abstract class Command {
	public String name, permission = "*";
	public int argNumber;
	
	public Command () {
		name = setName();
		argNumber = setMinArgNumber();
		permission = setPermission();
	}

	public abstract void execute (String[] args, Client caller);
	public abstract String setName ();
	public abstract String setPermission ();
	public abstract String writeDescription ();
	public abstract int setMinArgNumber ();
}
