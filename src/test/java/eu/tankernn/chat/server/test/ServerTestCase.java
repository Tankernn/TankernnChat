package eu.tankernn.chat.server.test;

import org.junit.Assert;
import org.junit.Test;

import eu.tankernn.chat.server.CommandRegistry;
import eu.tankernn.chat.util.ArrayUtil;

public class ServerTestCase {
	
	@Test
	public void testArrayUtils() {
		Assert.assertArrayEquals(new String[] {"like", "trains"}, ArrayUtil.removeFirst(new String[] {"I", "like", "trains"}));
	}
	
	@Test
	public void testCommandLoading() {
		CommandRegistry registry = new CommandRegistry();
		Assert.assertTrue(registry.getHelp().contains("kick"));
		//registry.executeCommand("/help", caller);
	}
}
