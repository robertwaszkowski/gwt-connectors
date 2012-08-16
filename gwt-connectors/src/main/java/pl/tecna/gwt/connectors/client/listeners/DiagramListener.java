package pl.tecna.gwt.connectors.client.listeners;

import pl.tecna.gwt.connectors.client.Connector;
import pl.tecna.gwt.connectors.client.listeners.change.DiagramChangeEvent;
import pl.tecna.gwt.connectors.client.listeners.change.DiagramEvent;

	

public interface DiagramListener {

	void onConnectorClick(Connector changedConnector);
	
	void onDiagramChanged(DiagramChangeEvent type, DiagramEvent event);
}
