package eu.tankernn.chat.client.filesend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

		c.writeAndFlush(new FileSendInfoPacket(file.getName(), (int) file.length(), destUsername));
	}

	@Override
	public void run() {
		try {
			FileInputStream fileIn = new FileInputStream(file);
			while (fileIn.available() > 0 && !Thread.interrupted()) {
				byte[] bytes = new byte[(int) Math.min(FileSendDataPacket.BUFFER_SIZE, fileIn.available())];
				fileIn.read(bytes);
				c.write(new FileSendDataPacket(bytes));
			}
			fileIn.close();
			c.write(FileSendStatusPacket.FINISHED);
			c.flush();
			//JOptionPane.showMessageDialog(null, "Transferred file " + file.getName());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
