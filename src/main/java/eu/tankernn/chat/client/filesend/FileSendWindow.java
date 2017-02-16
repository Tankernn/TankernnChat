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
import eu.tankernn.chat.packets.filesend.FileSendInfoPacket;

public class FileSendWindow extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Thread uploadThread;
	private Upload upload;
	private Download download;
	private ArrayList<Download> downloads = new ArrayList<Download>();
	private JList<Download> dlList = new JList<Download>();
	
	private final JFileChooser fc = new JFileChooser();
	
	private JPanel left = new JPanel(), right = new JPanel();
	private JButton buttonSend = new JButton("Send file..."),
			downloadFile = new JButton("Download selected file");
	private JComboBox<String> destUsername = new JComboBox<String>();
	private JTextField downloadDest = new JTextField(
			System.getProperty("user.home") + "/Downloads/");
	
	private GridBagLayout g = new GridBagLayout();
	private GridBagConstraints con = new GridBagConstraints();
	
	private ChatClient client;
	
	public FileSendWindow(ChatClient client) {
		this.client = client;
		left.setBorder(new TitledBorder("Send file"));
		destUsername.setBorder(new TitledBorder("Destination username: "));
		buttonSend.addActionListener(this);
		
		right.setLayout(new GridLayout(3, 1));
		right.setBorder(new TitledBorder("Download file"));
		downloadDest
				.setBorder(new TitledBorder("Download destination directory:"));
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
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
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
				
				try {
					upload = new Upload(client.getChannel(), file,
							(String) destUsername.getSelectedItem());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else if (e.getSource().equals(downloadFile)) {
			if (!dlList.isSelectionEmpty()) {
				download = dlList.getSelectedValue();
				download.run();
			}
		}
	}
	
	public void addDownload(FileSendInfoPacket pack) {
		addDownload(new Download(client, downloadDest.getText(), pack));
	}
	
	public void removeDownload(Download download) {
		downloads.remove(download);
		updateList();
	}
	
	public void cancelUpload() {
		if (uploadThread != null)
			uploadThread.interrupt();
		upload = null;
	}
	
	public void startUpload() {
		uploadThread = new Thread(upload);
		uploadThread.run();
	}
	
	public Download getDownload() {
		return download;
	}
}
