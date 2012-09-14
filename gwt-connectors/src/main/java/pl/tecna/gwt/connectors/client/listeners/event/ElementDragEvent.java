package pl.tecna.gwt.connectors.client.listeners.event;

import com.google.gwt.user.client.ui.Widget;

public class ElementDragEvent {

  public enum DragEventType {
    
    DRAG_START, DRAG_END;
  }
  
  private Widget draggedEl;
  private Integer dragLeft;
  private Integer dragTop;
  private DragEventType type;
  
  public ElementDragEvent(Widget draggedEl, Integer dragLeft, Integer dragTop, DragEventType type) {
    this.draggedEl = draggedEl;
    this.dragLeft = dragLeft;
    this.dragTop = dragTop;
    this.type = type;
  }
  
  public Widget getMovedEl() {
    return draggedEl;
  }
  
  public Integer getDragLeft() {
    return dragLeft;
  }
  
  public Integer getDragTop() {
    return dragTop;
  }
  
  public DragEventType getMoveEventType() {
    return type;
  }
  
}
