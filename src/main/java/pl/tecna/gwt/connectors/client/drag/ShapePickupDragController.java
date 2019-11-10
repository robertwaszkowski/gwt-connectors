package pl.tecna.gwt.connectors.client.drag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.EndPoint;
import pl.tecna.gwt.connectors.client.elements.Shape;
import pl.tecna.gwt.connectors.client.util.Position;

import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Pickup drag controller with list of draggable widgets.
 * 
 * @author Kamil
 * 
 */
public class ShapePickupDragController extends PickupDragController {

  private Logger LOG = Logger.getLogger("ShapePickupDragController");
  
  public List<Widget> dragableWidgets;
  private Diagram diagram;

  public ShapePickupDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel, Diagram diagram) {

    super(boundaryPanel, allowDroppingOnBoundaryPanel);
    dragableWidgets = new ArrayList<Widget>();
    this.diagram = diagram;

    addDragHandler(new DragHandlerAdapter() {

      @Override
      public void onPreviewDragStart(DragStartEvent event) {
        if (event.getContext().draggable instanceof Shape) {
          Shape shape = (Shape) event.getContext().draggable;
          shape.hideShapeConnectorStartPionts();
        }
      }
    });
  }

  private int startX = 0;
  private int startY = 0;

  // @override changes deselect style
  public void clearSelection() {

    diagram.deselectAllSections();
    for (Iterator<Widget> iterator = context.selectedWidgets.iterator(); iterator.hasNext();) {
      Widget widget = iterator.next();

      widget.addStyleName("gwt-connectors-shape-unselected");
      widget.removeStyleName("gwt-connectors-shape-selected");
      if (!(widget instanceof Shape)) {
        widget.addStyleName("gwt-connectors-widget-padding-unselected");
        widget.removeStyleName("gwt-connectors-widget-padding-selected");
      }
      iterator.remove();
    }
  }

  // @override, changes selection style
  public void toggleSelection(com.google.gwt.user.client.ui.Widget draggable) {

    if (!diagram.ctrlPressed) {
      diagram.deselectAllSections();
    }
    assert draggable != null;
    if (context.selectedWidgets.remove(draggable)) {
      draggable.addStyleName("gwt-connectors-shape-unselected");
      draggable.removeStyleName("gwt-connectors-shape-selected");
      if (!(draggable instanceof Shape)) {
        draggable.addStyleName("gwt-connectors-widget-padding-unselected");
        draggable.removeStyleName("gwt-connectors-widget-padding-selected");
      }
    } else {
      context.selectedWidgets.add(draggable);
      draggable.removeStyleName("gwt-connectors-shape-unselected");
      draggable.addStyleName("gwt-connectors-shape-selected");
      if (!(draggable instanceof Shape)) {
        draggable.removeStyleName("gwt-connectors-widget-padding-unselected");
        draggable.addStyleName("gwt-connectors-widget-padding-selected");
      }
    }

  };

  public void previewDragStart() throws VetoDragException {

    startX = diagram.boundaryPanel.getWidgetLeft(context.draggable) - diagram.boundaryPanel.getAbsoluteLeft();
    startY = diagram.boundaryPanel.getWidgetTop(context.draggable) - diagram.boundaryPanel.getAbsoluteTop();

    for (Widget widget : diagram.shapeDragController.getSelectedWidgets()) {
      if (widget instanceof Shape) {
        Shape shape = (Shape) widget;
        for (ConnectionPoint cp : shape.connectionPoints) {
          for (EndPoint ep : cp.gluedEndPoints) {
            ep.connector.rememberSectionsPositions();
          }
        }
      }
    }

    super.previewDragStart();
  };

  @Override
  public void dragMove() {
    // Update all glued connectors while dragging shape
    // Update glued end points positions and update connector
    //TODO: Refresh connections regarding to its endPoints positions
    //TODO: --------------------------------------------------------
    //TODO: poniżej jest rozbudowana wersja ze zmianą Connection Pointów przy obracaniu obiektów wokół siebie
    for (Widget widget : context.selectedWidgets) {
      if (widget instanceof Shape) {
        Shape shape = (Shape) widget;
        shape.setTranslationX(context.desiredDraggableX - startX - diagram.boundaryPanel.getAbsoluteLeft());
        shape.setTranslationY(context.desiredDraggableY - startY - diagram.boundaryPanel.getAbsoluteTop());
        for (ConnectionPoint cp : shape.connectionPoints) {
          for (EndPoint ep : cp.gluedEndPoints) {

                //moveEndPointGluedToCP(ep, cp);
//                ep.connector.recreateConnections(shape);
                ep.connector.recreateConnections();
              }
            }
          }
        }
    super.dragMove();
  }

//  @Override
//  public void dragMove() {
//    // Update all glued connectors while dragging shape
//    // Update glued end points positions and update connector
//    // TODO Refresh connections regarding to its endPoints positions
//    for (Widget widget : context.selectedWidgets) {
//      if (widget instanceof Shape) {
//        Shape shape = (Shape) widget;
//        shape.setTranslationX(context.desiredDraggableX - startX - diagram.boundaryPanel.getAbsoluteLeft());
//        shape.setTranslationY(context.desiredDraggableY - startY - diagram.boundaryPanel.getAbsoluteTop());
//        List<Connector> toRecreate = new LinkedList<Connector>();
//        for (ConnectionPoint cp : shape.connectionPoints) {
//          for (EndPoint ep : cp.gluedEndPoints) {
//            if (diagram.ctrlPressed) {
//              moveEndPointGluedToCP(ep, cp);
//            } else if (diagram.shiftPressed || (ep.connector.sections.size() <= 3 && !ep.connector.keepShape)) {
//              if (diagram.shiftPressed) {
//                ep.connector.keepShape = false;
//              }
//              toRecreate.add(ep.connector);
//            } else {
//              // moving multiple selected elements
//              if (ep.connector.startEndPoint.isGluedToConnectionPoint()
//                  && ep.connector.endEndPoint.isGluedToConnectionPoint()
//                  && context.selectedWidgets.contains(ep.connector.startEndPoint.gluedConnectionPoint.getParentShape())
//                  && context.selectedWidgets.contains(ep.connector.endEndPoint.gluedConnectionPoint.getParentShape())) {
//
//                moveEndPointGluedToCP(ep, cp);
//              } else {
//                // one element selected
//                boolean vertical = false;
//                if (ep.connector.prevSectionForPoint(ep) != null) {
//                  vertical = ep.connector.prevSectionForPoint(ep).isVertical();
//                } else {
//                  vertical = ep.connector.nextSectionForPoint(ep).isVertical();
//                }
//                ep.setPosition(cp.getConnectionPositionLeft(), cp.getConnectionPositionTop());
//
//                if (vertical) {
//                  ep.updateOpositeEndPointOfVerticalSection();
//                } else {
//                  ep.updateOpositeEndPointOfHorizontalSection();
//                }
//              }
//            }
//          }
//        }
//
//        if (toRecreate.size() > 0) {
//          for (Connector conn : toRecreate) {
//            recreateConnectios(conn);
//          }
//        }
//      }
//    }
//
//    super.dragMove();
//  }

  @Override
  public void makeDraggable(Widget draggable, Widget dragHandle) {

    if (!(draggable instanceof Shape)) {
      draggable.addStyleName("gwt-connectors-widget-padding-unselected");
    }
    dragableWidgets.add(draggable);
    super.makeDraggable(draggable, dragHandle);
  }

  @Override
  public void makeNotDraggable(Widget draggable) {

    dragableWidgets.remove(draggable);
    super.makeNotDraggable(draggable);
  }
  
  /*
   * Move End Point without change Connection Point's glue position
   */
  private void moveEndPointGluedToCP(EndPoint ep, ConnectionPoint cp) {
    ep.setPosition(cp.getConnectionPositionLeft(), cp.getConnectionPositionTop());
    ep.connector.calculateStandardPointsPositions();
    ep.connector.drawSections();
  }


//  private void recreateConnectios(Connector conn) {
//    Position startPosition;
//    Position endPosition;
//    if (conn.startEndPoint.isGluedToConnectionPoint()) {
//      startPosition = new Position(conn.startEndPoint.gluedConnectionPoint.getParentShape().getCenterLeft(),
//          conn.startEndPoint.gluedConnectionPoint.getParentShape().getCenterTop());
//    } else {
//      startPosition = new Position(conn.startEndPoint.getLeft(), conn.startEndPoint.getTop());
//    }
//
//    if (conn.endEndPoint.isGluedToConnectionPoint()) {
//      endPosition = new Position(conn.endEndPoint.gluedConnectionPoint.getParentShape().getCenterLeft(),
//          conn.endEndPoint.gluedConnectionPoint.getParentShape().getCenterTop());
//    } else {
//      endPosition = new Position(conn.endEndPoint.getLeft(), conn.endEndPoint.getTop());
//    }
//
//    if (conn.endEndPoint.isGluedToConnectionPoint()) {
//      ConnectionPoint nearestCP = conn.endEndPoint.gluedConnectionPoint.getParentShape().
//          findNearestConnectionPoint(startPosition.getLeft(), startPosition.getTop(), conn.startEndPoint.gluedConnectionPoint.connectionDirection ); //todo: !!! dodać atrybut kierunku (vertical, horizontal)
//      conn.endEndPoint.glueToConnectionPoint(nearestCP, false);
//      conn.endEndPoint.setLeftPosition(nearestCP.getConnectionPositionLeft());
//      conn.endEndPoint.setTopPosition(nearestCP.getConnectionPositionTop());
//
//      endPosition.setLeft(conn.endEndPoint.getLeft());
//      endPosition.setTop(conn.endEndPoint.getTop());
//    }
//    if (conn.startEndPoint.isGluedToConnectionPoint()) {
//      ConnectionPoint nearestCP = conn.startEndPoint.gluedConnectionPoint.getParentShape().
//          findNearestConnectionPoint(endPosition.getLeft(), endPosition.getTop(), conn.endEndPoint.gluedConnectionPoint.connectionDirection );
//      conn.startEndPoint.glueToConnectionPoint(nearestCP, false);
//      conn.startEndPoint.setLeftPosition(nearestCP.getConnectionPositionLeft());
//      conn.startEndPoint.setTopPosition(nearestCP.getConnectionPositionTop());
//
//      startPosition.setLeft(conn.startEndPoint.getLeft());
//      startPosition.setTop(conn.startEndPoint.getTop());
//    }
//    conn.calculateStandardPointsPositions();
//    conn.drawSections();
//  }
  
}
