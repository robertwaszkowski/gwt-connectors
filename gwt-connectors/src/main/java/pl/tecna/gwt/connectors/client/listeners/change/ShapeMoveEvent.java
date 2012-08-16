package pl.tecna.gwt.connectors.client.listeners.change;

import com.google.gwt.user.client.ui.Widget;

import pl.tecna.gwt.connectors.client.util.Position;

public class ShapeMoveEvent extends DiagramEvent {

	private Position oldPosition;
	private Position newPosition;
	
	public ShapeMoveEvent(Position oldPosition, Position newPosition, Widget element) {
		
		super(element);
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}
	
	public Position getNewPosition() {
		return newPosition;
	}
	
	public Position getOldPosition() {
		return oldPosition;
	}
	
	public void setNewPosition(Position newPosition) {
		this.newPosition = newPosition;
	}
	
	public void setOldPosition(Position oldPosition) {
		this.oldPosition = oldPosition;
	}
}
