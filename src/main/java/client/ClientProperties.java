package client;

import java.util.HashMap;

import common.util.Properties;

public class ClientProperties extends Properties{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientProperties(String filename) {
		super(filename);
	}

	@Override
	public HashMap<String, String> getDefaultValues() {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("host", "tankernn.eu");
		map.put("port", "25566");
		map.put("username", "Username");
		
		return null;
	}
	
}
