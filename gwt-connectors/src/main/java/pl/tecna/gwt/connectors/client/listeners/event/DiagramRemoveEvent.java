package pl.tecna.gwt.connectors.client.listeners.event;

import pl.tecna.gwt.connectors.client.elements.Connector;

import com.google.gwt.user.client.ui.Widget;

public class DiagramRemoveEvent {

  private Widget removedEl;
  private Connector removedConn;
  private Integer top;
  private Integer left;
  
  public DiagramRemoveEvent(Widget removedEl, Integer top, Integer left) {
    this.removedEl = removedEl;
    this.top = top;
    this.left = left;
  }
  
  public DiagramRemoveEvent(Connector removeConn, Integer top, Integer left) {
    this.removedConn = removeConn;
    this.top = top;
    this.left = left;
  }
  
  public Widget getRemovedEl() {
    return removedEl;
  }
  
  public Connector getRemovedConn() {
    return removedConn;
  }
  
  public Integer getTop() {
    return top;
  }
  
  public Integer getLeft() {
    return left;
  }
  
}
