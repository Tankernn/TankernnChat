package server;

import static org.junit.Assert.*;

import org.junit.Test;

public class ServerTest {
	
	@Test
	public void testCInt() {
		assertEquals(Server.CInt("832"), 832);
	}
	
	@Test
	public void testStart() {
		Server.main(new String[]{});
	}

}
