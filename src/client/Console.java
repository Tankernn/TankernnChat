package client;

import java.awt.Color;

import javax.swing.*;
import javax.swing.text.*;

import common.Message;

@SuppressWarnings("serial")
public class Console extends JTextPane {
	
	public Console() {
		setEditable(false);
	}
	
	void log(String str) {
		SimpleAttributeSet style = new SimpleAttributeSet();
		StyleConstants.setForeground(style, Color.RED);
		StyleConstants.setBold(style, true);
		
		SwingUtilities.invokeLater(new AppendThread(str, style, this.getStyledDocument()));
	}
	
	void log(Message mess) {
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
			} catch(Exception e) {
				System.out.println(e);
			}
		}
	}
}
