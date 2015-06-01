package common;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import client.ChatWindow;
import client.ClientTestCase;
import server.Server;
import server.ServerTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ ServerTestSuite.class, client.ClientTestSuite.class })
public class CompleteTestSuite {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ServerTestSuite.runServer.start();
		ClientTestCase.user1 = new ChatWindow("localhost", 25566, "test");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Server.exit();
	}
}
