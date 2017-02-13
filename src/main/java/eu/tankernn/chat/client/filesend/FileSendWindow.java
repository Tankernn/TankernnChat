package eu.tankernn.chat.client.filesend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import eu.tankernn.chat.client.ChatClient;

public class FileSendWindow extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ArrayList<Download> downloads = new ArrayList<Download>();
	JList<Download> dlList = new JList<Download>();
	
	final JFileChooser fc = new JFileChooser();
	
	JPanel left = new JPanel();
	JPanel right = new JPanel();
	JButton buttonSend = new JButton("Send file...");
	JButton downloadFile = new JButton("Download selected file");
	JComboBox<String> destUsername = new JComboBox<String>();
	public JTextField downloadDest = new JTextField(System.getProperty("user.home") + "/Downloads/");
	
	GridBagLayout g = new GridBagLayout();
	GridBagConstraints con = new GridBagConstraints();
	
	public FileSendWindow() {
		left.setBorder(new TitledBorder("Send file"));
		destUsername.setBorder(new TitledBorder("Destination username: "));
		buttonSend.addActionListener(this);
		
		right.setLayout(new GridLayout(3, 1)); right.setBorder(new TitledBorder("Download file"));
		downloadDest.setBorder(new TitledBorder("Download destination directory:"));
		dlList.setPreferredSize(new Dimension(200, 200));
		downloadFile.addActionListener(this);
		
		
		//Layout config
		//Left
		left.setLayout(g);
		right.setLayout(g);
		con.fill = GridBagConstraints.HORIZONTAL;
		con.weightx = 1;
		con.gridx = 0;
		
		left.add(destUsername, con);
		left.add(buttonSend, con);
		
		//Right
		right.add(downloadDest, con);
		
		con.weighty = 1;
		con.fill = GridBagConstraints.BOTH;
		right.add(dlList, con);

		con.weighty = 0;
		con.fill = GridBagConstraints.HORIZONTAL;
		right.add(downloadFile, con);
		
		setLayout(new BorderLayout());
		add(left, BorderLayout.WEST);
		add(right, BorderLayout.EAST); 
		
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
	
	public void updateList() {
		DefaultListModel<Download> model = new DefaultListModel<Download>();
		for (Download d: downloads)
			model.addElement(d);
		dlList.setModel(model);
	}
	
	public void updateComboBox(String[] users) {
		destUsername.removeAllItems();
		for (String s: users)
			destUsername.addItem(s);
	}
	
	public void addDownload(Download d) {
		downloads.add(d);
		updateList();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(buttonSend)) {
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
//				try {
//					// FIXME Move stuff from ChatWindow to ChatClient
//					// new Upload(ChatClient, file, (String) destUsername.getSelectedItem());
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
			}
		} else if (e.getSource().equals(downloadFile)) {
			if (!dlList.isSelectionEmpty()) {
				dlList.getSelectedValue().execute();
			}
		}
	}
	
	public static void main(String[] args) {
		new FileSendWindow();
	}
}
