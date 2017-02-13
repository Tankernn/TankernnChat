package eu.tankernn.chat.client.filesend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import eu.tankernn.chat.common.FileSendPacket;
import eu.tankernn.chat.common.FileSendPacket.TransferAction;
import io.netty.channel.Channel;

public class Upload implements Runnable {
	
	private Channel c;
	private File file;
	
	public Upload(Channel c, File file, String destUsername) throws UnknownHostException, IOException {
		this.c = c;
		this.file = file;
		
		send(new FileSendPacket(file.getName(), (int)file.length(), destUsername, TransferAction.INIT));
	}
	
	public void send(FileSendPacket pack) throws IOException {
		c.writeAndFlush(pack);
	}
	
	@Override
	public void run() {
		long bytesLeft = file.length();
		byte[] bytes = new byte[FileSendPacket.BUFFER_SIZE];
		try {
			FileInputStream fileIn = new FileInputStream(file);
			while(fileIn.available() > 0) {
				bytesLeft -= fileIn.read(bytes, 0, (int) Math.min(bytes.length, bytesLeft));
				send(new FileSendPacket(bytes));
			}
			fileIn.close();
			JOptionPane.showMessageDialog(null, "Transferred file " + file.getName());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
