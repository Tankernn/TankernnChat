package common;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import server.ServerTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ ServerTestSuite.class, client.ClientTestSuite.class })
public class CompleteTestSuite {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}
}
