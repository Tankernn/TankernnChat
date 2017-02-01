package client;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import server.Server;

public class ClientTestCase {
	private ChatWindow user1;
	private ChatWindow user2;
	
	@BeforeClass
	public static void setUpClass() {
		Server.main(new String[] {});
	}
	
	@Before
	public void setUp() {
		user1 = new ChatWindow("localhost", 25566, "user1");
		user2 = new ChatWindow("localhost", 25566, "user2");
	}
	
	@Test
	public void testSend() {
		user1.send("Hello!");
		assertTrue(user1.so.isConnected());
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
	
	@AfterClass
	public static void tearDownClass() {
		Server.exit();
	}
	
}
