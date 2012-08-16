package pl.tecna.gwt.connectors.client.listeners.change;

public class ConnectorRemoveEvent extends DiagramEvent {

	public ConnectorRemoveEvent(Object element) {
		
		super(element);
	}
	
	public Object getRemovedElement() {
		return element;
	}
	
}
