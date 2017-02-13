package eu.tankernn.chat.server.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = {eu.tankernn.chat.client.test.ClientTestSuite.class})
public class ServerTestSuite {
}
