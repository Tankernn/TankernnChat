package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import common.Message;
import server.CommandHandler;
import server.Channel;

public class Server {
	static Properties prop = new Properties();
	static int port, maxUsers, maxChannels;
	static final String version = "0.3";
	
	public static ArrayList<BanNote> banNotes = new ArrayList<BanNote>();
	public static Channel[] channels;
	public static ClientCollection clients;
	
	static ServerSocket so;
	public static LocalClient OPClient;
	
	public static void main(String[] arg){
		System.out.println("Starting ChatServer version " + version + "...");
		
		loadProperties();
		
		System.out.println("Setting up socket.");
		try {
			so = new ServerSocket(port);
		} catch(IOException ex) {
			System.out.println("Error setting up socket. Server already running?");
			System.exit(0);
		}
		
		clients = new ClientCollection();
		channels = new Channel[maxChannels];
		channels[0] = new Channel("Main");
		
		System.out.println("Starting commandhandler!");
		new CommandHandler();
		
		System.out.println("Creating virtual local client!");
		OPClient = new LocalClient();
		
		System.out.println("Server started successfully!");
		
		getClients();
	}

	static void getClients() {
		while(true) {
			Client newClient = null;
			try {
				newClient = new Client(Server.so.accept());
				clients.add(newClient);
				channels[0].add(newClient);
				wideBroadcast(new Message(newClient.username + " has connected."));
			} catch (IllegalArgumentException ex) {
				
			} catch (ArrayIndexOutOfBoundsException ex) {
				newClient.send(new Message("Server full!"));
				newClient.disconnect(false);
			} catch (Exception ex) {
				System.out.println("Could not get new client!");
				ex.printStackTrace();
			}
		}
	}
	
	public static Channel getChannelByName(String name) throws NullPointerException {
		for (int i = 0; i < channels.length; i++) {
			if (channels[i] != null)
				if (channels[i].name.equals(name)) {
					return channels[i];
				}
		}
		return null;
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

	public static Client getUserByName(String username) {
		return clients.getClientByName(username);
	}
	
	public static void cleanUp() { //Makes sure the client gets removed from all arrays
		clients.cleanUp();
		for (Channel c: channels)
			if (c != null)
				c.cleanUp();
	}
	
	public static void exit() {
		wideBroadcast(new Message("Shutting down server!"));
		
		for (int i = 0; i < clients.size(); i++)
			clients.get(i).disconnect();
		
		System.exit(0);
	}
	
	static int CInt(String str) {
		int i;
		Scanner sc = new Scanner(str);
		i = sc.nextInt();
		sc.close();
		return i;
	}
	
	static void loadProperties() {
		System.out.println("Loadning properties file.");
		try {
			prop.load(new FileInputStream("server.properties"));
		} catch (FileNotFoundException e1) {
			newPropertiesFile();
		} catch (IOException e2) {
			System.out.println("Could not load properties.");
			e2.printStackTrace();
		}
		
		System.out.println("Reading numbers from properties object.");
		try {
			port = CInt(prop.getProperty("port"));
			maxUsers = CInt(prop.getProperty("maxUsers"));
			maxChannels = CInt(prop.getProperty("maxChannels"));
		} catch (NullPointerException ex) {
			System.out.println("Could not get values from properties file.");
			newPropertiesFile();
		}
	}
	
	static void newPropertiesFile() {
		System.out.println("Generating new preperties file.");
		try {
			new File("server.properties").createNewFile();
			prop.setProperty("port", "25566");
			prop.setProperty("maxUsers", "20");
			prop.setProperty("maxChannels", "10");
			prop.store(new FileWriter("server.properties"), "ChatServer config file");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
