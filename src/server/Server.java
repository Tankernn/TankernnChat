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
	static int port, maxUsers = 20, maxChannels = 10;
	static final String version = "0.3";
	
	public static ArrayList<BanNote> banNotes = new ArrayList<BanNote>();
	public static Channel[] channels = new Channel[maxChannels];
	public static Client[] clients = new Client[maxUsers];
	
	static ServerSocket so;
	public static LocalClient OPClient;
	
	public static void main(String[] arg){
		System.out.println("Starting ChatServer version " + version + "...");
		
		System.out.println("Loadning properties file!");
		try {
			prop.load(new FileInputStream("server.properties"));
		} catch (FileNotFoundException ex1) {
			try {
				new File("server.properties").createNewFile();
				prop.setProperty("port", "25566");
				prop.store(new FileWriter("server.properties"), "ChatServer config file");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			System.out.println("Could not load properties!");
			e.printStackTrace();
		}
		System.out.println("Reading portnumber from properties!");
		Scanner sc = new Scanner(prop.getProperty("port"));
		port = sc.nextInt();
		sc.close();
		
		System.out.println("Setting up socket!");
		try {
			so = new ServerSocket(port);
		} catch(IOException ex) {
			System.out.println("Error setting up socket! Server already running?");
			System.exit(0);
		}
		
		System.out.println("Starting main channel!");
		channels[0] = new Channel("Main");
		
		System.out.println("Starting commandhandler!");
		new CommandHandler();
		
		System.out.println("Creating virtual local client!");
		OPClient = new LocalClient();
		
		System.out.println("Server started successfully!");
		
		getClients();
	}
	
	static void getNewClient() throws IOException {
		Client newClient = new Client(Server.so.accept());
		if (newClient.readuser != null) {
			for (int i = 0; i < clients.length; i++)
				if (positionFree(i)) {
					clients[i] = newClient;
					channels[0].addUser(newClient);
					Server.broadcast(new Message(clients[i].username + " has connected."));
					return;
				}
		}
	}

	static void getClients() {
		while(true) {
			try {
				getNewClient();
			} catch (Exception ex) {
				System.out.println("Could not get new client!");
				ex.printStackTrace();
			}
		}
	}
	
	public static Client getUserByName(String name) throws NullPointerException {
		for (int i = 0; i < clients.length; i++) {
			if (!positionFree(i))
				if (clients[i].username.equals(name))
					return clients[i];
		}
		throw new NullPointerException();
	}
	
	public static Channel getChannelByName(String name) {
		for (int i = 0; i < channels.length; i++) {
			if (channels[i] != null)
				if (channels[i].name.equals(name)) {
					return channels[i];
				}
		}
		return null;
	}
	
	static boolean positionFree(int pos) {
		return clients[pos] == null || !clients[pos].isConnected();
	}

	public static void broadcast(Message mess) {
		for (int i = 0; i < clients.length; i++)
			if (!positionFree(i))
				clients[i].send(mess);
		OPClient.send(mess.toString());
	}
	
	public static void broadcast(Object mess) {
		for (int i = 0; i < clients.length; i++)
			if (!positionFree(i))
				clients[i].send(mess);
	}

	public static String[] getUsersOnline() {
		ArrayList<String> usersOnline = new ArrayList<String>();
		for (int i = 0; i < clients.length; i++)
			if (!Server.positionFree(i)) {
				usersOnline.add(clients[i].username);
			}
		String[] usersOnlineStr = new String[usersOnline.size()];
		for (int i = 0; i < usersOnline.size(); i++)
			usersOnlineStr[i] = usersOnline.get(i);
		return usersOnlineStr;
	}
	
	public static String getUsersOnlineString() {
		String[] usersOnlineArr = getUsersOnline();
		String usersOnline = "";
		
		for (int i = 0; i < usersOnlineArr.length; i++) {
			if (i != 0)
				usersOnline += "\n";
			usersOnline += usersOnlineArr[i];
		}
		return usersOnline;
	}
}
