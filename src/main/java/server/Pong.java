package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Pong implements ActionListener {
	
	Timer tim = new Timer(100, this);
	
	Client player1; Client player2;
	
	public Pong (Client p1, Client p2) {
		player1 = p1;
		player2 = p2;
		
		// TODO Initialize game here
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Update game logic here
	}
}
