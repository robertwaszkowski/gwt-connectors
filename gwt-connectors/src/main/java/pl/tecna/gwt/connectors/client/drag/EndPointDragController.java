package pl.tecna.gwt.connectors.client.drag;

import java.util.ArrayList;
import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.EndPoint;
import pl.tecna.gwt.connectors.client.elements.Shape;
import pl.tecna.gwt.connectors.client.elements.ShapeConnectorStart;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class EndPointDragController extends PickupDragController {

  private final Logger LOG = Logger.getLogger("EndPointDragController");
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

    //create new connector for dragged ShapeConnectorStart
    if (context.draggable instanceof ShapeConnectorStart) {
      ShapeConnectorStart ep = (ShapeConnectorStart) context.draggable;
      if (ep.connector == null) {
        ep.shape.endPoints.remove(ep);
        ep.shape.hideShapeConnectorStartPionts();
        ep.removeHandlers();
        ep.removeStyle();
        if (ep.connector == null) {
          ep.setLeft(ep.getOverlapingCP().getCenterLeft());
          ep.setTop(ep.getOverlapingCP().getCenterTop());
          ep.connector = diagram.createConnector(
              ep.getOverlapingCP().getCenterLeft(), 
              ep.getOverlapingCP().getCenterTop(), 
              ep.getOverlapingCP().getCenterLeft(), 
              ep.getOverlapingCP().getCenterTop(), 
              ep);
          ep.connector.endPointDecoration = ep.shape.getEndDecoration();
          ep.connector.startPointDecoration = ep.shape.getStartDecoration();
        }
        ep.connector.startEndPoint.glueToConnectionPoint(ep.getOverlapingCP());
      }
    }
    super.previewDragStart();
  }

  @Override
  public void dragStart() {
    // remember Section orientations

    if (((((EndPoint) context.draggable).connector
        .findSectionWithThisEndPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
            .findSectionWithThisEndPoint((EndPoint) context.draggable).isHorizontal())))
            || ((((EndPoint) context.draggable).connector
                .findSectionWithThisStartPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
                    .findSectionWithThisStartPoint((EndPoint) context.draggable).isHorizontal())))) {
      sectionOrientation = HORIZONTAL;
    }
    if (((((EndPoint) context.draggable).connector
        .findSectionWithThisEndPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
            .findSectionWithThisEndPoint((EndPoint) context.draggable).isVertical())))
            || ((((EndPoint) context.draggable).connector
                .findSectionWithThisStartPoint((EndPoint) context.draggable) != null) && ((((EndPoint) context.draggable).connector
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
    draggedEP.setLeft(context.draggable.getAbsoluteLeft() + 6
        - context.boundaryPanel.getAbsoluteLeft());
    draggedEP.setTop(context.draggable.getAbsoluteTop() + 6
        - context.boundaryPanel.getAbsoluteTop());

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
        if (cp.gluedEndPoints != null && cp.gluedEndPoints.size() != 0 && !cp.equals(conn.startEndPoint.gluedConnectionPoint)) {
          excluded.add(cp);
        }
      }
      ConnectionPoint nearestCP = shape.findNearestConnectionPoint(dragEndPoint.getLeft(), dragEndPoint.getTop(), excluded);
      if (nearestCP != null && nearestCP != conn.startEndPoint.gluedConnectionPoint) {
        LOG.fine("Change connection to other ConnectionPoint");
        conn.startEndPoint.unglueFromConnectionPoint();
        conn.startEndPoint.glueToConnectionPoint(nearestCP);
        conn.startEndPoint.setLeft(nearestCP.getCenterLeft());
        conn.startEndPoint.setTop(nearestCP.getCenterTop());
      }
    }
    conn.calculateStandardPointsPositions();
    conn.drawSections();
  }

}
