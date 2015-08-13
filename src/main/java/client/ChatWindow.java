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

@SuppressWarnings("serial")
public class ChatWindow extends JFrame implements ActionListener, KeyListener {
	
	ArrayList<String> lastMess = new ArrayList<String>();
	int messIndex = 0;
	
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
	
	public ChatWindow() {
		
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
		setTitle("Chat on " + ChatClient.host + " | Username: " + ChatClient.username);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	public void updateList(String[] users) {
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (String s: users)
			model.addElement(s);
		userList.setModel(model);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(reconnect))
			ChatClient.connect();
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
				ChatClient.send(text);
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
