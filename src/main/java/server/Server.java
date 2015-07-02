package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Optional;

import util.Logger;
import util.ServerProperties;

import common.Message;

public class Server {
	static ServerProperties prop = new ServerProperties();
	static int port, maxUsers;
	static final String version = "0.3";
	
	public static ArrayList<BanNote> banNotes = new ArrayList<BanNote>();
	public static ArrayList<Channel> channels;
	public static ClientCollection clients;
	
	static ServerSocket so;
	public static LocalClient OPClient;
	public static Logger log;
	public static CommandRegistry commReg;
	
	public static void main(String[] arg) {
		System.out.println("Starting ChatServer version " + version + "...");
		
		System.out.print("Loadning properties file...");
		prop.loadProperties();
		System.out.println("Done");
		
		System.out.print("Reading numbers from properties object...");
		port = prop.getNumberProperty("port");
		maxUsers = prop.getNumberProperty("maxUsers");
		System.out.println("Done");
		
		System.out.print("Setting up socket...");
		try {
			so = new ServerSocket(port);
		} catch (IOException ex) {
			System.out.println("Error setting up socket. Server already running?");
			System.exit(0);
		}
		System.out.println("Done");
		
		clients = new ClientCollection();
		channels = new ArrayList<Channel>();
		channels.add(new Channel("Main"));
		
		System.out.print("Starting commandhandler...");
		commReg = new CommandRegistry();
		System.out.println("Done");
		
		System.out.print("Creating virtual local client...");
		OPClient = new LocalClient();
		System.out.println("Done");
		
		System.out.print("Starting logger...");
		try {
			log = new Logger();
		} catch (IOException e) {
			System.out.println();
			System.out.println("Unable to start logger.");
			e.printStackTrace();
			return;
		}
		System.out.println("Done");
		
		System.out.println("Server started successfully!");
		
		getClients();
	}
	
	static void getClients() {
		while (!so.isClosed()) {
			Client newClient = null;
			try {
				newClient = new Client(Server.so.accept());
				clients.add(newClient);
				channels.get(0).add(newClient);
				wideBroadcast(new Message(newClient.username + " has connected."));
			} catch (IllegalArgumentException ex) {
				
			} catch (ArrayIndexOutOfBoundsException ex) {
				newClient.send(new Message("Server full!"));
				newClient.disconnect(false);
			} catch (IOException ex) {
				if (so.isClosed())
					return;
			} catch (Exception ex) {
				System.out.println("Could not get new client!");
				ex.printStackTrace();
			}
		}
	}
	
	public static Optional<Channel> getChannelByName(String name) throws NullPointerException {
		return channels.stream().filter(c -> c.name.equals(name)).findFirst();
	}
	
	public static void wideBroadcast(Message mess) {
		clients.broadcast(mess);
	}
	
	public static String[] getUsersOnline() {
		return clients.listClientsArray();
	}
	
	public static String listClients() {
		return clients.listClients();
	}
	
	public static Optional<Client> getUserByName(String username) {
		return clients.getClientByName(username);
	}
	
	public static void cleanUp() { //Makes sure the client gets removed from all arrays
		clients.cleanUp();
		channels.forEach(c -> c.cleanUp());
	}
	
	public static void exit() {
		wideBroadcast(new Message("Shutting down server!"));
		
		for (int i = 0; i < clients.size(); i++)
			//Has to be done with number iteration, otherwise unsafe
			clients.get(i).disconnect();
		
		log.close();
		
		OPClient.disconnect();
		
		try {
			so.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
