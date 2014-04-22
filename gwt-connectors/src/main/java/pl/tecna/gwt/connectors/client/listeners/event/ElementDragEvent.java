package pl.tecna.gwt.connectors.client.listeners.event;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.google.gwt.user.client.ui.Widget;

public class ElementDragEvent implements ConnectorEvent {

  public enum DragEventType {
    DRAG_START, DRAG_END;
  }

  private DragContext dragContext;
  private List<Widget> draggedList;
  private double dragLeft;
  private double dragTop;
  private DragEventType type;

  public ElementDragEvent(DragContext dragContext, double dragLeft, double dragTop, DragEventType type) {
    this.dragContext = dragContext;
    this.dragLeft = dragLeft;
    this.dragTop = dragTop;
    this.type = type;
  }

  public ElementDragEvent(List<Widget> draggedList, double dragLeft, double dragTop, DragEventType type) {
    this.draggedList = draggedList;
    this.dragLeft = dragLeft;
    this.dragTop = dragTop;
    this.type = type;
  }

  public DragContext getDragContext() {
    return dragContext;
  }

  public double getDragLeft() {
    return dragLeft;
  }

  public double getDragTop() {
    return dragTop;
  }

  public DragEventType getMoveEventType() {
    return type;
  }

  public List<Widget> getDraggedList() {
    return draggedList;
  }

}
