package eu.tankernn.chat.common.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import eu.tankernn.chat.server.Server;
import eu.tankernn.chat.server.test.ServerTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ServerTestSuite.class, eu.tankernn.chat.client.test.ClientTestSuite.class})
public class CompleteTestSuite {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Server.main(new String[] {});
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Server.exit();
	}
}
