package pl.tecna.gwt.connectors.client.listeners.change;

public class ElementDragEvent extends DiagramEvent {

  public static String DRAG_START = "drag_start";
  public static String DRAG_END = "drag_end";
  
  private String eventType;
  
  public ElementDragEvent(Object element, String eventType) {
    super(element);
    this.setEventType(eventType);
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public String getEventType() {
    return eventType;
  } 

}
