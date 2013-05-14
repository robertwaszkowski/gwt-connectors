package pl.tecna.gwt.connectors.client.listeners.event;

import pl.tecna.gwt.connectors.client.elements.Connector;

import com.google.gwt.user.client.ui.Widget;

public class DiagramAddEvent implements ConnectorEvent {

  private Widget addedEl;
  private Connector addedConn;
  private Integer top;
  private Integer left;
  
  public DiagramAddEvent(Widget addedEl, Integer top, Integer left) {
    this.addedEl = addedEl;
    this.top = top;
    this.left = left;
  }
  
  public DiagramAddEvent(Connector addedConn, Integer top, Integer left) {
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
  
  public Integer getTop() {
    return top;
  }
  
  public Integer getLeft() {
    return left;
  }
  
}
