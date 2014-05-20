package pl.tecna.gwt.connectors.client.drop;

import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.EndPoint;
import pl.tecna.gwt.connectors.client.elements.Shape;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.Widget;

public class DiagramWidgetDropController extends SimpleDropController {

  public DiagramWidgetDropController(Widget dropTarget) {
    super(dropTarget);
  }

  @Override
  public void onEnter(DragContext context) {
    if (context.draggable instanceof EndPoint) {
      if (getDropTarget() instanceof Shape) {
        Shape dropTarget = (Shape) getDropTarget();
        EndPoint ep = (EndPoint) context.draggable;
        if (ep.connector.startEndPoint.isGluedToConnectionPoint()
            && ep.connector.startEndPoint.gluedConnectionPoint.getParentShape().equals(dropTarget)) {
        } else {
          dropTarget.showConnectionPoints(dropTarget.diagram);
        }
      }
    }
    super.onEnter(context);
  }

  @Override
  public void onLeave(DragContext context) {
    if (context.draggable instanceof EndPoint) {
      if (getDropTarget() instanceof Shape) {
        Shape dropTarget = (Shape) getDropTarget();
        dropTarget.hideConnectionPoints(dropTarget.diagram);
      }
    }
    super.onLeave(context);
  }

  @Override
  public void onDrop(DragContext context) {
    if (context.draggable instanceof EndPoint) {
      // Glue end point to the nearest connection point
      if (getDropTarget() instanceof Shape) {
        Shape dropTarget = (Shape) getDropTarget();
        EndPoint endPoint = (EndPoint) context.draggable;
        endPoint
            .glueToConnectionPoint(dropTarget.findNearestFreeConnectionPoint(endPoint.getLeft(), endPoint.getTop()));

        // Unglue if EndPoints are glued to the same element
        Connector conn = endPoint.connector;
        if (conn.startEndPoint.isGluedToConnectionPoint() && conn.endEndPoint.isGluedToConnectionPoint()) {
          if (conn.endEndPoint.gluedConnectionPoint.getParentWidget() == conn.startEndPoint.gluedConnectionPoint
              .getParentWidget()) {
            if (conn.endEndPoint == endPoint) {
              conn.disconnectEnd();
            } else
              conn.disconnectStart();
          }
        }
      }
    }
    super.onDrop(context);
  }

  @Override
  public void onPreviewDrop(DragContext context) throws VetoDragException {

    if (!(context.draggable instanceof EndPoint)) {
      throw new VetoDragException();
    }
    super.onPreviewDrop(context);
  }
}
