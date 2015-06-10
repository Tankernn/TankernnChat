package client;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import server.Server;

public class ClientTestCase {
	public static ChatWindow user1;
	public static ChatWindow user2;
	
	static Thread runServer = new Thread(){
    	@Override
    	public void run() {
    		Server.main(new String[]{});
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
	
	@AfterClass
	public static void tearDownClass() {
		Server.exit();
	}

}
