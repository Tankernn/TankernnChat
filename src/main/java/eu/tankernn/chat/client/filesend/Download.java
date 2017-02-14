package eu.tankernn.chat.client.filesend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ProgressMonitor;

import eu.tankernn.chat.client.ChatClient;
import eu.tankernn.chat.packets.filesend.FileSendDataPacket;
import eu.tankernn.chat.packets.filesend.FileSendInfoPacket;
import eu.tankernn.chat.packets.filesend.FileSendStatusPacket;

public class Download implements Runnable {
	private ChatClient c;
	private String fileName;
	private String dest;
	private int remoteSize, received;
	private FileOutputStream fOut;
	
	private ProgressMonitor monitor;
	
	public Download(ChatClient c, String destinationPath, FileSendInfoPacket pack) {
		this.dest = destinationPath + pack.filename;
		this.c = c;
		
		fileName = pack.filename;
		remoteSize = pack.fileSize;
	}
	
	public void run() {
		c.send(FileSendStatusPacket.ACCEPT);
		
		File file = new File(dest);
		
		try {
			file.createNewFile();
			fOut = new FileOutputStream(file);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		monitor = new ProgressMonitor(null, "Downloading " + fileName + "...",
				"0%", 0, 100);
		monitor.setProgress(0);
	}
	
	private void updateProgress() {
		int prog = (int) ((received / remoteSize) * 100);
		if (prog > 100) {
			prog = 100;
		} else if (prog < 0) {
			prog = 0;
		}
		
		monitor.setProgress(prog);
		monitor.setNote(prog + "%");
	}
	
	public void handlePacket(FileSendDataPacket pack) {
		byte[] bytes = pack.data;
		try {
			fOut.write(bytes);
			received += bytes.length;
			updateProgress();
			if (received >= remoteSize) {
				if (received > remoteSize)
					System.err.println("Recieved more bytes than advertised.");
				finish();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void finish() {
		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		monitor.setNote("File transfer complete. File resides in " + dest);
		c.getFileWindow().removeDownload(this);
		c.getFileWindow().updateList();
	}
	
	@Override
	public String toString() {
		return fileName;
	}
}
