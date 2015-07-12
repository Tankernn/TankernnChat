package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import common.InfoPacket;
import common.MessagePacket;
import common.MessagePacket.MessageType;

@SuppressWarnings("serial")
public class ChatWindow extends JFrame implements ActionListener, Runnable, KeyListener {
	Thread getMessages;
	static File confFile = new File("client.properties");
	
	String adress, username;
	ArrayList<String> lastMess = new ArrayList<String>();
	int port, messIndex = 0;
	
	Socket so = new Socket();
	ObjectInputStream objIn;
	PrintWriter out;
	
	GridBagLayout g = new GridBagLayout();
	GridBagConstraints con = new GridBagConstraints();
	
	JPanel right = new JPanel();
	JLabel infoLabel = new JLabel("Users online:");
	DefaultListModel<String> model = new DefaultListModel<String>();
	JList<String> userList = new JList<String>(model);
	JButton reconnect = new JButton("Reconnect");
	
	Console chat = new Console();
	JScrollPane scroll = new JScrollPane(chat);
	JTextField write = new JTextField();
	
	public ChatWindow(String adress, int port, String username) {
		this.adress = adress;
		this.port = port;
		this.username = username;
		
		//List config
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.setLayoutOrientation(JList.VERTICAL);
		//Label config
		infoLabel.setHorizontalAlignment(JLabel.CENTER);
		infoLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		//Layout config
		right.setLayout(g);
		con.fill = GridBagConstraints.HORIZONTAL;
		con.weightx = 1;
		con.gridx = 0;
		
		right.add(infoLabel, con);
		
		con.weighty = 1;
		con.fill = GridBagConstraints.BOTH;
		right.add(userList, con);
		
		con.weighty = 0;
		con.fill = GridBagConstraints.HORIZONTAL;
		right.add(reconnect, con);
		
		setLayout(new BorderLayout());
		add(chat, BorderLayout.NORTH);
		add(write, BorderLayout.SOUTH);
		add(right, BorderLayout.EAST);
		
		//Scrollbar config
		add(scroll);
		scroll.setMinimumSize(new Dimension(100, 100));
		scroll.setViewportView(chat);
		scroll.setSize(500, 130);
		
		//Listener config
		reconnect.addActionListener(this);
		write.addKeyListener(this);
		
		//Window config
		this.setLocation(new Point(100, 100));
		setSize(600, 600);
		setVisible(true);
		setTitle("Chat on " + adress + " | Username: " + username);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		connect(adress, port, username);
	}
	
	public void send(String text) {
		if (so.isConnected() && !so.isClosed())
			out.println(text);
		else {
			chat.log(new MessagePacket("Not connected to server!", MessageType.WARNING));
			write.setEnabled(false);
		}
	}
	
	void connect(String address, int port, String username) {
		chat.log(new MessagePacket("Connecting to " + address + " on port " + port + ".", MessageType.INFO));
		if (getMessages != null)
			getMessages.interrupt();
		
		try {
			so.close();
			objIn.close();
			out.close();
		} catch (NullPointerException ex) {
			//Nothing
		} catch (IOException ex) {
			chat.log(new MessagePacket(ex.toString(), MessageType.ERROR));
		}
		
		try {
			so = new Socket();
			so.connect(new InetSocketAddress(address, port), 1000);
			objIn = new ObjectInputStream(so.getInputStream());
			out = new PrintWriter(so.getOutputStream(), true);
		} catch (SocketTimeoutException ex) {
			chat.log(new MessagePacket("Could not connect to server. (Connection timed out!)", MessageType.ERROR));
			return;
		} catch (IOException e) {
			chat.log(new MessagePacket(e.toString(), MessageType.ERROR));
			return;
		}
		
		send(username); //First packet sent to server sets username
		
		getMessages = new Thread(this);
		getMessages.start();
		
		write.setEnabled(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(reconnect))
			connect(adress, port, username);
	}
	
	@Override
	public void run() {
		try {
			getMessages();
		} catch (EOFException eof) {
			chat.log(new MessagePacket(eof.toString() + " Disconnected from host.", MessageType.ERROR));
		} catch (ClassNotFoundException cnf) {
			chat.log(new MessagePacket("The message recieved from the server could not be understood. Are you using the right version?", MessageType.ERROR));
		} catch (IOException e) {
			chat.log(new MessagePacket(e.toString(), MessageType.ERROR));
		}
	}
	
	public void getMessages() throws IOException, ClassNotFoundException {
		while (!getMessages.isInterrupted()) {
			Object fromServer = objIn.readObject();
			if (fromServer instanceof MessagePacket) {
				MessagePacket mess = ((MessagePacket) fromServer);
				chat.log(mess);
			} else if (fromServer instanceof InfoPacket) {
				InfoPacket info = (InfoPacket) fromServer;
				
				infoLabel.setText("<html>" + info.toString().replace("\n", "<br>"));
				
				model = new DefaultListModel<String>();
				for (String user: info.usersOnline)
					model.addElement(user);
				
				userList.setModel(model);
			}
			else if (fromServer instanceof String) {
				chat.log(new MessagePacket((String) fromServer, MessageType.NORMAL));
			} else
				throw new ClassNotFoundException();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent eKey) {
		int keyCode = eKey.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_UP:
			if (messIndex > 0)
				messIndex--;
			if (!lastMess.isEmpty())
				write.setText(lastMess.get(messIndex));
			break;
		case KeyEvent.VK_DOWN:
			if (messIndex <= lastMess.size())
				messIndex++;
			if (messIndex >= lastMess.size()) {
				messIndex = lastMess.size();
				write.setText("");
			} else
				write.setText(lastMess.get(messIndex));
			break;
		case KeyEvent.VK_ENTER:
			String text = write.getText().trim();
			if (!text.equals("")) {
				send(text);
				lastMess.add(text);
				messIndex = lastMess.size();
				write.setText("");
			}
			break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {}
	
	@Override
	public void keyTyped(KeyEvent arg0) {}
}
