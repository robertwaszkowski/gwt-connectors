package pl.tecna.gwt.connectors.client.listeners.change;

public class ElementRemoveEvent extends DiagramEvent {

	public ElementRemoveEvent(Object element) {
		
		super(element);
	}
	
	public Object getRemovedElement() {
		return element;
	}
	
}
