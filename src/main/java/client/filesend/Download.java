package client.filesend;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import client.ChatClient;

import common.FileSendPacket;

public class Download extends SwingWorker<Boolean, Void> {
	Socket sock;
	BufferedReader in;
	PrintWriter out;
	String fileName;
	String dest;
	int remoteSize;
	
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
	
	public Download(FileSendPacket pack) throws IOException {
		sock = ChatClient.so;
		this.dest = ChatClient.fileWindow.downloadDest.getText() + pack.filename;
		
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new PrintWriter(sock.getOutputStream());
		
		fileName = pack.filename;
		remoteSize = pack.fileSize;
	}

	public boolean doDownload() throws IOException {
		out.println("START");
		out.flush();
		
		byte[] buffer = new byte[24000];
		int readLen;
		int currentSize = 0;
		
		File file = new File(ChatClient.fileWindow.downloadDest.getText() + fileName);
		file.createNewFile();
		BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(file));
		
		byte[] mybytearray = new byte[(int) remoteSize];
		
		readLen = sock.getInputStream().read(mybytearray, 0, mybytearray.length);
		currentSize = readLen;
		
		monitor = new ProgressMonitor(null, mybytearray, dest, 0, 100);
		setProgress(0);
		setStatus("Downloading " + fileName + "...");
		
		while ((readLen = sock.getInputStream().read(buffer, 0, buffer.length)) != -1) {
			fOut.write(buffer, 0, readLen);
			currentSize += readLen;
			
			int prog = (int) ((currentSize / remoteSize) * 100);
			if (prog > 100) {
				prog = 100;
			}
			if (prog < 0) {
				prog = 0;
			}
			
			setProgress(prog);
			
			prog = (progressIndex * 100) + prog;
			
			setReady(prog);
			
			monitor.setNote(status);
		}
		
		fOut.flush();
		fOut.close();
		
		monitor.setNote("File transfer complete. File resides in " + file.getAbsolutePath());
		
		sock.close();
		ChatClient.fileWindow.downloads.remove(this);
		ChatClient.fileWindow.updateList();
		
		return true;
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
			ChatClient.fileWindow.downloads.remove(this);
			ChatClient.fileWindow.updateList();
		}
		setStatus(success ? "Success" : "Downloads failed");
		return success;
	}
}
