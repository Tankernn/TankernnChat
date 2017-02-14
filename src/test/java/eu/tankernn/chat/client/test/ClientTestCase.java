package eu.tankernn.chat.client.test;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.tankernn.chat.client.ChatClient;

public class ClientTestCase {
	private ChatClient user1;
	private ChatClient user2;

	@Before
	public void setUp() {
		user1 = new ChatClient("localhost", 25566, "user1");
		user2 = new ChatClient("localhost", 25566, "user2");
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
		user1.exit();
		user2.exit();
	}

}
