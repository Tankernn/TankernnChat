package server;

import static org.junit.Assert.*;
import org.junit.Test;

public class ServerTestCase {

	@Test
	public void testCInt() {
		assertEquals(Server.CInt("832"), 832);
	}
}
