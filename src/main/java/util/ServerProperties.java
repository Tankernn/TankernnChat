package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class ServerProperties extends java.util.Properties{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void loadProperties() {
		try {
			load(new FileInputStream("server.properties"));
		} catch (FileNotFoundException e1) {
			newPropertiesFile();
		} catch (IOException e2) {
			System.out.println("Could not load properties.");
			e2.printStackTrace();
		}
	}
	
	public int getNumberProperty(String key) {
		int num;
		try {
			num = Numbers.CInt(getProperty(key));
			return num;
		} catch (NullPointerException ex) {
			System.out.println("The property " + key + " could not be read as a number.");
			return -1;
		}
	}
	
	void newPropertiesFile() {
		System.out.println("Generating new properties file.");
		try {
			new File("server.properties").createNewFile();
			setProperty("port", "25566");
			setProperty("maxUsers", "20");
			store(new FileWriter("server.properties"), "ChatServer config file");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
