package pl.tecna.gwt.connectors.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.drop.DiagramWidgetDropController;
import pl.tecna.gwt.connectors.client.util.ConnectorsClientBundle;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class Shape extends FocusPanel{

  /**
   * Enum with values defining Shape object connection points positioning type
   * @author Kamil Kurek
   *
   */
  
  private Logger LOG = Logger.getLogger("Shape");

  public enum CPShapeType {
    OVAL,
    DIAMOND,
    RECTANGLE,
    USER_DEFINED
  }

  public CPShapeType cpShapeType;

  public Widget connectedWidget;

  public List<ConnectionPoint> connectionPoints;

  public AbsolutePanel connectionPointsPanel;

  public DiagramWidgetDropController shapeDropController;

  private boolean enableOverlap = false;

  public Diagram diagram;

  public int left = 0;
  public int top = 0;

  /**
   *  Defines size of connection point margin
   */
  public static int CP_MARGIN = 7;
  /**
   *  Defines tolerance in merging sections
   */
  public static int SECTION_TOLERANCE = 20;
  public int offsetTop = 0;
  public int offsetLeft = 0;

  /**
   * Defines translation
   */
  public int translationX = 0;
  public int translationY = 0;

  public Shape(Widget w) {

    this(w, CPShapeType.RECTANGLE);
  }

  /**
   * Shape constructor
   * @param w Widget to which the Shape is connected
   * @param cpShapeType defines how connection points will be placed, when set to USER_DEFINED You have to override createUserDefinedShapeCP method
   */
  public Shape(Widget w, CPShapeType cpShapeType) {
    this.connectedWidget = w;
    this.cpShapeType = cpShapeType;
    this.setStylePrimaryName(ConnectorsClientBundle.INSTANCE.css().shapeUnselected());
  }

  public void showOnDiagram(final Diagram diagram) {

    this.diagram = diagram;

    sinkEvents(Event.ONDBLCLICK);

    // Add Shape to Diagram
    diagram.shapes.add(this);

    // Add shape to parent panel
    this.setPixelSize(
        connectedWidget.getOffsetWidth() + CP_MARGIN * 2 + offsetLeft, 
        connectedWidget.getOffsetHeight() + CP_MARGIN * 2 + offsetTop);

    //		int padding = Integer.parseInt(this.getElement().getStyle().getPadding());
    int padding = 3;
    ((AbsolutePanel)connectedWidget.getParent()).add(
        this, 
        connectedWidget.getAbsoluteLeft() - connectedWidget.getParent().getAbsoluteLeft() - CP_MARGIN - offsetLeft - padding,
        connectedWidget.getAbsoluteTop() - connectedWidget.getParent().getAbsoluteTop() - CP_MARGIN - offsetTop - padding);

    // Add Absolute Panel which contains Widget in the center and ConnectionPoints on North, East, South, and West
    this.left = connectedWidget.getAbsoluteLeft() - connectedWidget.getParent().getAbsoluteLeft() - CP_MARGIN - offsetLeft;
    this.top = connectedWidget.getAbsoluteTop() - connectedWidget.getParent().getAbsoluteTop() - CP_MARGIN - offsetTop;

    connectionPointsPanel = new AbsolutePanel();

    this.add(connectionPointsPanel);		

    connectionPointsPanel.setPixelSize(
        connectedWidget.getOffsetWidth() + CP_MARGIN * 2 + offsetLeft, 
        connectedWidget.getOffsetHeight() + CP_MARGIN * 2 + offsetTop);
    connectionPointsPanel.add(connectedWidget, CP_MARGIN + offsetLeft, CP_MARGIN + offsetTop);


    //Add connection points to the absolute panel
    switch (cpShapeType) {
      case OVAL: 
        connectionPoints = createOvalShapeCP(connectionPointsPanel, diagram);
        break;
      case DIAMOND: 
        connectionPoints = createDiamondShapeCP(connectionPointsPanel, diagram);
        break;
      case RECTANGLE: 
        connectionPoints = createRectangleShapeCP(connectionPointsPanel, diagram);
        break;
      case USER_DEFINED:
        connectionPoints = createUserDefinedShapeCP(connectionPointsPanel, diagram);
        if (connectionPoints == null) {
          connectionPoints = createRectangleShapeCP(connectionPointsPanel, diagram);
        }
        break;
    }

    // Make Focus Panel draggable
    diagram.shapeDragController.makeDraggable(this);

    // Create drop controller
    shapeDropController = new DiagramWidgetDropController(this);

    diagram.endPointDragController.registerDropController(shapeDropController);
    diagram.shapeDragController.registerDropController(shapeDropController);

    sinkEvents(Event.ONMOUSEUP);
    sinkEvents(Event.ONCLICK);
    sinkEvents(Event.ONMOUSEDOWN);

  }

  public void repaint(Diagram diagram) {

    this.setPixelSize(
        connectedWidget.getOffsetWidth() + CP_MARGIN * 2 + offsetLeft, 
        connectedWidget.getOffsetHeight() + CP_MARGIN * 2 + offsetTop);

      connectionPointsPanel.setPixelSize(
          connectedWidget.getOffsetWidth() + CP_MARGIN * 2 + offsetLeft, 
          connectedWidget.getOffsetHeight() + CP_MARGIN * 2 + offsetTop);

      //refresh connection points positions
      int cpPanelHeight = connectionPointsPanel.getOffsetHeight();
      int cpPanelWidth = connectionPointsPanel.getOffsetWidth();

      int centerLeft = cpPanelWidth / 2 + offsetLeft;
      int centerTop = cpPanelHeight / 2 + offsetTop;

      switch (cpShapeType) {
        case DIAMOND:
          //counted from left
          int horizontalDifference = ((cpPanelWidth / 2) - ConnectionPoint.CPSize / 2) / 2;
          int verticalDifference = ((cpPanelHeight / 2) - ((ConnectionPoint.CPSize) / 2)) / 2;

          connectionPointsPanel.setWidgetPosition(connectionPoints.get(0), 0, (cpPanelHeight / 2) - ((ConnectionPoint.CPSize) / 2));
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(1), horizontalDifference, (cpPanelHeight / 2) - ((ConnectionPoint.CPSize) / 2) - verticalDifference);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(2), (cpPanelWidth / 2) - ConnectionPoint.CPSize / 2, 0);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(3), (cpPanelWidth / 2) - ConnectionPoint.CPSize / 2 + horizontalDifference, 0 + verticalDifference);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(4), cpPanelWidth - ConnectionPoint.CPSize, (cpPanelHeight / 2) - (ConnectionPoint.CPSize / 2));
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(5), cpPanelWidth - ConnectionPoint.CPSize - horizontalDifference, (cpPanelHeight / 2) - (ConnectionPoint.CPSize / 2) + verticalDifference);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(6), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2), cpPanelHeight - ConnectionPoint.CPSize);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(7), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2) - horizontalDifference, cpPanelHeight - ConnectionPoint.CPSize - verticalDifference);

          break;
        case OVAL:
          //counted from left
          cpPanelHeight = connectedWidget.getOffsetHeight() + CP_MARGIN * 2 - ConnectionPoint.CPSize;
          cpPanelWidth = connectedWidget.getOffsetWidth() + CP_MARGIN * 2 - ConnectionPoint.CPSize;

          int i = 0;
          for (i = 0 ; i < 8 ; i++) {
            connectionPointsPanel.add(connectionPoints.get(i),
                (int) (centerLeft - Math.floor((cpPanelWidth / 2) * Math.cos(2 * Math.PI / 8 * i))), 
                (int) (centerTop - Math.floor((cpPanelHeight / 2) * Math.sin(2 * Math.PI / 8 * i))));
          }
          break;
        case RECTANGLE:
          //counted from top-left

          connectionPointsPanel.setWidgetPosition(connectionPoints.get(0), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2) - (cpPanelWidth / 4), 0);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(1), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2), 0);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(2), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2) + (cpPanelWidth / 4), 0);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(3), cpPanelWidth - ConnectionPoint.CPSize, (cpPanelHeight / 2) - (ConnectionPoint.CPSize / 2) - (cpPanelHeight / 4));
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(4), cpPanelWidth - ConnectionPoint.CPSize, (cpPanelHeight / 2) - (ConnectionPoint.CPSize / 2));
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(5), cpPanelWidth - ConnectionPoint.CPSize, (cpPanelHeight / 2) - (ConnectionPoint.CPSize / 2) + (cpPanelHeight / 4));
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(6), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2) + (cpPanelWidth / 4), cpPanelHeight - ConnectionPoint.CPSize);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(7), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2), cpPanelHeight - ConnectionPoint.CPSize);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(8), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2) - (cpPanelWidth / 4), cpPanelHeight - ConnectionPoint.CPSize);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(9), 0, (cpPanelHeight / 2) - ConnectionPoint.CPSize / 2 + (cpPanelHeight / 4));
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(10), 0, (cpPanelHeight / 2) - ConnectionPoint.CPSize / 2);
          connectionPointsPanel.setWidgetPosition(connectionPoints.get(11), 0, (cpPanelHeight / 2) - ConnectionPoint.CPSize / 2 - (cpPanelHeight / 4));			

          break;
        case USER_DEFINED:
          refreshUserDefinedCPPositions(connectionPointsPanel, diagram);
          break;
      }
    
  }

  /**
   * Removes Shape from Diagram
   * 
   * @param  diagram  a Diagram the Shape will be removed from
   * @return the Shape removed from specified Diagram and from its boundaryPanel
   */
  public void removeFromDiagram(Diagram diagram) {
    try {
      // Remove Shape from Diagram
      diagram.endPointDragController.unregisterDropController(shapeDropController);
      diagram.shapeDragController.unregisterDropController(shapeDropController);
      diagram.shapeDragController.makeNotDraggable(this);
      //TODO Removed connectionPointDropController
      //		for (ConnectionPoint cp : connectionPoints) {
      //			diagram.endPointDragController.unregisterDropController(cp.connectionPointDropController);
      //		}
      diagram.shapes.remove(this);
      diagram.boundaryPanel.remove(this);

      List<Connector> connectors = getConnectedConnectors();
      for (Connector conn : connectors) {
        conn.removeFromDiagram(diagram);
      }
      // TODO Remove Shape from Diagram's boundaryPanel

    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Remove from diagram exception", e);
    }
  }

  public ConnectionPoint findNearestConnectionPoint(int absLeft, int absTop) {
    ConnectionPoint retCP = null;
    int distance = Integer.MAX_VALUE;
    for (ConnectionPoint cp : connectionPoints) {
      int tempDist = Math.abs(absLeft - (cp.getAbsoluteLeft() + cp.getOffsetWidth() / 2)) + Math.abs(absTop - (cp.getAbsoluteTop() + cp.getOffsetHeight() / 2));
      if (tempDist < distance) {
        distance = tempDist;
        retCP = cp;
      }
    }

    return retCP;
  }

  public ConnectionPoint findNearestFreeConnectionPoint(int absLeft, int absTop) {
    ConnectionPoint retCP = null;	
    int distance = Integer.MAX_VALUE;
    
    for (ConnectionPoint cp : connectionPoints) {
      int tempDist = Math.abs(absLeft - (cp.getAbsoluteLeft() + cp.getOffsetWidth() / 2)) + Math.abs(absTop - (cp.getAbsoluteTop() + cp.getOffsetHeight() / 2));
      if (tempDist < distance && cp.gluedEndPoints.size() == 0) {
        distance = tempDist;
        retCP = cp;
      }
    }
    return retCP;
  }

  private List<ConnectionPoint> createRectangleShapeCP(AbsolutePanel connectionPointsPanel, Diagram diagram) {
    List<ConnectionPoint> connectionPoints = new ArrayList<ConnectionPoint>();

    ConnectionPoint cp = new ConnectionPoint(ConnectionPoint.DIRECTION_TOP, 0, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_TOP, 1, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_TOP, 2, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_RIGHT, 3, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_RIGHT, 4, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_RIGHT, 5, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_BOTTOM, 6, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_BOTTOM, 7, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_BOTTOM, 8, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_LEFT, 9, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_LEFT, 10, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_LEFT, 11, connectedWidget);
    connectionPoints.add(cp);		

    for (ConnectionPoint connectionPoint : connectionPoints) {
      connectionPoint.showOnDiagram(diagram);
    }

    int cpPanelHeight = connectionPointsPanel.getOffsetHeight();
    int cpPanelWidth = connectionPointsPanel.getOffsetWidth();

    connectionPointsPanel.add(connectionPoints.get(0), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2) - (cpPanelWidth / 4), 0);
    connectionPointsPanel.add(connectionPoints.get(1), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2), 0);
    connectionPointsPanel.add(connectionPoints.get(2), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2) + (cpPanelWidth / 4), 0);
    connectionPointsPanel.add(connectionPoints.get(3), cpPanelWidth - ConnectionPoint.CPSize, (cpPanelHeight / 2) - (ConnectionPoint.CPSize / 2) - (cpPanelHeight / 4));
    connectionPointsPanel.add(connectionPoints.get(4), cpPanelWidth - ConnectionPoint.CPSize, (cpPanelHeight / 2) - (ConnectionPoint.CPSize / 2));
    connectionPointsPanel.add(connectionPoints.get(5), cpPanelWidth - ConnectionPoint.CPSize, (cpPanelHeight / 2) - (ConnectionPoint.CPSize / 2) + (cpPanelHeight / 4));
    connectionPointsPanel.add(connectionPoints.get(6), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2) + (cpPanelWidth / 4), cpPanelHeight - ConnectionPoint.CPSize);
    connectionPointsPanel.add(connectionPoints.get(7), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2), cpPanelHeight - ConnectionPoint.CPSize);
    connectionPointsPanel.add(connectionPoints.get(8), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2) - (cpPanelWidth / 4), cpPanelHeight - ConnectionPoint.CPSize);
    connectionPointsPanel.add(connectionPoints.get(9), 0, (cpPanelHeight / 2) - ConnectionPoint.CPSize / 2 + (cpPanelHeight / 4));
    connectionPointsPanel.add(connectionPoints.get(10), 0, (cpPanelHeight / 2) - ConnectionPoint.CPSize / 2);
    connectionPointsPanel.add(connectionPoints.get(11), 0, (cpPanelHeight / 2) - ConnectionPoint.CPSize / 2 - (cpPanelHeight / 4));			

    return connectionPoints;
  }

  private List<ConnectionPoint> createOvalShapeCP(AbsolutePanel connectionPointsPanel, Diagram diagram) {
    List<ConnectionPoint> connectionPoints = new ArrayList<ConnectionPoint>();

    ConnectionPoint cp = new ConnectionPoint(ConnectionPoint.DIRECTION_LEFT, 0, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_TOP, 1, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_TOP, 2, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_TOP, 3, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_RIGHT, 4, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_BOTTOM, 5, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_BOTTOM, 6, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_BOTTOM, 7, connectedWidget);
    connectionPoints.add(cp);

    for (ConnectionPoint connectionPoint : connectionPoints) {
      connectionPoint.showOnDiagram(diagram);
    }

    int cpPanelHeight = (int) Math.floor(connectedWidget.getOffsetHeight() + CP_MARGIN * 2 - ConnectionPoint.CPSize);
    int cpPanelWidth = (int) Math.floor(connectedWidget.getOffsetWidth() + CP_MARGIN * 2 - ConnectionPoint.CPSize);

    int centerLeft = (int) Math.floor( cpPanelWidth / 2 ) + offsetLeft;
    int centerTop = (int) Math.floor( cpPanelHeight / 2 ) + offsetTop;

    int i = 0;
    for (i = 0 ; i < 8 ; i++) {
      connectionPointsPanel.add(connectionPoints.get(i),
          (int) Math.floor(centerLeft - ((cpPanelWidth / 2) * Math.cos(2 * Math.PI / 8 * i))), 
          (int) Math.floor(centerTop - ((cpPanelHeight / 2) * Math.sin(2 * Math.PI / 8 * i))));
    }

    return connectionPoints;
  }

  private List<ConnectionPoint> createDiamondShapeCP(AbsolutePanel connectionPointsPanel, Diagram diagram) {
    List<ConnectionPoint> connectionPoints = new ArrayList<ConnectionPoint>();

    ConnectionPoint cp = new ConnectionPoint(ConnectionPoint.DIRECTION_LEFT, 0, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_TOP, 1, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_TOP, 2, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_TOP, 3, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_RIGHT, 4, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_BOTTOM, 5, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_BOTTOM, 6, connectedWidget);
    connectionPoints.add(cp);
    cp = new ConnectionPoint(ConnectionPoint.DIRECTION_BOTTOM, 7, connectedWidget);
    connectionPoints.add(cp);

    for (ConnectionPoint connectionPoint : connectionPoints) {
      connectionPoint.showOnDiagram(diagram);
    }

    int cpPanelHeight = connectionPointsPanel.getOffsetHeight();
    int cpPanelWidth = connectionPointsPanel.getOffsetWidth();
    int horizontalDifference = ((cpPanelWidth / 2) - ConnectionPoint.CPSize / 2) / 2;
    int verticalDifference = ((cpPanelHeight / 2) - ((ConnectionPoint.CPSize) / 2)) / 2;

    connectionPointsPanel.add(connectionPoints.get(0), 0, (cpPanelHeight / 2) - ((ConnectionPoint.CPSize) / 2));
    connectionPointsPanel.add(connectionPoints.get(1), horizontalDifference, (cpPanelHeight / 2) - ((ConnectionPoint.CPSize) / 2) - verticalDifference);
    connectionPointsPanel.add(connectionPoints.get(2), (cpPanelWidth / 2) - ConnectionPoint.CPSize / 2, 0);
    connectionPointsPanel.add(connectionPoints.get(3), (cpPanelWidth / 2) - ConnectionPoint.CPSize / 2 + horizontalDifference, 0 + verticalDifference);
    connectionPointsPanel.add(connectionPoints.get(4), cpPanelWidth - ConnectionPoint.CPSize, (cpPanelHeight / 2) - (ConnectionPoint.CPSize / 2));
    connectionPointsPanel.add(connectionPoints.get(5), cpPanelWidth - ConnectionPoint.CPSize - horizontalDifference, (cpPanelHeight / 2) - (ConnectionPoint.CPSize / 2) + verticalDifference);
    connectionPointsPanel.add(connectionPoints.get(6), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2), cpPanelHeight - ConnectionPoint.CPSize);
    connectionPointsPanel.add(connectionPoints.get(7), (cpPanelWidth / 2) - (ConnectionPoint.CPSize / 2) - horizontalDifference, cpPanelHeight - ConnectionPoint.CPSize - verticalDifference);

    return connectionPoints;
  }

  private List<ConnectionPoint> createUserDefinedShapeCP(AbsolutePanel connectionPointsPanel, Diagram diagram) {
    return null;
  }
  
  private void refreshUserDefinedCPPositions(AbsolutePanel connectionPointsPanel, Diagram diagram) {
  }

  public void hideConnectionPoints(Diagram diagram) {
    for (int i = 0; i < connectionPoints.size(); i++) {
      connectionPoints.get(i).setUnfocused();
    }
  }

  public void showConnectionPoints(Diagram diagram) {
    for (int i = 0; i < connectionPoints.size(); i++) {
      connectionPoints.get(i).setSelected();
    }	
  }

  public ConnectionPoint getCPForPosition(int cpPos) {
    for (ConnectionPoint cp : connectionPoints) {
      if (cp.position == cpPos) {
        return cp;
      }
    }
    return null;
  }

  /**
   * Returns shape left position on parent panel
   * @return widget left position on parent panel {@link AbsolutePanel}
   */
  public int getRelativeShapeLeft() {
    if (this.diagram != null) {
      return this.getAbsoluteLeft() - this.diagram.boundaryPanel.getAbsoluteLeft();
    } else {
      LOG.severe("getRelativeShapeLeft -> -1");
      return -1;
    }
  }

  /**
   * Returns shape top position on parent panel
   * @return widget top position on parent panel {@link AbsolutePanel}
   */
  public int getRelativeShapeTop() {
    if (this.diagram != null) {
      return this.getAbsoluteTop() - this.diagram.boundaryPanel.getAbsoluteTop();
    } else {
      LOG.severe("getRelativeShapeLeft -> -1");
      return -1;
    }
  }


  public Point getCPPosition(ConnectionPoint cp) {
    Point point = null;
    
    if (connectionPoints.contains(cp)) {
      if (this.getParent() != null) {
        int left = cp.getAbsoluteLeft() - this.diagram.boundaryPanel.getAbsoluteLeft();
        int top = cp.getAbsoluteTop() - this.diagram.boundaryPanel.getAbsoluteTop();
        point = new Point(left, top);
        return point;
      }
    }
    return null;
  }

  public List<Connector> getConnectedConnectors() {
    List<Connector> connectors = new ArrayList<Connector>();
    
    for (ConnectionPoint cp : connectionPoints) {
      for (EndPoint endPoint : cp.gluedEndPoints) {
        connectors.add(endPoint.connector);
      }
    }

    return connectors;
  }

  /**
   * Update sections connected to the Shape (should be used after Shape position changed)
   */
  public void updateConnectors() {
    for (ConnectionPoint cp : connectionPoints) {
      for (EndPoint ep : cp.gluedEndPoints) {

        boolean vertical = false;
        if (ep.connector.prevSectionForPoint(ep) != null) {
          vertical = ep.connector.prevSectionForPoint(ep).isVertical();
        } else {
          vertical = ep.connector.nextSectionForPoint(ep).isVertical();
        }
        ep.setLeft(cp.getCenterLeft());
        ep.setTop(cp.getCenterTop());
        if (vertical) {
          ep.updateOpositeEndPointOfVerticalSection();
        } else {
          ep.updateOpositeEndPointOfHorizontalSection();
        }
      }
    }
  }

  /**
   * Gives connector's sections that lay on this {@link Shape}
   * @param connector {@link Connector}
   * @return list of overlap sections
   */
  public List<Section> overlapSections(Connector connector) {
    List<Section> sections = new ArrayList<Section>();
    
    for (int i = 0 ; i < connector.sections.size() ; i++) {
      if (
          (connector.sections.get(i).connector.endEndPoint.isGluedToConnectionPoint() &&
              connector.sections.get(i).connector.endEndPoint.gluedConnectionPoint.getParentShape().equals(this)) 
              ||
              (connector.sections.get(i).connector.startEndPoint.isGluedToConnectionPoint() &&
                  connector.sections.get(i).connector.startEndPoint.gluedConnectionPoint.getParentShape().equals(this))
      ) { 
      } else if (isOnThisShape(connector.sections.get(i))) {
        sections.add(connector.sections.get(i));
      }
    }
    return sections;
  }

  public boolean goesThroughThisShape(Section sect) {
    boolean ret = true;
    LOG.info("Assuming, that section goes through this shape");
    return ret;
  }

  /**
   * Determines whether given {@link Section} is on this Shape's containing widget
   * @param startPoint
   * @param endPoint
   * @param direction
   * @return
   */
  public boolean isOnThisShape(Section section) {
    Point startPoint = section.startPoint;
    Point endPoint = section.endPoint;

    int direction;
    //		if (	section.connector.endEndPoint.isGluedToConnectionPoint() &&
    //				section.connector.endEndPoint.gluedConnectionPoint.getParentShape().equals(this)) {
    //			return false;
    //		}
    //		if (	section.connector.startEndPoint.isGluedToConnectionPoint() &&
    //				section.connector.startEndPoint.gluedConnectionPoint.getParentShape().equals(this)) {
    //			return false;
    //		}

    if (section.isHorizontal()) {
      direction = Section.HORIZONTAL;
    } else if (section.isVertical()) {
      direction = Section.VERTICAL;
    } else {
      LOG.severe("isOnThisShape -> direction = -1");
      direction = -1;
    }

    if (this.getParent() != null) {

      int shapeLeft = this.getRelativeShapeLeft();
      int shapeTop = this.getRelativeShapeTop();
      int shapeRight = shapeLeft + this.getOffsetWidth();
      int shapeBottom = shapeTop + this.getOffsetHeight();

      if (direction == Section.HORIZONTAL) {
        if (shapeTop <= startPoint.getTop() && shapeBottom >= startPoint.getTop()) {
          if ( 	(startPoint.getLeft() >= shapeLeft && startPoint.getLeft() <= shapeRight) ||
              (endPoint.getLeft() >= shapeLeft && endPoint.getLeft() <= shapeRight) ||
              (startPoint.getLeft() <= shapeLeft && endPoint.getLeft() >= shapeRight) ||
              (startPoint.getLeft() >= shapeLeft && endPoint.getLeft() <= shapeRight)
          ) {
            return true;
          }
        }
      } else if (direction == Section.VERTICAL){
        if (shapeLeft <= startPoint.getLeft() && shapeRight >= startPoint.getLeft()) {
          if ( 	(startPoint.getTop() >= shapeTop && startPoint.getTop() <= shapeBottom) ||
              (endPoint.getTop() >= shapeTop && endPoint.getTop() <= shapeBottom) ||
              (startPoint.getTop() <= shapeTop && endPoint.getTop() >= shapeBottom) ||
              (startPoint.getTop() >= shapeTop && endPoint.getTop() <= shapeBottom)
          ) {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * Define wheather given x and y coordinates lie on this {@link Shape} 
   * @param x 
   * @param y
   * @return
   */
  public boolean isOnShape(int x, int y) {
    if (this.getParent() != null) {
      int shapeLeft = this.getRelativeShapeLeft();
      int shapeTop = this.getRelativeShapeTop();
      int shapeRight = shapeLeft + this.getOffsetWidth();
      int shapeBottom = shapeTop + this.getOffsetHeight();
      if ( 	(shapeLeft <= x && shapeRight >= x) &&
          (shapeTop <= y && shapeBottom >= y)
      ) {
        return true;
      }
    } 
    return false;
  }

  public int getConnectedWidgetLeft() {
    if (this.isAttached()) {
      AbsolutePanel boundary = (AbsolutePanel) this.getParent();
      return connectedWidget.getAbsoluteLeft() - boundary.getAbsoluteLeft();
    } else {
      return -1;
    }
  }

  public int getConnectedWidgetTop() {
    if (this.isAttached()) {
      AbsolutePanel boundary = (AbsolutePanel) this.getParent();
      return connectedWidget.getAbsoluteTop() - boundary.getAbsoluteTop();
    } else {
      return -1;
    }
  }
  /**
   * Defines, wheather this {@link Shape} lie on selected rectangular
   * @param startSelectionPoint
   * @param endSelectionPoint
   * @return
   */
  public boolean isInRect(Point startSelectionPoint, Point endSelectionPoint) {
    boolean xBetween = false;
    boolean yBetween = false;
    xBetween = isNumberBetween(startSelectionPoint.getLeft(), endSelectionPoint.getLeft(), getRelativeShapeLeft()+getOffsetWidth()/2);
    yBetween = isNumberBetween(startSelectionPoint.getTop(), endSelectionPoint.getTop(), getRelativeShapeTop()+getOffsetHeight()/2);
    return (xBetween && yBetween);
  }
  
  private boolean isNumberBetween(int bound1, int bound2, int q){
    int min = 0;
    int max = 0;
    
    if (bound1 <= bound2) {
      min = bound1;
      max = bound2;
    } else {
      min = bound2;
      max = bound1;
    }
    
    if (q > min && q < max) {
      return true;
    }
    
    //Log.info(q+" is not between "+min+"-"+max);
    return false;
  }

  public int getTranslationX() {
    return translationX;
  }

  public void setTranslationX(int translationX) {
    this.translationX = translationX;
  }

  public int getTranslationY() {
    return translationY;
  }

  public void setTranslationY(int translationY) {
    this.translationY = translationY;
  }

  public void setEnableOverlap(boolean enableOverlap) {
    this.enableOverlap = enableOverlap;
  }

  public boolean isEnableOverlap() {
    return enableOverlap;
  }

  public void disableConnectors(boolean disable) {
    if (disable) {
      try {
        diagram.endPointDragController.unregisterDropController(shapeDropController);
        diagram.shapeDragController.unregisterDropController(shapeDropController);
      } catch (Exception e) {
        LOG.log(Level.SEVERE, "error while disable connectors for shape", e);
      }
    } else {
      diagram.endPointDragController.registerDropController(shapeDropController);
      diagram.shapeDragController.registerDropController(shapeDropController);
    }
  }

}
