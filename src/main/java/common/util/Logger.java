package common.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	
	PrintWriter out;
	File log;
	
	public Logger() throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-dd-MM(HH-mm)");
		Date time = new Date();
		String timestamp = dateFormat.format(time);
		
		log = new File("logs/log-" + timestamp + ".log");
		log.getParentFile().mkdirs();
		log.createNewFile();
		out = new PrintWriter(log);
	}
	
	public void log(String txt) {
		out.println(txt);
		out.flush();
	}
	
	public void close() {
		out.close();
	}
}
