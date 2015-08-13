package client.filesend;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import client.ChatClient;

import common.FileSendPacket;
import common.FileSendPacket.TransferAction;

public class Upload {
	Socket so;
	ObjectOutputStream out;
	BufferedInputStream fileIn;
	File file;
	
	public Upload(File file, String destUsername) throws UnknownHostException, IOException {
		this.file = file;
		
		so = ChatClient.so;
		out = new ObjectOutputStream(so.getOutputStream());
		fileIn = new BufferedInputStream(new FileInputStream(file));
		
		send(new FileSendPacket(file.getName(), (int)file.length(), ChatClient.username, destUsername, TransferAction.INIT));
	}
	
	public void send(FileSendPacket pack) throws IOException {
		out.writeObject(pack);
		out.flush();
	}
	
	public void doUpload() {
		try {
			byte[] allBytes = new byte[(int) file.length()];
			fileIn.read(allBytes, 0, allBytes.length);
			
			so.getOutputStream().write(allBytes);
			
			fileIn.close();
			JOptionPane.showMessageDialog(null, "Transferred file " + file.getName());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
