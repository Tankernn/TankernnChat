package server;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import common.util.Numbers;

public class ServerTestCase {
	
	@BeforeClass
	public static void setUpClass() {
		ServerTestSuite.runServer.start();
	}
	
	@Test
	public void testCInt() {
		assertEquals(Numbers.CInt("832"), 832);
	}
	
	@AfterClass
	public static void tearDownClass() {
		Server.exit();
	}
}
