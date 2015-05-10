package client;

import java.awt.Color;

import javax.swing.*;
import javax.swing.text.*;

import common.Message;

@SuppressWarnings("serial")
public class Console extends JTextPane implements Runnable {
	
	String str;
	SimpleAttributeSet style;
	
	void log(String str) {
		this.str = str;
		style = new SimpleAttributeSet();
		StyleConstants.setForeground(style, Color.RED);
		StyleConstants.setBold(style, true);
		
		SwingUtilities.invokeLater(this);
	}
	
	void log(Message mess) {
		this.str = mess.toString();
		this.style = mess.style;
		
		SwingUtilities.invokeLater(this);
	}
	
	@Override
	public void run() {
		StyledDocument doc = this.getStyledDocument();
		
		try
		{
		    doc.insertString(doc.getLength(), str + "\n", style);
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}
