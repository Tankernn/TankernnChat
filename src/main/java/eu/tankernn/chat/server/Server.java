package eu.tankernn.chat.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import eu.tankernn.chat.packets.MessagePacket;
import eu.tankernn.chat.packets.Packet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class Server {
	private static Properties prop = new Properties();
	private static File propFile = new File("server.properties");
	private static int port;
	private static final String VERSION = "4.0";

	private static final Logger LOG = Logger.getGlobal();

	private static ArrayList<BanNote> banNotes = new ArrayList<BanNote>();
	private static ArrayList<Channel> channels = new ArrayList<Channel>();
	private static ClientCollection clients;

	private static ServerBootstrap bootstrap;
	private static EventLoopGroup bossGroup = new NioEventLoopGroup();
	private static EventLoopGroup workerGroup = new NioEventLoopGroup();

	private static LocalClient localClient;
	private static CommandRegistry commandRegistry;
	private static Timer timer = new Timer();

	public static void main(String[] arg) {
		try {
			LogManager.getLogManager().readConfiguration(Server.class.getResourceAsStream("/logger.properties"));
		} catch (SecurityException | IOException e2) {
			LOG.log(Level.SEVERE, e2.getMessage(), e2);
		}
		LOG.info("Starting ChatServer version " + VERSION + "...");

		LOG.fine("Loadning properties file...");
		try {
			prop.load(new FileReader(propFile));
		} catch (FileNotFoundException e1) {
			try {
				prop.load(Server.class.getResourceAsStream("/" + propFile.getName()));
			} catch (IOException e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
			}
		} catch (IOException e1) {
			LOG.log(Level.SEVERE, e1.getMessage(), e1);
		}

		LOG.fine("Reading numbers from properties object...");
		port = Integer.parseInt(prop.getProperty("port"));

		clients = new ClientCollection();
		getChannels().add(new Channel("Main"));

		LOG.fine("Starting commandhandler...");
		commandRegistry = new CommandRegistry();

		LOG.fine("Creating virtual local client...");
		localClient = new LocalClient();

		LOG.fine("Starting client listener...");
		run();

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				clients.stream().forEach(Client::spamReset);
			}
		}, 1000, 1000);

		LOG.info("Server started successfully!");
	}

	public static void addClient(Client c) {
		clients.add(c);
		getChannels().get(0).add(c);
		c.send(new MessagePacket("Welcome to the server, " + c.username + "! Enjoy your stay!"));
		wideBroadcast(new MessagePacket(c.username + " has connected."));
	}

	private static void run() {
		try {
			bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("decoder", new ObjectDecoder(
									ClassResolvers.weakCachingResolver(Packet.class.getClassLoader())));
							ch.pipeline().addLast("encoder", new ObjectEncoder());
							ch.pipeline().addLast("timeouthandler", new ReadTimeoutHandler(5));
							ch.pipeline().addLast("handler", new ChatServerHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			// Bind and start to accept incoming connections.
			bootstrap.bind(port).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
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

		clients.disconnectAll();
		localClient.disconnect();
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
		timer.cancel();

		try {
			prop.store(new PrintWriter(propFile), "ChatServer config file");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static LocalClient getLocalClient() {
		return localClient;
	}

	public static Logger getLogger() {
		return LOG;
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
