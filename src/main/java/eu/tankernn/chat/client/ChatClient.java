package eu.tankernn.chat.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import eu.tankernn.chat.client.filesend.FileSendWindow;
import eu.tankernn.chat.packets.MessagePacket;
import eu.tankernn.chat.packets.MessagePacket.MessageType;
import eu.tankernn.chat.packets.Packet;
import eu.tankernn.chat.packets.StringPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class ChatClient {
	private static Properties prop = new Properties();
	private static File confFile = new File("client.properties");
	private static ChatClient instance;
	
	private EventLoopGroup workerGroup = new NioEventLoopGroup();
	private Channel c;
	
	private FileSendWindow fileWindow;
	private ChatWindow chatWindow;
	
	public final String address, username;
	private int port;
	
	public ChatClient(String adress, int port, String username) {
		this.username = username;
		this.address = adress;
		this.port = port;
		
		chatWindow = new ChatWindow(this);
		fileWindow = new FileSendWindow(this);
		
		connect(); 
	}
	
	public void send(String text) {
		send(new StringPacket(text));
	}
	
	public void send(Packet pack) {
		ChannelFuture cf = null;
		try {
			cf = c.writeAndFlush(pack).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!cf.isSuccess()) {
			chatWindow.log(new MessagePacket(
					"Error sending message.",
					MessageType.WARNING));
			cf.cause().printStackTrace();
		}
	}
	
	public void connect() {
		if (!workerGroup.isShutdown())
			workerGroup.shutdownGracefully();
		workerGroup = new NioEventLoopGroup();
		
		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast("decoder",
						new ObjectDecoder(ClassResolvers.weakCachingResolver(
								Packet.class.getClassLoader())));
				ch.pipeline().addLast("encoder", new ObjectEncoder());
				ch.pipeline().addLast(new IdleStateHandler(0, 4, 0));
				ch.pipeline().addLast("handler",
						new ChatClientHandler(ChatClient.this));
			}
		});
		
		// Start the client.
		try {
			c = b.connect(address, port).sync().channel();
			// Set username
			send(username);
			// Send empty message to get info packet
			send("");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		return c.isActive();
	}
	
	public static void main(String[] arg) {
		try {
			prop.load(new FileInputStream(confFile));
		} catch (FileNotFoundException e) {
			prop.setProperty("host", "tankernn.eu");
			prop.setProperty("port", "25566");
			prop.setProperty("username", "Username");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JTextField hostBox = new JTextField(prop.getProperty("host"));
		JTextField portBox = new JTextField(prop.getProperty("port"));
		JTextField userBox = new JTextField(prop.getProperty("username"));
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Host:"), hostBox,
				new JLabel("Port:"), portBox,
				new JLabel("Username:"), userBox
		};
		
		String host, username, portString;
		JOptionPane.showMessageDialog(null, inputs, "Chat settings", JOptionPane.PLAIN_MESSAGE);
		
		host = hostBox.getText();
		prop.setProperty("host", host);
		username = userBox.getText();
		prop.setProperty("username", username);
		
		portString = portBox.getText();
		Scanner sc = new Scanner(portString);
		int port = sc.nextInt();
		sc.close();
		prop.setProperty("port", portString);
		
		writeConfFile();
		
		setInstance(new ChatClient(host, port, username));
	}
	
	static void writeConfFile() {
		try {
			if (!confFile.exists())
				confFile.createNewFile();
			prop.store(new FileOutputStream(confFile), "Configuration for chat client");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileSendWindow getFileWindow() {
		return fileWindow;
	}

	public ChatWindow getChatWindow() {
		return chatWindow;
	}

	public static ChatClient getInstance() {
		return instance;
	}

	private static void setInstance(ChatClient instance) {
		ChatClient.instance = instance;
	}

	public void exit() {
		workerGroup.shutdownGracefully();
		chatWindow.dispose();
		fileWindow.dispose();
	}

	public Channel getChannel() {
		return c;
	}
}
