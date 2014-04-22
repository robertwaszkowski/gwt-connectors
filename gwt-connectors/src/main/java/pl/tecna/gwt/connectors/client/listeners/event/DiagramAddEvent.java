package pl.tecna.gwt.connectors.client.listeners.event;

import pl.tecna.gwt.connectors.client.elements.Connector;

import com.google.gwt.user.client.ui.Widget;

public class DiagramAddEvent implements ConnectorEvent {

  private Widget addedEl;
  private Connector addedConn;
  private double top;
  private double left;

  public DiagramAddEvent(Widget addedEl, double top, double left) {
    this.addedEl = addedEl;
    this.top = top;
    this.left = left;
  }

  public DiagramAddEvent(Connector addedConn, double top, double left) {
    this.addedConn = addedConn;
    this.top = top;
    this.left = left;
  }

  public Widget getAddedEl() {
    return addedEl;
  }

  public Connector getAddedConn() {
    return addedConn;
  }

  public double getTop() {
    return top;
  }

  public double getLeft() {
    return left;
  }

}
