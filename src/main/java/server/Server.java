package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import common.MessagePacket;

public class Server {
	private static Thread clientListener;
	private static Properties prop = new Properties();
	private static File propFile = new File("server.properties");
	private static int port, maxUsers;
	private static final String version = "0.3";

	private static ArrayList<BanNote> banNotes = new ArrayList<BanNote>();
	private static ArrayList<Channel> channels = new ArrayList<Channel>();
	private static ClientCollection clients;

	private static ServerSocket so;
	private static LocalClient OPClient;
	private static final Logger log = Logger.getGlobal();
	private static CommandRegistry commandRegistry;

	public static void main(String[] arg) {
		try {
			LogManager.getLogManager().readConfiguration(Server.class.getResourceAsStream("/logger.properties"));
		} catch (SecurityException | IOException e2) {
			log.log(Level.SEVERE, e2.getMessage(), e2);
		}
		log.info("Starting ChatServer version " + version + "...");

		log.fine("Loadning properties file...");
		try {
			prop.load(new FileReader(propFile));
		} catch (FileNotFoundException e1) {
			try {
				prop.load(Server.class.getResourceAsStream("/" + propFile.getName()));
			} catch (IOException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		} catch (IOException e1) {
			log.log(Level.SEVERE, e1.getMessage(), e1);
		}

		log.fine("Reading numbers from properties object...");
		port = Integer.parseInt(prop.getProperty("port"));
		maxUsers = Integer.parseInt(prop.getProperty("maxUsers"));

		log.fine("Setting up socket...");
		try {
			so = new ServerSocket(port);
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Error setting up socket. Server already running?", ex);
			return;
		}

		clients = new ClientCollection();
		getChannels().add(new Channel("Main"));

		log.fine("Starting commandhandler...");
		commandRegistry = new CommandRegistry();

		log.fine("Creating virtual local client...");
		OPClient = new LocalClient();

		log.fine("Starting client listener thread...");
		clientListener = new Thread(Server::listenClients);
		clientListener.start();

		log.info("Server started successfully!");
	}

	static void listenClients() {
		while (!so.isClosed()) {
			Client newClient = null;
			try {
				Socket clientSock = so.accept();
				clients.cleanUp(); // Free taken names
				newClient = new Client(clientSock);
				clients.add(newClient);
				getChannels().get(0).add(newClient);
				wideBroadcast(new MessagePacket(newClient.username + " has connected."));
			} catch (IllegalArgumentException ex) {

			} catch (ArrayIndexOutOfBoundsException ex) {
				newClient.send(new MessagePacket("Server full!"));
				newClient.disconnect(false);
			} catch (IOException ex) {
				if (so.isClosed())
					return;
			} catch (Exception ex) {
				log.log(Level.WARNING, "Could not get new client!", ex);
			}
		}
	}

	public static Optional<Channel> getChannelByName(String name) throws NullPointerException {
		return getChannels().stream().filter(c -> c.name.equals(name)).findFirst();
	}

	public static void wideBroadcast(MessagePacket mess) {
		getClients().broadcast(mess);
	}

	public static String[] getUsersOnline() {
		return getClients().getUsernameArray();
	}

	public static String listClients(CharSequence c) {
		return getClients().listClients(c);
	}

	public static Optional<Client> getUserByName(String username) {
		return getClients().getClientByName(username);
	}

	public static void ban(BanNote ban) {
		banNotes.add(ban);
	}

	/**
	 * Removes disconnected clients from all collections on the server.
	 */
	public static void cleanUp() {
		getClients().cleanUp();
		getChannels().forEach(c -> c.cleanUp());
	}

	/**
	 * Disconnects all users and closes log and socket.
	 */
	public static void exit() {
		wideBroadcast(new MessagePacket("Shutting down server!"));

		try {
			so.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		clientListener.interrupt();

		clients.disconnectAll();
		getOPClient().disconnect();

		try {
			prop.store(new PrintWriter(propFile), "ChatServer config file");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static int getMaxUsers() {
		return maxUsers;
	}

	public static LocalClient getOPClient() {
		return OPClient;
	}

	public static Logger getLogger() {
		return log;
	}

	public static ArrayList<Channel> getChannels() {
		return channels;
	}

	public static ClientCollection getClients() {
		return clients;
	}

	public static CommandRegistry getCommReg() {
		return commandRegistry;
	}

	public static List<BanNote> getBanned() {
		return banNotes;
	}

}
