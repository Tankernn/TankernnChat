package server;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class ServerTestCase {
	
	@BeforeClass
	public static void setUpClass() {
		ServerTestSuite.runServer.start();
	}
	
	@AfterClass
	public static void tearDownClass() {
		Server.exit();
	}
}
