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

	public abstract void execute (String[] args, Client caller) throws Exception;
	public abstract String setName ();
	public abstract String setPermission ();
	public abstract String writeDescription ();
	public abstract int setMinArgNumber ();
	
	public String stringArrayToString(String[] strArr) {
		String content = "";
		for (int i = 1; i < strArr.length -1; i++) {
			content += strArr[i] + " ";
		}
		return content;
	}
}
