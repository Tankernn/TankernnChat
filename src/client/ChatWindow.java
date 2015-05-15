package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import common.Message;

@SuppressWarnings("serial")
public class ChatWindow extends JFrame implements ActionListener, Runnable{
	Thread getMessages;
	static File confFile = new File("client.properties");
	
	String adress;
	int port;
	String username;
	
	Socket so = new Socket();
	ObjectInputStream objIn;
	PrintWriter out;
	
	GridBagLayout g = new GridBagLayout();
	GridBagConstraints con = new GridBagConstraints();
	
	JPanel right = new JPanel();
	JLabel lblUsersOnline = new JLabel("Users online:");
	DefaultListModel<String> model = new DefaultListModel<String>();
	JList<String> userList = new JList<String>(model);
	JButton reconnect = new JButton("Reconnect");
	
	Console chat = new Console();
	JScrollPane scroll = new JScrollPane(chat);
	JTextField skriv = new JTextField();
	
	public ChatWindow(String adress, int port, String username) {
		this.adress = adress;
		this.port = port;
		this.username = username;
		
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.setLayoutOrientation(JList.VERTICAL);
		
		lblUsersOnline.setHorizontalAlignment(JLabel.CENTER);
		lblUsersOnline.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		right.setLayout(g);
		con.fill = GridBagConstraints.HORIZONTAL;
		con.weightx = 1;
		con.gridx = 0;
		
		right.add(lblUsersOnline, con);

		con.weighty = 1;
		con.fill = GridBagConstraints.BOTH;
		right.add(userList, con);

		con.weighty = 0;
		con.fill = GridBagConstraints.HORIZONTAL;
		right.add(reconnect, con);
		
		reconnect.addActionListener(this);
		
		setLayout(new BorderLayout());
		add(chat, BorderLayout.NORTH); add(skriv, BorderLayout.SOUTH); add(right, BorderLayout.EAST);
		
		add(scroll);
		scroll.setMinimumSize(new Dimension(100, 100));
		scroll.setViewportView(chat);
		scroll.setSize(500, 130);
		
		skriv.addActionListener(this);
		chat.setSize(500, 450);
		chat.setEditable(false);
		
		setSize(500, 500);
		setVisible(true);
		setTitle("Chat | Username: " + username);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		connect(adress, port, username);
	}
	
	void send(String text) {
		if (so.isConnected() && !so.isClosed())
			out.println(text);
		else {
			chat.log("Not connected to server!");
			skriv.setEnabled(false);
		}
	}
	
	void connect(String address, int port, String username) {
		chat.log("Connecting to " + address + " on port " + port + ".");
		if (getMessages != null)
			getMessages.interrupt();
		
		try {
			so.close();
			objIn.close();
			out.close();
		} catch (NullPointerException ex) {
			//Nothing
		} catch (IOException ex) {
			chat.log(ex.toString());
		}
		
		try {
			so = new Socket();
			so.connect(new InetSocketAddress(address, port), 1000);
			objIn =		new ObjectInputStream(so.getInputStream());
			out =		new PrintWriter(so.getOutputStream(), true);
		} catch (SocketTimeoutException ex) {
			chat.log("Could not connect to server. (Connection timed out!)");
			return;
		} catch (IOException e) {
			chat.log(e.toString());
			return;
		}
		
		send(username); //First packet sent to server sets username
		
		getMessages = new Thread(this);
		getMessages.start();
		
		skriv.setEnabled(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == skriv) {
			send(skriv.getText());
			skriv.setText("");
		} else if (e.getSource() == reconnect) {
			connect(adress, port, username);
		}
	}

	@Override
	public void run() {
		try {
			getMessages();
		} catch (EOFException eof) {
			chat.log(eof.toString() + " Disconnected from host.");
		} catch (ClassNotFoundException cnf) {
			chat.log("The message recieved from the server could not be understood. Are you using the right version?");
		} catch (IOException e) {
			chat.log(e.toString());
		}
	}
	
	public void getMessages() throws IOException, ClassNotFoundException {
		while(!getMessages.isInterrupted()) {
			Object fromServer = objIn.readObject();
			if (fromServer instanceof Message) {
				Message mess = ((Message)fromServer);
				chat.log(mess);
				
				model = new DefaultListModel<String>();
				for (int i = 0; i < mess.usersOnline.length; i++)
					model.addElement(mess.usersOnline[i]);
				
				userList.setModel(model);
			} else
				chat.log(fromServer.toString());
		}
	}
}
