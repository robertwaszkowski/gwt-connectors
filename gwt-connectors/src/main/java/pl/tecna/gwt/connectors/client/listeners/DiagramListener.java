package pl.tecna.gwt.connectors.client.listeners;

import pl.tecna.gwt.connectors.client.listeners.event.ConnectorClickEvent;
import pl.tecna.gwt.connectors.client.listeners.event.ConnectorDoubleClickEvent;
import pl.tecna.gwt.connectors.client.listeners.event.DiagramAddEvent;
import pl.tecna.gwt.connectors.client.listeners.event.DiagramRemoveEvent;
import pl.tecna.gwt.connectors.client.listeners.event.ElementConnectEvent;
import pl.tecna.gwt.connectors.client.listeners.event.ElementDragEvent;


	

public interface DiagramListener {

	void onConnectorClick(ConnectorClickEvent event);

  void onConnectorDoubleClick(ConnectorDoubleClickEvent event);
	
	void onDiagramAdd(DiagramAddEvent event);
	
	void onDiagramRemove(DiagramRemoveEvent event);
	
	void onElementConnect(ElementConnectEvent event);
	
	void onElementDrag(ElementDragEvent event);
	
}
