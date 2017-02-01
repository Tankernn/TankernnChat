package server;

import org.junit.Assert;
import org.junit.Test;

import util.ArrayUtil;

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
