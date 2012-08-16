package pl.tecna.gwt.connectors.client.util;

public class Logger {

	private String clazz;
	
	public Logger(String clazz) {
		
		this.clazz = clazz;
	}
	
	public void i(String message) {

		Log.info("[" + clazz + "] : " + message);
	}

	public void d(String message) {

		Log.fine("[" + clazz + "] : " + message);
	}

	public void w(String message) {

		Log.warn("[" + clazz + "] : " + message);
	}

	public void e(String message) {

		Log.severe("[" + clazz + "] : " + message);
	}
	
	public void i(String message, Throwable e) {

		Log.info("[" + clazz + "] : " + message, e);
	}

	public void d(String message, Throwable e) {

		Log.fine("[" + clazz + "] : " + message, e);
	}

	public void w(String message, Throwable e) {

		Log.warn("[" + clazz + "] : " + message, e);
	}

	public void e(String message, Throwable e) {

		Log.severe("[" + clazz + "] : " + message, e);
	}
}
