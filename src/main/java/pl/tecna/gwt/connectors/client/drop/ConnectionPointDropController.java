package pl.tecna.gwt.connectors.client.drop;

import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.elements.EndPoint;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;

public class ConnectionPointDropController extends SimpleDropController {

  private static final Logger LOG = Logger.getLogger("ConnectionPointDropController");
  
  public ConnectionPoint targetConnectionPoint;
  
  public ConnectionPointDropController(ConnectionPoint dropTarget) {
    super(dropTarget);
    targetConnectionPoint = dropTarget;
  }

  @Override
  public void onEnter(DragContext context) {
    targetConnectionPoint.setVisible();
    targetConnectionPoint.addStyleName("gwt-connectors-shape-connector-inner-drop-over");
    if (context.draggable instanceof EndPoint) {
      EndPoint draggedEP = (EndPoint) context.draggable;
      if (draggedEP.connector.sections.size() <= 3) {
        if (draggedEP.connector.startEndPoint.isGluedToConnectionPoint() && 
            context.dropController instanceof ConnectionPointDropController) {
          ConnectionPoint target = ((ConnectionPointDropController) context.dropController).targetConnectionPoint;
          draggedEP.gluedConnectionPoint = target;
          draggedEP.setGluedToConnectionPoint(true);
          draggedEP.setPosition(targetConnectionPoint.getConnectionPositionLeft(), targetConnectionPoint.getConnectionPositionTop());
          draggedEP.connector.calculateStandardPointsPositions(
              draggedEP.connector.startEndPoint, 
              draggedEP);
          draggedEP.connector.drawSections();
        }
      }
    }
  }
  
  @Override
  public void onLeave(DragContext context) {
    targetConnectionPoint.setTransparent();
    targetConnectionPoint.removeStyleName("gwt-connectors-shape-connector-inner-drop-over");
    if (context.finalDropController == null) {
      if (context.draggable instanceof EndPoint) {
        EndPoint draggedEP = (EndPoint) context.draggable;
        draggedEP.unglueFromConnectionPoint();
      }
    }
  }
  
  @Override
  public void onDrop(DragContext context) {
    EndPoint endPoint = (EndPoint) context.draggable;
    endPoint.glueToConnectionPoint(targetConnectionPoint);
    endPoint.connector.fixEndSectionDirection(endPoint);
  }
  
}
