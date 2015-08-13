package client;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import server.BanNote;
import server.Server;

public class ClientTestCase {
	public static ChatWindow user1;
	public static ChatWindow user2;
	
	static Thread runServer = new Thread() {
		@Override
		public void run() {
			Server.main(new String[] {});
		}
	};
	
	@BeforeClass
	public static void setUpClass() {
		runServer.start();
		user1 = new ChatWindow("localhost", 25566, "user1");
		assertTrue(user1.so.isConnected());
	}
	
	@Test
	public void testSend() {
		user1.send("Hello!");
	}
	
	@Test
	public void testPM() {
		user2 = new ChatWindow("localhost", 25566, "user2");
		
		user1.send("/pm user2 Hi there user2!");
	}
	
	@Test
	public void testBan() {
		Server.banNotes.add(new BanNote("localhost"));
		assertTrue(user1.so.isClosed());
		
		user1.connect("localhost", 25566, "user1");
		assertTrue(user1.so.isClosed());
		
		Server.banNotes.clear();
		
		user1.connect("localhost", 25566, "user1");
		assertTrue(!user1.so.isClosed());
	}
	
	@Test
	public void testNames() {
		user1.connect("localhost", 25566, "user 1");
		assertTrue(user1.so.isClosed());
		
		user1.connect("localhost", 25566, "user1");
		user2 = new ChatWindow("localhost", 25566, "user1");
		
		assertTrue(user2.so.isClosed());
		assertTrue(!user1.so.isClosed());
	}
	
	@Test
	public void testFullServer() {
		ArrayList<ChatWindow> arr = new ArrayList<ChatWindow>();
		
		for (int i = 0; i < 100; i++)
			arr.add(new ChatWindow("localhost", 25566, "user" + i));
		
		assertTrue(arr.get(99).so.isClosed());
	}
	
	@AfterClass
	public static void tearDownClass() {
		Server.exit();
	}
	
}
