package pl.tecna.gwt.connectors.client.listeners.event;

import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.EndPoint;

import com.google.gwt.user.client.ui.Widget;

public class ElementConnectEvent implements ConnectorEvent {

  private Widget connected;
  private Connector connector;
  private EndPoint endPoint;

  public ElementConnectEvent(Widget connected, Connector connector, EndPoint endPoint) {
    this.connected = connected;
    this.connector = connector;
    this.endPoint = endPoint;
  }

  public Widget getConnected() {
    return connected;
  }

  public Connector getConnector() {
    return connector;
  }

  public EndPoint getEndPoint() {
    return endPoint;
  }

}
