package eu.tankernn.chat.client.filesend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ProgressMonitor;

import eu.tankernn.chat.client.ChatClient;
import eu.tankernn.chat.packets.filesend.FileSendDataPacket;
import eu.tankernn.chat.packets.filesend.FileSendInfoPacket;
import eu.tankernn.chat.packets.filesend.FileSendStatusPacket;
import eu.tankernn.chat.packets.filesend.FileSendStatusPacket.TransferAction;
import io.netty.channel.Channel;

public class Download {
	private Channel c;
	private String fileName;
	private String dest;
	private int remoteSize, offset;
	private FileOutputStream fOut;
	
	private ProgressMonitor monitor;
	
	public Download(Channel c, String destinationPath, FileSendInfoPacket pack) throws IOException {
		this.dest = destinationPath + pack.filename;
		this.c = c;
		
		fileName = pack.filename;
		remoteSize = pack.fileSize;
	}
	
	public void startDownload() {
		c.writeAndFlush(new FileSendStatusPacket(TransferAction.ACCEPT));
		
		File file = new File(dest);
		
		try {
			file.createNewFile();
			fOut = new FileOutputStream(file);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		monitor = new ProgressMonitor(null, "Downloading " + fileName + "...", "0%", 0, 100);
		monitor.setProgress(0);
		
		while (offset < remoteSize) {
			int prog = (int) ((offset / remoteSize) * 100);
			if (prog > 100) {
				prog = 100;
			} else if (prog < 0) {
				prog = 0;
			}
			
			monitor.setProgress(prog);
			monitor.setNote(prog + "%");
		}
	}
	
	public void handlePacket(FileSendDataPacket pack) {
		byte[] bytes = pack.data;
		try {
			fOut.write(bytes);
			offset += bytes.length;
			if (offset >= remoteSize) {
				finish();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void finish() throws IOException {
		fOut.flush();
		fOut.close();
		monitor.setNote(
				"File transfer complete. File resides in " + dest);
		ChatClient.getFileWindow().downloads.remove(this);
		ChatClient.getFileWindow().updateList();
	}
	
	@Override
	public String toString() {
		return fileName;
	}
}
