package common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Properties extends java.util.Properties {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String filename;
	HashMap<String, String> defaultValues;
	
	public Properties(String filename) {
		this.filename = filename;
	}
	
	public void loadProperties() {
		try {
			load(new FileInputStream(this.filename));
		} catch (FileNotFoundException e1) {
			newPropertiesFile();
		} catch (IOException e2) {
			System.out.println("Could not load properties.");
			e2.printStackTrace();
		}
	}
	
	void newPropertiesFile() {
		System.out.println("Generating new properties file.");
		try {
			new File(this.filename).createNewFile();
			
			Iterator<Entry<String, String>> it = defaultValues.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String, String> pair = (Map.Entry<String, String>)it.next();
		        setProperty(pair.getKey(), pair.getValue());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
			
			store();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getInt(String key) {
		int num;
		try {
			num = Numbers.CInt(getProperty(key));
			return num;
		} catch (NullPointerException ex) {
			System.out.println("The property " + key + " could not be read as a number.");
			return -1;
		}
	}
	
	public void store() {
		try {
			super.store(new PrintWriter(this.filename), "Config file for TankernnChat");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public abstract HashMap<String, String> getDefaultValues();
}
