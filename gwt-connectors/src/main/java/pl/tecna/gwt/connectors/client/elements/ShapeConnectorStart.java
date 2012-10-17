package pl.tecna.gwt.connectors.client.elements;

import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.util.ConnectorsClientBundle;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;

public class ShapeConnectorStart extends EndPoint {

  private final Logger LOG = Logger.getLogger(this.getClass().getName());
  public Shape shape;
  private Timer endPointsTimer; 
  private HandlerRegistration overHandlerReg;
  private HandlerRegistration outHandlerReg;
  private ConnectionPoint overlapingCP;
  
  public ShapeConnectorStart(Integer left, Integer top, Shape shape, Timer endPointsTimer, ConnectionPoint overlapingCP) {
    super(left, top);
    this.shape = shape;
    this.endPointsTimer = endPointsTimer;
    this.overlapingCP = overlapingCP;
    addHandlers();
    setStyle();
  }
  
  protected void setStyle() {
    setStyleName(ConnectorsClientBundle.INSTANCE.css().gwtConnectorsShapeConnectorStart());
  }
  
  public void removeStyle() {
    removeStyleName(ConnectorsClientBundle.INSTANCE.css().gwtConnectorsShapeConnectorStart());
  }
  
  private void addHandlers() {
    
    MouseOverHandler mouseOverHandler = new MouseOverHandler() {
      
      public void onMouseOver(MouseOverEvent event) {
        endPointsTimer.cancel();
      }
    };
    
    MouseOutHandler mouseOutHandler = new MouseOutHandler() {
      
      public void onMouseOut(MouseOutEvent event) {
        endPointsTimer.schedule(Shape.END_POINTS_VIS_DELAY);
      }
    };
    
    outHandlerReg = addMouseOutHandler(mouseOutHandler);
    overHandlerReg = addMouseOverHandler(mouseOverHandler);
    
  }
  
  public void removeHandlers() {
    outHandlerReg.removeHandler();
    overHandlerReg.removeHandler();
  }

  public ConnectionPoint getOverlapingCP() {
    return overlapingCP;
  }

  public Shape getShape() {
    return shape;
  }
  
}
