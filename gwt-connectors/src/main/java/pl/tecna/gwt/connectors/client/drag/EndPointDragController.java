package pl.tecna.gwt.connectors.client.drag;

import java.util.ArrayList;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.EndPoint;
import pl.tecna.gwt.connectors.client.elements.Shape;
import pl.tecna.gwt.connectors.client.elements.ShapeConnectorStart;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class EndPointDragController extends PickupDragController {

  final int VERTICAL = 0;
  final int HORIZONTAL = 1;
  int sectionOrientation; // 0 - vertical; 1 - horizontal
  private Diagram diagram;

  public EndPointDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel, Diagram diagram) {
    super(boundaryPanel, allowDroppingOnBoundaryPanel);
    this.diagram = diagram;
  }

  @Override
  public void previewDragStart() throws VetoDragException {

    // create new connector for dragged ShapeConnectorStart
    if (context.draggable instanceof ShapeConnectorStart) {
      diagram.clearSelection();
      ShapeConnectorStart ep = (ShapeConnectorStart) context.draggable;
      ep.setWidget(ep.createEndPointImage());
      ep.setLeft(getDraggableCenterLeft(ep));
      ep.setTop(getDraggableCenterTop(ep));

      if (ep.connector == null) {
        ep.shape.endPoints.remove(ep);
        ep.shape.hideShapeConnectorStartPionts();
        ep.removeHandlers();
        ep.removeStyle();
        DOM.setStyleAttribute(ep.getElement(), "cursor", "crosshair");
        if (ep.connector == null) {
          int startLeft = ep.getOverlapingCP().getCenterLeft();
          int startTop = ep.getOverlapingCP().getCenterTop();
          int endLeft = ep.getLeft();
          int endTop = ep.getTop();

          ep.connector = diagram.createConnector(startLeft, startTop, endLeft, endTop, ep, ep.shape.connectorsStyle);
        }
        ep.connector.startEndPoint.glueToConnectionPoint(ep.getOverlapingCP());
      }
    }
    super.previewDragStart();
  }

  @Override
  public void dragStart() {
    // remember Section orientations

    if (((((EndPoint) context.draggable).connector.findSectionWithThisEndPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
        .findSectionWithThisEndPoint((EndPoint) context.draggable).isHorizontal())))
        || ((((EndPoint) context.draggable).connector.findSectionWithThisStartPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
            .findSectionWithThisStartPoint((EndPoint) context.draggable).isHorizontal())))) {
      sectionOrientation = HORIZONTAL;
    }
    if (((((EndPoint) context.draggable).connector.findSectionWithThisEndPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
        .findSectionWithThisEndPoint((EndPoint) context.draggable).isVertical())))
        || ((((EndPoint) context.draggable).connector.findSectionWithThisStartPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
            .findSectionWithThisStartPoint((EndPoint) context.draggable).isVertical())))) {
      sectionOrientation = VERTICAL;
    }
    super.dragStart();
  }

  @Override
  public void dragMove() {

    EndPoint draggedEP = (EndPoint) context.draggable;
    draggedEP.connector.select();
    // Update left and top position for dragged EndPoint
    draggedEP.setLeft(getDraggableCenterLeft(draggedEP));
    draggedEP.setTop(getDraggableCenterTop(draggedEP));

    if (draggedEP.connector.sections.size() <= 3) {
      fixConnectorPath(draggedEP);
    } else {
      if (diagram.ctrlPressed) {
        fixConnectorPath(draggedEP);
      } else {
        if (sectionOrientation == VERTICAL) {
          draggedEP.updateOpositeEndPointOfVerticalSection();
        } else if (sectionOrientation == HORIZONTAL) {
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
          conn.startEndPoint.unglueFromConnectionPoint();
          conn.startEndPoint.glueToConnectionPoint(nearestCP);
          conn.startEndPoint.setLeft(nearestCP.getCenterLeft());
          conn.startEndPoint.setTop(nearestCP.getCenterTop());
        }
      }
    }
    conn.calculateStandardPointsPositions();
    conn.drawSections();
  }

  private int getDraggableCenterLeft(EndPoint w) {
    return (w.getAbsoluteLeft() - context.boundaryPanel.getAbsoluteLeft() + (int) Math.floor((double) ((double) w
        .getOffsetWidth() / (double) 2)));
  }

  private int getDraggableCenterTop(EndPoint w) {
    return (w.getAbsoluteTop() - context.boundaryPanel.getAbsoluteTop() + (int) Math.floor((double) ((double) w
        .getOffsetHeight() / (double) 2)));
  }

}
