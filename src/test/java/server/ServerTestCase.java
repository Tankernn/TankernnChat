package server;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServerTestCase {

	@BeforeClass
	public static void setUpClass() {
		ServerTestSuite.runServer.start();
	}

	@Test
	public void testCInt() {
		assertEquals(server.util.Numbers.CInt("832"), 832);
	}

	@AfterClass
	public static void tearDownClass() {
		Server.exit();
	}
}
