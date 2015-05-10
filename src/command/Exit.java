package command;

import server.Client;

public class Exit extends Command{

	@Override
	public void execute(String[] args, Client caller) {
		caller.send("Shutting down server!");
		System.exit(0);
	}

	@Override
	public String setName() {
		return "exit";
	}

	@Override
	public int setMinArgNumber() {
		return 0;
	}

	@Override
	public String writeDescription() {
		return "Exits the server.";
	}

	@Override
	public String setPermission() {
		return "server.exit";
	}

}
