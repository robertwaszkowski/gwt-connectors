package pl.tecna.gwt.connectors.client.listeners;

import pl.tecna.gwt.connectors.client.listeners.event.ConnectorClickEvent;
import pl.tecna.gwt.connectors.client.listeners.event.ConnectorDoubleClickEvent;

public interface ConnectorListener {

  void onConnectorClick(ConnectorClickEvent event);

  void onConnectorDoubleClick(ConnectorDoubleClickEvent event);

}
