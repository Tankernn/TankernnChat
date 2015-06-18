package server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = { client.ClientTestSuite.class })
public class ServerTestSuite {

	public static Thread runServer = new Thread() {
		@Override
		public void run() {
			Server.main(new String[] {});
		}
	};
}
