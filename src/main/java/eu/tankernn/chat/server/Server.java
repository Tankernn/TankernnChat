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
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import eu.tankernn.chat.common.MessagePacket;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class Server {
	private static Thread clientListener;
	private static Properties prop = new Properties();
	private static File propFile = new File("server.properties");
	private static int port, maxUsers;
	private static final String version = "4.0";
	
	private static ArrayList<BanNote> banNotes = new ArrayList<BanNote>();
	private static ArrayList<Channel> channels = new ArrayList<Channel>();
	private static ClientCollection clients;
	
	private static ServerBootstrap bootstrap;
	
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
				prop.load(Server.class
						.getResourceAsStream("/" + propFile.getName()));
			} catch (IOException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		} catch (IOException e1) {
			log.log(Level.SEVERE, e1.getMessage(), e1);
		}
		
		log.fine("Reading numbers from properties object...");
		port = Integer.parseInt(prop.getProperty("port"));
		maxUsers = Integer.parseInt(prop.getProperty("maxUsers"));
		
		clients = new ClientCollection();
		getChannels().add(new Channel("Main"));
		
		log.fine("Starting commandhandler...");
		commandRegistry = new CommandRegistry();
		
		log.fine("Creating virtual local client...");
		OPClient = new LocalClient();
		
		log.fine("Starting client listener thread...");
		clientListener = new Thread(Server::run);
		clientListener.start();
		
		log.info("Server started successfully!");
	}
	
	public static void addClient(Client c) {
		clients.add(c);
		getChannels().get(0).add(c);
		wideBroadcast(new MessagePacket(c.username + " has connected."));
	}
	
	private static void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("decoder",
									new StringDecoder());
							ch.pipeline().addLast("encoder",
									new ObjectEncoder());
							ch.pipeline().addLast("timeouthandler",
									new ReadTimeoutHandler(5));
							ch.pipeline().addLast("handler",
									new ChatServerHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			// Bind and start to accept incoming connections.
			ChannelFuture f = bootstrap.bind(port).sync();
			
			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully
			// shut down your server.
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// No need to handle, just shut down
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public static Optional<Channel> getChannelByName(String name) throws NullPointerException {
		return getChannels().stream().filter(c -> c.name.equals(name))
				.findFirst();
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
		
		clientListener.interrupt();
		
		clients.disconnectAll();
		getLocalClient().disconnect();
		
		try {
			prop.store(new PrintWriter(propFile), "ChatServer config file");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static int getMaxUsers() {
		return maxUsers;
	}
	
	public static LocalClient getLocalClient() {
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
