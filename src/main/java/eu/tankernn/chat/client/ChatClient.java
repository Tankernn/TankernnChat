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

public class ChatClient {
	static Properties prop = new Properties();
	static File confFile = new File("client.properties");
	private static FileSendWindow fileWindow;
	
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
		
		new ChatWindow(host, port, username);
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

	public static FileSendWindow getFileWindow() {
		return fileWindow;
	}
}
