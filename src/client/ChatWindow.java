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
import java.net.Socket;
import java.net.SocketException;

import javax.swing.*;

import common.Message;

@SuppressWarnings("serial")
public class ChatWindow extends JFrame implements ActionListener, Runnable{
	Thread getMessages;
	static File confFile = new File("client.properties");
	
	String adress;
	int port;
	String username;
	
	Socket so;
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
	
	public ChatWindow(String adress, int port, String username) throws IOException {
		this.adress = adress;
		this.port = port;
		this.username = username;
		
		connect(adress, port, username);
		
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.setLayoutOrientation(JList.VERTICAL);
		
		right.setLayout(g);
		
		con.weighty = 1;
		con.gridy = 0;
		con.gridheight = 1;
		g.setConstraints(lblUsersOnline, con);
		right.add(lblUsersOnline);
		
		con.fill = GridBagConstraints.BOTH;
		con.weighty = 20;
		con.gridy = 1; con.gridx = 0;
		g.setConstraints(userList, con);
		right.add(userList);
		
		con.weighty = 1;
		con.gridy = 21;
		con.gridheight = 1;
		g.setConstraints(reconnect, con);
		right.add(reconnect);
		
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
	}
	
	void send(String text) {
		out.println(text);
	}
	
	void connect(String adress, int port, String username) throws IOException {
		if (getMessages != null)
			getMessages.interrupt();
		
		if (so != null) {
			so.close();
			objIn.close();
			out.close();
		}
		
		so =		new Socket(adress, port);
		objIn =		new ObjectInputStream(so.getInputStream());
		out =		new PrintWriter(so.getOutputStream(), true);
		
		chat.log("Connected to " + adress + " on port " + port + ".");
		
		send(username);
		
		getMessages = new Thread(this);
		getMessages.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == skriv) {
			send(skriv.getText());
			skriv.setText("");
		} else if (e.getSource() == reconnect) {
			try {
				connect(adress, port, username);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
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
	
	public void getMessages() throws SocketException, IOException, ClassNotFoundException {
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
