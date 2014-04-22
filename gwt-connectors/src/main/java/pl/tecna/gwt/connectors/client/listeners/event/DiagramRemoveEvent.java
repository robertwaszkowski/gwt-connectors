package pl.tecna.gwt.connectors.client.listeners.event;

import java.util.List;

import pl.tecna.gwt.connectors.client.elements.Connector;

import com.google.gwt.user.client.ui.Widget;

public class DiagramRemoveEvent implements ConnectorEvent {

  private Widget removedEl;
  private Connector removedConn;
  private List<Object> removedList;
  private double top;
  private double left;

  public DiagramRemoveEvent(Widget removedEl, double top, double left) {
    this.removedEl = removedEl;
    this.top = top;
    this.left = left;
  }

  public DiagramRemoveEvent(Connector removeConn, double top, double left) {
    this.removedConn = removeConn;
    this.top = top;
    this.left = left;
  }

  public DiagramRemoveEvent(List<Object> removedList) {
    this.removedList = removedList;
  }

  public Widget getRemovedEl() {
    return removedEl;
  }

  public Connector getRemovedConn() {
    return removedConn;
  }

  public double getTop() {
    return top;
  }

  public double getLeft() {
    return left;
  }

  public List<Object> getRemovedElements() {
    return removedList;
  }

}
