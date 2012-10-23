package pl.tecna.gwt.connectors.client.listeners;

import java.util.HashSet;
import java.util.Set;


import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;

/**
 * Class that can take keyboard events. To start keyboard listening invoke
 * {@link #init()}.
 */
// @SuppressWarnings("deprecation")
public final class Keyboard {
	private static final class WindowCloseListenerImpl implements
			Window.ClosingHandler {
	
		public native void onWindowClosed() /*-{
			$doc.onkeydown = null;
			$doc.onkeypress = null;
			$doc.onkeyup = null;
		}-*/;

		public String onWindowClosing() {
			return null;
		}

		private native void init() /*-{
			$doc.onkeydown = function(evt) {
			    @pl.tecna.gwt.connectors.client.listeners.Keyboard::onKeyDown(Lcom/google/gwt/user/client/Event;)(evt || $wnd.event);
			  }

			  $doc.onkeypress = function(evt) {
			    @pl.tecna.gwt.connectors.client.listeners.Keyboard::onKeyPress(Lcom/google/gwt/user/client/Event;)(evt || $wnd.event);
			  }

			  $doc.onkeyup = function(evt) {
			    @pl.tecna.gwt.connectors.client.listeners.Keyboard::onKeyUp(Lcom/google/gwt/user/client/Event;)(evt || $wnd.event);
			  }
		}-*/;

		public void onWindowClosing(ClosingEvent event) {
			
		}
	}

	private static Keyboard instance = new Keyboard();
	private HandlerRegistration windowClosingHandler = null;
	
	private Set<KeyboardListener> listeners;

	private Keyboard() {
		listeners = new HashSet<KeyboardListener>();
	}

	/**
	 * Gets Keyboard instance.
	 * 
	 * @return instance
	 */
	public static Keyboard getInstance() {
		return instance;
	}

	/**
	 * Init keyboard listener.
	 */
	public void init() {
	  if (windowClosingHandler == null) {
	    WindowCloseListenerImpl closingHandler = new WindowCloseListenerImpl();
	    windowClosingHandler = Window.addWindowClosingHandler(closingHandler);
	    closingHandler.init();
	  }
	}

	/**
	 * Adds keyboard listener.
	 * 
	 * @param listener keyboard listener
	 */
	public void addListener(KeyboardListener listener) {
		listeners.add(listener);
	}

	@SuppressWarnings("unused")
	private static void onKeyDown(Event event) {
		for (KeyboardListener listener : getInstance().listeners) {
			listener.onKeyDown(event.getKeyCode(), event);
		}
//		System.out.println("Keyboard.onKeyDown " + event.getKeyCode());
	}

	@SuppressWarnings("unused")
	private static void onKeyPress(Event event) {
		for (KeyboardListener listener : getInstance().listeners) {
			listener.onKeyPress(event.getKeyCode(), event);
		}
//		System.out.println("Keyboard.onKeyPress " + event.getKeyCode());
	}

	@SuppressWarnings("unused")
	private static void onKeyUp(Event event) {
		for (KeyboardListener listener : getInstance().listeners) {
			listener.onKeyUp(event.getKeyCode(), event);
		}
//		System.out.println("Keyboard.onKeyUp " + event.getKeyCode());
	}
}
