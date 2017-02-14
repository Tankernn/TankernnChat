package eu.tankernn.chat.client.filesend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import eu.tankernn.chat.packets.Packet;
import eu.tankernn.chat.packets.filesend.FileSendDataPacket;
import eu.tankernn.chat.packets.filesend.FileSendInfoPacket;
import eu.tankernn.chat.packets.filesend.FileSendStatusPacket;
import io.netty.channel.Channel;

public class Upload implements Runnable {
	
	private Channel c;
	private File file;
	
	public Upload(Channel c, File file, String destUsername) throws IOException {
		this.c = c;
		this.file = file;
		
		send(new FileSendInfoPacket(file.getName(), (int) file.length(),
				destUsername));
	}
	
	public void send(Packet pack) throws IOException {
		try {
			c.writeAndFlush(pack).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			FileInputStream fileIn = new FileInputStream(file);
			while (fileIn.available() > 0 && !Thread.interrupted()) {
				byte[] bytes = new byte[(int) Math
						.min(FileSendDataPacket.BUFFER_SIZE, fileIn.available())];
				fileIn.read(bytes);
				send(new FileSendDataPacket(bytes));
			}
			fileIn.close();
			send(FileSendStatusPacket.FINISHED);
			JOptionPane.showMessageDialog(null,
					"Transferred file " + file.getName());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
