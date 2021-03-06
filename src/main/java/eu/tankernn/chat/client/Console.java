package eu.tankernn.chat.client;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import eu.tankernn.chat.common.MessagePacket;

@SuppressWarnings("serial")
public class Console extends JTextPane {
	
	public Console() {
		setEditable(false);
	}
	
	void log(MessagePacket mess) {
		SwingUtilities.invokeLater(new AppendThread(mess.toString(), mess.style, this.getStyledDocument()));
	}
	
	private static class AppendThread extends Thread {
		String text;
		SimpleAttributeSet style;
		StyledDocument doc;
		
		public AppendThread(String text, SimpleAttributeSet style, StyledDocument doc) {
			this.text = text;
			this.style = style;
			this.doc = doc;
		}
		
		@Override
		public synchronized void run() {
			try {
				doc.insertString(doc.getLength(), text + "\n", style);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
}
