package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import client.filesend.FileSendWindow;

import common.MessagePacket;
import common.MessagePacket.MessageType;

public class ChatClient {
	static ClientProperties prop = new ClientProperties("client.properties");
	public static ChatWindow chatWindow;
	public static FileSendWindow fileWindow = new FileSendWindow();
	
	static Thread getMessages;
	
	public static String host, username;
	public static int port;
	
	public static Socket so = new Socket();
	static ObjectInputStream objIn;
	static PrintWriter out;
	
	public static void main(String[] arg) {
		prop.loadProperties();
		
		JTextField hostBox = new JTextField(prop.getProperty("host"));
		JTextField portBox = new JTextField(prop.getProperty("port"));
		JTextField userBox = new JTextField(prop.getProperty("username"));
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Host:"), hostBox,
				new JLabel("Port:"), portBox,
				new JLabel("Username:"), userBox
		};
		
		String portString;
		
		JOptionPane.showMessageDialog(null, inputs, "Chat settings", JOptionPane.PLAIN_MESSAGE);
		
		host = hostBox.getText();
		prop.setProperty("host", host);
		username = userBox.getText();
		prop.setProperty("username", username);
		
		portString = portBox.getText();
		Scanner sc = new Scanner(portString);
		port = sc.nextInt();
		sc.close();
		prop.setProperty("port", portString);
		
		prop.store();
		
		chatWindow = new ChatWindow();
		connect();
	}
	
	static void print(String text) {
		chatWindow.chat.print(text);
	}
	
	static void print(MessagePacket text) {
		chatWindow.chat.print(text);
	}
	
	public static void send(String text) {
		switch (text.toLowerCase()) {
		case "/filesend":
			fileWindow.setVisible(true);
			break;
		case "/disconnect":
			disconnect();
			break;
		case "/exit":
			System.exit(0);
			break;
		case "/help":
			print("disconnect: Disconnects you from the server.");
			print("exit: Exits the client");
		default:
			if (so.isConnected() && !so.isClosed())
				out.println(text);
			else {
				print(new MessagePacket("Not connected to server!", MessageType.WARNING));
				chatWindow.write.setEnabled(false);
			}
		}
	}
	
	static void connect() {
		disconnect();
		
		print(new MessagePacket("Connecting to " + host + " on port " + port + ".", MessageType.INFO));
		
		try {
			so = new Socket();
			so.connect(new InetSocketAddress(host, port), 1000);
			objIn = new ObjectInputStream(so.getInputStream());
			out = new PrintWriter(so.getOutputStream(), true);
		} catch (SocketTimeoutException ex) {
			print(new MessagePacket("Could not connect to server. (Connection timed out!)", MessageType.ERROR));
			return;
		} catch (IOException e) {
			print(new MessagePacket(e.toString(), MessageType.ERROR));
			return;
		}
		
		send(username); //First packet sent to server sets username
		
		getMessages = new ListenServerThread(objIn);
		getMessages.start();
		
		chatWindow.write.setEnabled(true);
	}
	
	static void disconnect() {
		print("Disconnecting...");
		
		if (getMessages != null)
			getMessages.interrupt();
		
		try {
			so.close();
			objIn.close();
			out.close();
		} catch (NullPointerException ex) {
			//Nothing
		} catch (IOException ex) {
			print(new MessagePacket(ex.toString(), MessageType.ERROR));
		}
	}
	
}
