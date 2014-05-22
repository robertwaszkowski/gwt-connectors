package pl.tecna.gwt.connectors.client.elements;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.Point;
import pl.tecna.gwt.connectors.client.images.ConnectorsBundle;
import pl.tecna.gwt.connectors.client.listeners.event.ElementConnectEvent;
import pl.tecna.gwt.connectors.client.util.ConnectorsClientBundle;
import pl.tecna.gwt.connectors.client.util.WidgetUtils;

import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class EndPoint extends Point {
  
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
  public EndPoint(Integer left, Integer top, Connector connector) {
    super(left, top);

    this.connector = connector;
    this.setGluedToConnectionPoint(false);
    Widget img = createImage();
    if (img != null) {
      this.setWidget(img);
    }
    this.getElement().getStyle().setZIndex(3);
  }

  public EndPoint(Integer left, Integer top) {
    super(left, top);
    this.setGluedToConnectionPoint(false);
    Widget img = createImage();
    if (img != null) {
      this.setWidget(img);
    }
    this.getElement().getStyle().setZIndex(3);
  }


  public void glueToConnectionPoint(ConnectionPoint connectionPoint) {
    glueToConnectionPoint(connectionPoint, true);
  }
  
  public void glueToConnectionPoint(ConnectionPoint connectionPoint, boolean fireEvent) {
    if (isGluedToConnectionPoint()) {
      unglueFromConnectionPoint();
    }
    this.gluedConnectionPoint = connectionPoint;
    connectionPoint.glueToEndPoint(this);
    this.setGluedToConnectionPoint(true);
    this.clear();

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
    WidgetUtils.addWidget(diagram.boundaryPanel, this, getDataCenterLeft(), getDataCenterTop());

    // Set EndPoint's cursor
    DOM.setStyleAttribute(this.getElement(), "cursor", "crosshair");

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

  protected Widget createImage() {
    Image img = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.end_point()).createImage();
    img.addStyleName(ConnectorsClientBundle.INSTANCE.css().imageDispBlock());
    return img;
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
    return (int) Math.ceil(getLeft() - ConnectionPoint.RADIUS);
  }
  
  public int getDataCenterTop() {
    return (int) Math.ceil(getTop() - ConnectionPoint.RADIUS);
  }
  
  public int getCurrentCenterLeft() {
    return (int) Math.ceil(getWidgetLocation().getLeft() - ConnectionPoint.RADIUS);
  }
  
  public int getCurrentCenterTop() {
    return (int) Math.ceil(getWidgetLocation().getTop() - ConnectionPoint.RADIUS);
  }
  
  private WidgetLocation getWidgetLocation() {
    return new WidgetLocation(this, connector.diagram.boundaryPanel);
  }
  
}
