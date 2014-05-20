package pl.tecna.gwt.connectors.client.elements;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.Point;
import pl.tecna.gwt.connectors.client.images.ConnectorsBundle;
import pl.tecna.gwt.connectors.client.listeners.event.ElementConnectEvent;
import pl.tecna.gwt.connectors.client.util.ConnectorsClientBundle;
import pl.tecna.gwt.connectors.client.util.Coordinate;
import pl.tecna.gwt.connectors.client.util.CoordinatesUtils;
import pl.tecna.gwt.connectors.client.util.WidgetUtils;

import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class EndPoint extends WidgetDiagramElement implements Coordinate {

  //Defines size of connection point
  public static final int CP_MARGIN = 13;
  public static final int CENTER_OFFSET = (int) Math.floor(CP_MARGIN / 2.0);
  
  public ConnectionPoint gluedConnectionPoint;
  public Connector connector;
  boolean gluedToConnectionPoint;
  
  protected int left;
  protected int top;
  
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
    this.left = left;
    this.top = top;

    this.connector = connector;
    this.setGluedToConnectionPoint(false);
    Widget img = createImage();
    if (img != null) {
      this.setWidget(img);
    }
    this.getElement().getStyle().setZIndex(3);
  }

  public EndPoint(Integer left, Integer top) {
    this.left = left;
    this.top = top;
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
    this.gluedConnectionPoint.unglueFromEndPoint(this);
    this.gluedConnectionPoint = null;
    this.setGluedToConnectionPoint(false);
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
  public void showOnDiagram() {
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
      if (section.startCoordinates == this) { 
        return section.endCoordinates;
      } else if (section.endCoordinates == this) { 
        return section.startCoordinates;
      }
    }
    return null;
  }
  
  public int getDataCenterLeft() {
    return (int) Math.ceil(getLeft() - getCenterOffset());
  }
  
  public int getDataCenterTop() {
    return (int) Math.ceil(getTop() - getCenterOffset());
  }
  
  public int getCurrentCenterLeft() {
    return (int) Math.ceil(getWidgetLocation().getLeft() - getCenterOffset());
  }
  
  public int getCurrentCenterTop() {
    return (int) Math.ceil(getWidgetLocation().getTop() - getCenterOffset());
  }
  
  private double getCenterOffset() {
    return (double) CP_MARGIN / 2.0;
  }
  
  private WidgetLocation getWidgetLocation() {
    return new WidgetLocation(this, connector.diagram.boundaryPanel);
  }
  
  public void setLeft(int left) {
    this.left = left;
  }
  
  public void setTop(int top) {
    this.top = top;
  }

  @Override
  public int getLeft() {
    return left;
  }

  @Override
  public int getTop() {
    return top;
  }

  @Override
  public int compareTo(Coordinate coordinate) {
    return CoordinatesUtils.compare(this, coordinate);
  }

  @Override
  public void set(int left, int top) {
    this.left = left;
    this.top = top;
  }
  
}
