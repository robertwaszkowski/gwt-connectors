package pl.tecna.gwt.connectors.client.drag;

import java.util.ArrayList;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.drop.ConnectionPointDropController;
import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.EndPoint;
import pl.tecna.gwt.connectors.client.elements.Section;
import pl.tecna.gwt.connectors.client.elements.Shape;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class EndPointDragController extends PickupDragController {
  
  int sectionOrientation; // 0 - vertical; 1 - horizontal
  private Diagram diagram;
  private int boundaryOffsetX;
  private int boundaryOffsetY;

  public EndPointDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel, Diagram diagram) {
    super(boundaryPanel, allowDroppingOnBoundaryPanel);
    this.diagram = diagram;
  }

  @Override
  public void previewDragStart() throws VetoDragException {
    EndPoint ep = (EndPoint) context.draggable;
    if (ep.connector == null && ep.gluedConnectionPoint != null) {
      diagram.clearSelection();
      ep.setConnectorEndPointStyle();
      ep.setPosition(getEndPointCenterLeft(ep), getEndPointCenterTop(ep));
      Shape shape = ep.gluedConnectionPoint.getParentShape();
      shape.endPoints.remove(ep);
      shape.hideShapeConnectorStartPionts();
      ep.disableConnectorCreate();
      if (ep.connector == null) {
        int startLeft = ep.gluedConnectionPoint.getConnectionPositionLeft();
        int startTop = ep.gluedConnectionPoint.getConnectionPositionTop();
        int endLeft = ep.getLeft();
        int endTop = ep.getTop();

        ep.connector = diagram.createConnector(startLeft, startTop, endLeft, endTop, ep, shape.connectorsStyle);
        ep.connector.initalizing = true;
      }
      ep.connector.startEndPoint.glueToConnectionPoint(ep.gluedConnectionPoint);

    } else if (ep.connector != null && ep.isGluedToConnectionPoint()) {
      ep.connector.initalizing = false;
      ep.unglueFromConnectionPoint();
      ep.setConnectorEndPointStyle();
    }
    super.previewDragStart();
  }

  @Override
  public void dragStart() {
    // remember Section orientations

    Location widgetLocation = new WidgetLocation(context.boundaryPanel, null);
    boundaryOffsetX = widgetLocation.getLeft() + DOMUtil.getBorderLeft(context.boundaryPanel.getElement());
    boundaryOffsetY = widgetLocation.getTop() + DOMUtil.getBorderTop(context.boundaryPanel.getElement());
    
    if (((((EndPoint) context.draggable).connector.findSectionWithThisEndPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
        .findSectionWithThisEndPoint((EndPoint) context.draggable).isHorizontal())))
        || ((((EndPoint) context.draggable).connector.findSectionWithThisStartPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
            .findSectionWithThisStartPoint((EndPoint) context.draggable).isHorizontal())))) {
      sectionOrientation = Section.HORIZONTAL;
    }
    if (((((EndPoint) context.draggable).connector.findSectionWithThisEndPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
        .findSectionWithThisEndPoint((EndPoint) context.draggable).isVertical())))
        || ((((EndPoint) context.draggable).connector.findSectionWithThisStartPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
            .findSectionWithThisStartPoint((EndPoint) context.draggable).isVertical())))) {
      sectionOrientation = Section.VERTICAL;
    }
    super.dragStart();
  }

  @Override
  public void dragMove() {
    EndPoint draggedEP = (EndPoint) context.draggable;
    draggedEP.connector.select();
    
    if (draggedEP.connector.sections.size() <= 3) {
      if (draggedEP.connector.startEndPoint.isGluedToConnectionPoint() && 
          context.dropController instanceof ConnectionPointDropController) {
        super.dragMove();
      }
    }
    
    int desiredLeft = getEndPointCenterLeft(draggedEP);
    int desiredTop = getEndPointCenterTop(draggedEP);
    if (diagram.drawInitializingConnectorsInLine && draggedEP.connector.initalizing) {
      EndPoint connectorStartPoint = draggedEP.connector.startEndPoint;
      switch (connectorStartPoint.gluedConnectionPoint.connectionDirection) {
        case DIRECTION_LEFT:
        case DIRECTION_RIGHT: {
          if (Math.abs(draggedEP.connector.startEndPoint.getTop() - 
              (context.desiredDraggableY - boundaryOffsetY)) < diagram.initialDragTolerance) {
            desiredTop = connectorStartPoint.getTop();
            context.desiredDraggableY = (int) Math.round(desiredTop + boundaryOffsetY - ConnectionPoint.RADIUS);
          }
        } break;
        case DIRECTION_TOP:
        case DIRECTION_BOTTOM: {
          if (Math.abs(draggedEP.connector.startEndPoint.getLeft() - 
              (context.desiredDraggableX - boundaryOffsetX)) < diagram.initialDragTolerance) {
            desiredLeft = connectorStartPoint.getLeft();
            context.desiredDraggableX = (int) Math.round(desiredLeft + boundaryOffsetX - ConnectionPoint.RADIUS);
          }
        } break;
      }
    }
    
    draggedEP.setPosition(desiredLeft, desiredTop);

    if (draggedEP.connector.sections.size() <= 3) {
      fixConnectorPath(draggedEP);
    } else {
      if (diagram.ctrlPressed) {
        fixConnectorPath(draggedEP);
      } else {
        if (sectionOrientation == Section.VERTICAL) {
          draggedEP.updateOpositeEndPointOfVerticalSection();
        } else if (sectionOrientation == Section.HORIZONTAL) {
          draggedEP.updateOpositeEndPointOfHorizontalSection();
        }
      }
    }
    super.dragMove();
  }

  public void fixConnectorPath(EndPoint dragEndPoint) {
    Connector conn = dragEndPoint.connector;
    if (conn.startEndPoint.isGluedToConnectionPoint()) {
      Shape shape = conn.startEndPoint.gluedConnectionPoint.getParentShape();
      ArrayList<ConnectionPoint> excluded = new ArrayList<ConnectionPoint>();
      for (ConnectionPoint cp : shape.connectionPoints) {
        if (cp.gluedEndPoints != null && cp.gluedEndPoints.size() != 0
            && !cp.equals(conn.startEndPoint.gluedConnectionPoint)) {
          excluded.add(cp);
        }
      }

      if (!diagram.altPressed) {
        ConnectionPoint nearestCP =
            shape.findNearestConnectionPoint(dragEndPoint.getLeft(), dragEndPoint.getTop(), excluded);
        if (nearestCP != null && nearestCP != conn.startEndPoint.gluedConnectionPoint) {
          conn.startEndPoint.glueToConnectionPoint(nearestCP);
          conn.startEndPoint.setPosition(nearestCP.getConnectionPositionLeft(), nearestCP.getConnectionPositionTop());
        }
      }
    }
    conn.calculateStandardPointsPositions();
    conn.drawSections();
  }

  private int getEndPointCenterLeft(EndPoint w) {
    return (w.getAbsoluteLeft() - context.boundaryPanel.getAbsoluteLeft() + (int) Math.floor((double) ((double) w
        .getOffsetWidth() / (double) 2)));
  }

  private int getEndPointCenterTop(EndPoint w) {
    return (w.getAbsoluteTop() - context.boundaryPanel.getAbsoluteTop() + (int) Math.floor((double) ((double) w
        .getOffsetHeight() / (double) 2)));
  }

}
