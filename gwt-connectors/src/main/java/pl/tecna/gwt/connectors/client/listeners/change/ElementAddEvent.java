package pl.tecna.gwt.connectors.client.listeners.change;

import pl.tecna.gwt.connectors.client.util.Position;

public class ElementAddEvent extends DiagramEvent {

	private Position position;
	
	public ElementAddEvent(Position position, Object element) {
		
		super(element);
		this.position = position;		
	}
	
	public void setPosition(Position position) {
		this.position = position;
	}
	
	public Position getPosition() {
		return position;
	}
	
}
