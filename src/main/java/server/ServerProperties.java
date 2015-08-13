package server;

import java.util.HashMap;

import common.util.Properties;

public class ServerProperties extends Properties {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ServerProperties(String filename) {
		super(filename);
	}

	@Override
	public HashMap<String, String> getDefaultValues() {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("port", "25566");
		map.put("maxUsers", "10");
		
		return null;
	}
	
}
