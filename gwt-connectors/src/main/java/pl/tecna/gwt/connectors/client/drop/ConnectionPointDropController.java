package pl.tecna.gwt.connectors.client.drop;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.elements.EndPoint;
import pl.tecna.gwt.connectors.client.util.ConnectorsClientBundle;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;

public class ConnectionPointDropController extends SimpleDropController {

  public ConnectionPoint targetConnectionPoint;
  
  public ConnectionPointDropController(ConnectionPoint dropTarget) {
    super(dropTarget);
    targetConnectionPoint = dropTarget;
  }

  @Override
  public void onEnter(DragContext context) {
    targetConnectionPoint.setVisible();
    targetConnectionPoint.addStyleName(ConnectorsClientBundle.INSTANCE.css().gwtConnectorsShapeConnectorInnerDropOver());
  }
  
  @Override
  public void onLeave(DragContext context) {
    targetConnectionPoint.setTransparent();
    targetConnectionPoint.removeStyleName(ConnectorsClientBundle.INSTANCE.css().gwtConnectorsShapeConnectorInnerDropOver());
  }
  
  @Override
  public void onDrop(DragContext context) {
    EndPoint endPoint = (EndPoint) context.draggable;
    endPoint.glueToConnectionPoint(targetConnectionPoint);
  }
  
}
