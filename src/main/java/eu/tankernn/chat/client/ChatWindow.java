package eu.tankernn.chat.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import eu.tankernn.chat.packets.InfoPacket;
import eu.tankernn.chat.packets.MessagePacket;

@SuppressWarnings("serial")
public class ChatWindow extends JFrame implements ActionListener, KeyListener, WindowListener {
	private ChatClient client;
	
	private ArrayList<String> lastMess = new ArrayList<String>();
	private int messIndex = 0;
	
	private GridBagLayout g = new GridBagLayout();
	private GridBagConstraints con = new GridBagConstraints();
	
	private JPanel right = new JPanel();
	private JLabel infoLabel = new JLabel("Users online:");
	private DefaultListModel<String> model = new DefaultListModel<String>();
	private JList<String> userList = new JList<String>(model);
	private JButton reconnect = new JButton("Reconnect");
	
	private Console chat = new Console();
	private JScrollPane scroll = new JScrollPane(chat);
	private JTextField write = new JTextField();
	
	public ChatWindow(ChatClient client) {
		this.client = client;
		
		// List config
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.setLayoutOrientation(JList.VERTICAL);
		// Label config
		infoLabel.setHorizontalAlignment(JLabel.CENTER);
		infoLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Layout config
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
		
		// Scrollbar config
		add(scroll);
		scroll.setMinimumSize(new Dimension(100, 100));
		scroll.setViewportView(chat);
		scroll.setSize(500, 130);
		
		// Listener config
		reconnect.addActionListener(this);
		write.addKeyListener(this);
		
		// Window config
		this.setLocation(new Point(100, 100));
		setSize(600, 600);
		setTitle(
				"Chat on " + client.address + " | Username: " + client.username);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(reconnect))
			client.connect();
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
				client.send(text);
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
	
	@Override
	public void windowOpened(WindowEvent e) {}
	
	@Override
	public void windowClosing(WindowEvent e) {
		client.exit();
	}
	
	@Override
	public void windowClosed(WindowEvent e) {}
	
	@Override
	public void windowIconified(WindowEvent e) {}
	
	@Override
	public void windowDeiconified(WindowEvent e) {}
	
	@Override
	public void windowActivated(WindowEvent e) {}
	
	@Override
	public void windowDeactivated(WindowEvent e) {}
	
	public Console getChat() {
		return chat;
	}
	
	public void log(MessagePacket messagePacket) {
		chat.log(messagePacket);
		JScrollBar s = scroll.getVerticalScrollBar();
		s.setValue(s.getMaximum());
	}
	
	public void setInfo(InfoPacket info) {
		infoLabel.setText("<html>" + info.toString().replace("\n", "<br>"));
		
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (String user: info.usersOnline)
			model.addElement(user);
		
		userList.setModel(model);
	}
}
