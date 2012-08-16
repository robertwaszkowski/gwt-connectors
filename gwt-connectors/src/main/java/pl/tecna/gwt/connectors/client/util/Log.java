package pl.tecna.gwt.connectors.client.util;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.logging.client.TextLogFormatter;


public class Log {
	
	private final static java.util.logging.Logger log = Logger.getLogger("");
	private final static TextLogFormatter logFormatter = new TextLogFormatter(true);

	private final static Log instance = new Log();
	
	public Log() {
		
//		log.addHandler(new FirebugLogHandler());
		log.addHandler(new ConsoleLogHandler());
	}
	
	public static Log get() {
		
		return instance;
	}
	
	public static void fine(String message) {
		log.fine(message);
	}

	public static void info(String message) {
		
		log.info(message);
	}

	public static void warn(String message) {

		log.warning(message);
	}

	public static void severe(String message) {

		log.severe(message);
	}
	
	public static void fine(String message, Throwable e) {
		
		LogRecord logRecord = new LogRecord(Level.FINE, message);
		logRecord.setThrown(e);
		log.fine(logFormatter.format(logRecord));
	}

	public static void info(String message, Throwable e) {

		LogRecord logRecord = new LogRecord(Level.INFO, message);
		logRecord.setThrown(e);
		log.fine(logFormatter.format(logRecord));
	}

	public static void warn(String message, Throwable e) {

		LogRecord logRecord = new LogRecord(Level.WARNING, message);
		logRecord.setThrown(e);
		log.fine(logFormatter.format(logRecord));
	}

	public static void severe(String message, Throwable e) {

		LogRecord logRecord = new LogRecord(Level.SEVERE, message);
		logRecord.setThrown(e);
		log.fine(logFormatter.format(logRecord));
	}

}
