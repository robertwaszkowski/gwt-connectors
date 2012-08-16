package pl.tecna.gwt.connectors.client.listeners.change;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.EndPoint;
import pl.tecna.gwt.connectors.client.Shape;

public class EndPointConnectEvent extends DiagramEvent {

	private EndPoint connectedEP;
	private ConnectionPoint connectionPoint;
	
	public EndPointConnectEvent(EndPoint connectedEP, ConnectionPoint connectionPoint) {
		
		super(connectionPoint.getParentShape());
		this.connectedEP = connectedEP;
		this.connectionPoint = connectionPoint;
	}

	public EndPoint getConnectedEP() {
		return connectedEP;
	}

	public ConnectionPoint getConnectionPoint() {
		return connectionPoint;
	}

	public Shape getShape() {
		return (Shape) element;
	}
	
}
