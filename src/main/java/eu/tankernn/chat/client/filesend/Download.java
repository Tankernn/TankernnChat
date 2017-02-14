package eu.tankernn.chat.client.filesend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import eu.tankernn.chat.client.ChatClient;
import eu.tankernn.chat.packets.filesend.FileSendDataPacket;
import eu.tankernn.chat.packets.filesend.FileSendInfoPacket;
import io.netty.channel.Channel;

public class Download extends SwingWorker<Boolean, Void> {
	Channel c;
	String fileName;
	String dest;
	int remoteSize, offset;
	FileOutputStream fOut;
	
	private ProgressMonitor monitor;
	int ready;
	String status;
	private int progressIndex;
	
	public synchronized void setReady(int newReady) {
		int oldReady = ready;
		ready = newReady;
		firePropertyChange("ready", oldReady, ready);
	}
	
	public synchronized void setStatus(String newStatus) {
		String oldStatus = status;
		status = newStatus;
		firePropertyChange("note", oldStatus, status);
		monitor.setNote(status);
	}
	
	public Download(Channel c, String destinationPath, FileSendInfoPacket pack) throws IOException {
		this.dest = destinationPath + pack.filename;
		this.c = c;
		
		fileName = pack.filename;
		remoteSize = pack.fileSize;
	}
	
	public boolean doDownload() throws IOException {
		c.writeAndFlush("/start");
		
		File file = new File(dest);
		file.createNewFile();
		fOut = new FileOutputStream(file);
		
		monitor = new ProgressMonitor(null, null, dest, 0, 100);
		setProgress(0);
		setStatus("Downloading " + fileName + "...");
		
		int prog = (int) ((offset / remoteSize) * 100);
		if (prog > 100) {
			prog = 100;
		} else if (prog < 0) {
			prog = 0;
		}
		
		setProgress(prog);
		
		prog = (progressIndex * 100) + prog;
		
		setReady(prog);
		
		monitor.setNote(status);
		
		return true;
	}
	
	public void handlePacket(FileSendDataPacket pack) {
		byte[] bytes = pack.data;
		try {
			fOut.write(bytes);
			offset += bytes.length;
			if (offset >= remoteSize) {
				fOut.flush();
				fOut.close();
				monitor.setNote("File transfer complete. File resides in " + dest);
				ChatClient.getFileWindow().downloads.remove(this);
				ChatClient.getFileWindow().updateList();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public String toString() {
		return fileName;
	}
	
	@Override
	protected Boolean doInBackground() {
		boolean success = false;
		
		try {
			success = doDownload();
		} catch (IOException ex) {
			ex.printStackTrace();
			ChatClient.getFileWindow().downloads.remove(this);
			ChatClient.getFileWindow().updateList();
		}
		setStatus(success ? "Success" : "Downloads failed");
		return success;
	}
}
