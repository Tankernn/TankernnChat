package client;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class ClientTestCase {
	public static ChatWindow user1;
	public static ChatWindow user2;

    @BeforeClass 
    public static void setUpClass() {      
        user1 = new ChatWindow("localhost", 25566, "user1");
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

}
