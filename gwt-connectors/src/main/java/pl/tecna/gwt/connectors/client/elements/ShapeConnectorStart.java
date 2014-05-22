package pl.tecna.gwt.connectors.client.elements;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.images.ConnectorsBundle;
import pl.tecna.gwt.connectors.client.util.ConnectorsClientBundle;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ShapeConnectorStart extends EndPoint {

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
    DOM.setStyleAttribute(this.getElement(), "cursor", "pointer");
  }

  private void addHandlers() {

    if (outHandlerReg == null) {
      
      MouseOutHandler mouseOutHandler = new MouseOutHandler() {

        public void onMouseOut(MouseOutEvent event) {
          endPointsTimer.schedule(Shape.END_POINTS_VIS_DELAY);
        }
      };

      outHandlerReg = addMouseOutHandler(mouseOutHandler);
    }
    
    if (overHandlerReg == null) {
      
      MouseOverHandler mouseOverHandler = new MouseOverHandler() {

        public void onMouseOver(MouseOverEvent event) {
          endPointsTimer.cancel();
        }
      };
      
      overHandlerReg = addMouseOverHandler(mouseOverHandler);
    }

  }

  public void removeHandlers() {
    outHandlerReg.removeHandler();
    overHandlerReg.removeHandler();
    
    outHandlerReg = null;
    overHandlerReg = null;
  }

  public ConnectionPoint getOverlapingCP() {
    return overlapingCP;
  }

  public Shape getShape() {
    return shape;
  }

  @Override
  protected Widget createImage() {
    Widget w = new AbsolutePanel();
    w.addStyleName(ConnectorsClientBundle.INSTANCE.css().gwtConnectorsShapeConnectorInner());
    return w;
  }

  public Image createEndPointImage() {
    Image img = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.end_point()).createImage();
    img.addStyleName(ConnectorsClientBundle.INSTANCE.css().imageDispBlock());
    return img;
  }

}
