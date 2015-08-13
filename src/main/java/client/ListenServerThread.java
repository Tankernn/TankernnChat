package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

import client.filesend.Download;

import common.FileSendPacket;
import common.InfoPacket;
import common.MessagePacket;
import common.MessagePacket.MessageType;

class ListenServerThread extends Thread {
	
	private ObjectInputStream objIn;

	public ListenServerThread(ObjectInputStream objIn) {
		this.objIn = objIn;
	}
	
	@Override
	public void run() {
		try {
			getMessages();
		} catch (EOFException eof) {
			ChatClient.print(new MessagePacket(eof.toString() + " Disconnected from host.", MessageType.ERROR));
		} catch (ClassNotFoundException cnf) {
			ChatClient.print(new MessagePacket("The message recieved from the server could not be understood. Are you using the right version?", MessageType.ERROR));
		} catch (IOException e) {
			ChatClient.print(new MessagePacket(e.toString(), MessageType.ERROR));
		}
	}
	
	public void getMessages() throws IOException, ClassNotFoundException {
		while (!this.isInterrupted()) {
			Object fromServer = objIn.readObject();
			if (fromServer instanceof MessagePacket) {
				MessagePacket mess = ((MessagePacket) fromServer);
				ChatClient.print(mess);
			} else if (fromServer instanceof InfoPacket) {
				InfoPacket info = (InfoPacket) fromServer;
				
				ChatClient.chatWindow.infoLabel.setText("<html>" + info.toString().replace("\n", "<br>"));
				
				ChatClient.chatWindow.updateList(info.usersOnline);
				ChatClient.fileWindow.updateComboBox(info.usersOnline);
				
			} else if (fromServer instanceof FileSendPacket) {
				ChatClient.fileWindow.setVisible(true);
				
				ChatClient.fileWindow.addDownload(new Download((FileSendPacket) fromServer));
			} else if (fromServer instanceof String) {
				ChatClient.print(new MessagePacket((String) fromServer, MessageType.NORMAL));
			} else
				throw new ClassNotFoundException();
		}
	}
}