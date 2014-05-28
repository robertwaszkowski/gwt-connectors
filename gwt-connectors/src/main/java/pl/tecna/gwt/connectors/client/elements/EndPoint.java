package pl.tecna.gwt.connectors.client.elements;

import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.Point;
import pl.tecna.gwt.connectors.client.listeners.event.ElementConnectEvent;
import pl.tecna.gwt.connectors.client.util.ConnectorsClientBundle;
import pl.tecna.gwt.connectors.client.util.WidgetUtils;

import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class EndPoint extends Point {

  private static final Logger LOG = Logger.getLogger("EndPoint");
  
  public static final int SIZE = 8;
  public static final int RADIUS = 4;
  
  private Timer endPointsHideTimer;
  private HandlerRegistration overHandlerReg;
  private HandlerRegistration outHandlerReg;
  
  public ConnectionPoint gluedConnectionPoint;
  public Connector connector;
  boolean gluedToConnectionPoint;
  
  /**
   * {@link Shape} attached to the {@link EndPoint}. It would be moved together with
   * end point.
   */
  private Shape linkedShape;

  /**
   * {@link Connector}s are ended with EndPoints. You can drag and drop EndPoint to change its
   * position. When EndPoint is dragging its Connector is redrawing.
   * <p>
   * {@link EndPoint} is visible until the {@link Connector} is glued to {@link ConnectionPoint}.
   * EndPoints are represented by small circles.
   */
  public EndPoint(Integer left, Integer top) {
    super(left, top);
    getElement().getStyle().setZIndex(3);
    
    addMouseOverHandler(new MouseOverHandler() {
      
      @Override
      public void onMouseOver(MouseOverEvent event) {
        if (isGluedToConnectionPoint()) {
          setConnectorEndPointStyle();
          gluedConnectionPoint.getParentShape().hideShapeConnectorStartPionts();
        }
      }
    });
    
    addMouseOutHandler(new MouseOutHandler() {
      
      @Override
      public void onMouseOut(MouseOutEvent event) {
        if (isGluedToConnectionPoint()) {
          setTransparent();
        }
      }
    });
  }

  public void glueToConnectionPoint(ConnectionPoint connectionPoint) {
    glueToConnectionPoint(connectionPoint, true);
  }
  
  public void glueToConnectionPoint(ConnectionPoint connectionPoint, boolean fireEvent) {
    if (isGluedToConnectionPoint()) {
      unglueFromConnectionPoint();
    }
    if (this.getParent() == connector.diagram.boundaryPanel) {
      WidgetUtils.setWidgetPosition(connector.diagram.boundaryPanel, this, 
          connectionPoint.getConnectionPositionLeft() - RADIUS, 
          connectionPoint.getConnectionPositionTop() - RADIUS);
    } else {
      WidgetUtils.addWidget(connector.diagram.boundaryPanel, this, 
          connectionPoint.getConnectionPositionLeft() - RADIUS, 
          connectionPoint.getConnectionPositionTop() - RADIUS);
    }
    this.gluedConnectionPoint = connectionPoint;
    connectionPoint.glueToEndPoint(this);
    this.setGluedToConnectionPoint(true);
    setTransparent();

    if (fireEvent) {
      connector.diagram.onElementConnect(new ElementConnectEvent(connectionPoint.parentWidget, connector, this));
    }
  }

  /**
   * 
   */
  public void unglueFromConnectionPoint() {
    if (gluedConnectionPoint != null) {
      gluedConnectionPoint.unglueFromEndPoint(this);
      gluedConnectionPoint = null;
    }
    setGluedToConnectionPoint(false);
  }

  /**
	 * 
	 */
  public void update() {
    WidgetUtils.setWidgetPosition(((AbsolutePanel) this.getParent()), this, getDataCenterLeft(), getDataCenterTop());
  }

  /**
   * @param diagram
   */
  public void showOnDiagram(Diagram diagram) {
    // Add EndPoint to given panel
    WidgetUtils.addWidget(diagram.boundaryPanel, this, getLeft() - RADIUS, getTop() - RADIUS);

    // Make EndPoint draggable
    diagram.endPointDragController.makeDraggable(this);
    
  }

  public void updateOpositeEndPointOfVerticalSection() {
    // Updates position of connector after moving the EndPoint
    // Find section, then find opposite Corner Point.
    // Update position of opposite Corner Point and update Section
    Section prevSection = this.connector.prevSectionForPoint(this);
    if (prevSection != null) {
      prevSection.getStartPoint().setLeftPosition(this.getLeft());
      this.connector.update();
    }

    Section nextSection = this.connector.nextSectionForPoint(this);
    if (nextSection != null) {
      nextSection.getEndPoint().setLeftPosition(this.getLeft());
      this.connector.update();
    }

  }

  public void updateOpositeEndPointOfHorizontalSection() {
    // Updates position of connector after moving the EndPoint
    // Find section, then find opposite Corner Point.
    // Update position of opposite Corner Point and update Section
    Section prevSection = this.connector.prevSectionForPoint(this);
    if (prevSection != null) {
      prevSection.getStartPoint().setTopPosition(this.getTop());
      this.connector.update();
    }

    Section nextSection = this.connector.nextSectionForPoint(this);
    if (nextSection != null) {
      nextSection.getEndPoint().setTopPosition(this.getTop());
      this.connector.update();
    }

  }

  public boolean isGluedToConnectionPoint() {
    return gluedToConnectionPoint;
  }

  public void setGluedToConnectionPoint(boolean gluedToConnectionPoint) {
    this.gluedToConnectionPoint = gluedToConnectionPoint;
  }

  public void linkShape(Shape shape) {
    linkedShape = shape;
  }
  
  @Override
  public void setPosition(Integer newLeft, Integer newTop) {
    moveLinkedShape(newLeft - this.left, newTop - this.top);
    super.setPosition(newLeft, newTop);
  }
  
  public void moveLinkedShape(Integer offsetLeft, Integer offsetTop) {
    if (linkedShape != null && linkedShape.isAttached()) {
      WidgetLocation linkedShapeLocation = new WidgetLocation(linkedShape, linkedShape.diagram.boundaryPanel);
      linkedShape.left = linkedShapeLocation.getLeft() + offsetLeft;
      linkedShape.top = linkedShapeLocation.getTop() + offsetTop;
      WidgetUtils.setWidgetPosition(linkedShape.diagram.boundaryPanel, linkedShape, linkedShape.left, linkedShape.top);
      linkedShape.updateConnectors();
    }
  }
  
  public boolean isStart() {
    if (connector != null) {
      if (connector.startEndPoint == this) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isEnd() {
    if (connector != null) {
      if (connector.endEndPoint == this) {
        return true;
      }
    }
    return false;
  }
  
  public Point findNeighboringEndPoint() {
    for (Section section : connector.sections) {
      if (section.startPoint == this) { 
        return section.endPoint;
      } else if (section.endPoint == this) { 
        return section.startPoint;
      }
    }
    return null;
  }
  
  public int getDataCenterLeft() {
    return getLeft() - ConnectionPoint.RADIUS;
  }
  
  public int getDataCenterTop() {
    return getTop() - ConnectionPoint.RADIUS;
  }
  
  public int getCurrentCenterLeft() {
    return getWidgetLocation().getLeft() - ConnectionPoint.RADIUS;
  }
  
  public int getCurrentCenterTop() {
    return getWidgetLocation().getTop() - ConnectionPoint.RADIUS;
  }
  
  private WidgetLocation getWidgetLocation() {
    return new WidgetLocation(this, connector.diagram.boundaryPanel);
  }
  
  public void setTransparent() {
    addStyleName(ConnectorsClientBundle.INSTANCE.css().endPointTransparent());
  }
  
  public void setConnectorEndPointStyle() {
    setStyleName(ConnectorsClientBundle.INSTANCE.css().endPoint());
  }
 
  public void setConnectorCreateStyle() {
    setStyleName(ConnectorsClientBundle.INSTANCE.css().endPointConnectorCreate());
  }
  
  public void enableConnectorCreate(Timer timer, ConnectionPoint startCP) {
    gluedConnectionPoint = startCP;
    endPointsHideTimer = timer;
    if (outHandlerReg == null) {
      MouseOutHandler mouseOutHandler = new MouseOutHandler() {

        public void onMouseOut(MouseOutEvent event) {
          endPointsHideTimer.schedule(Shape.END_POINTS_VIS_DELAY);
        }
      };
      outHandlerReg = addMouseOutHandler(mouseOutHandler);
    }
    
    if (overHandlerReg == null) {
      MouseOverHandler mouseOverHandler = new MouseOverHandler() {

        public void onMouseOver(MouseOverEvent event) {
          endPointsHideTimer.cancel();
        }
      };
      overHandlerReg = addMouseOverHandler(mouseOverHandler);
    }
    setStyleName(ConnectorsClientBundle.INSTANCE.css().shapeConnector());
  }

  public void disableConnectorCreate() {
    outHandlerReg.removeHandler();
    overHandlerReg.removeHandler();
    outHandlerReg = null;
    overHandlerReg = null;
  }
  
}
