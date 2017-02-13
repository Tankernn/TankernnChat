package eu.tankernn.chat.server.test;

import org.junit.Assert;
import org.junit.Test;

import eu.tankernn.chat.server.CommandRegistry;

public class ServerTestCase {
	@Test
	public void testCommandLoading() {
		CommandRegistry registry = new CommandRegistry();
		Assert.assertTrue(registry.getHelp().contains("kick"));
		//registry.executeCommand("/help", caller);
	}
}
