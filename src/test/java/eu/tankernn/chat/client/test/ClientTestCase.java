package eu.tankernn.chat.client.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.tankernn.chat.client.ChatWindow;

public class ClientTestCase {
	private ChatWindow user1;
	private ChatWindow user2;

	@Before
	public void setUp() {
		user1 = new ChatWindow("localhost", 25566, "user1");
		user2 = new ChatWindow("localhost", 25566, "user2");
	}

	@Test
	public void testSend() {
		user1.send("Hello!");
		assertTrue(user1.isConnected());
	}

	@Test
	public void testPM() {
		user1.send("/pm user2 Hi there user2!");
	}

	@After
	public void cleanUp() {
		user1.dispose();
		user2.dispose();
	}

}
