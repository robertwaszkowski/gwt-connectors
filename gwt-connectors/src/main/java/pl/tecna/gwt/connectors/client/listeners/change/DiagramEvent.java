package pl.tecna.gwt.connectors.client.listeners.change;

public class DiagramEvent {

	protected Object element;
	
	public DiagramEvent(Object element) {
		
		this.element = element;
	}
	
	public Object getElement() {
		return element;
	}
}