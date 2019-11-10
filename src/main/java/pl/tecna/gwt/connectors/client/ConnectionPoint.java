package pl.tecna.gwt.connectors.client;

import java.util.ArrayList;
import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.drop.ConnectionPointDropController;
import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.EndPoint;
import pl.tecna.gwt.connectors.client.elements.Shape;
import pl.tecna.gwt.connectors.client.util.WidgetUtils;

import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author robert.waszkowski@gmail.com
 *
 */
public class ConnectionPoint extends FocusPanel {

  Logger LOG = Logger.getLogger("ConnectionPoint");
  
  public static final int SIZE = 11;
  public static final int RADIUS = (int) Math.floor(SIZE / 2);
  
	public ArrayList<EndPoint> gluedEndPoints;
	
	/**
	 * Connection point is on left/right/top/bottom edge of shape
	 */
	public ConnectionDirection connectionDirection;
//todo remove:
	public ConnectionPointPosition position;
	public Widget parentWidget;
	
	/**
	 * Position on ConnectionPointsPanel where {@link Connector}'s {@link EndPoint} is connected
	 */
	public Point positionOnCPPanel;
	
	/**
	 * Position EndPoint center
	 */
	public Point endPointPosition;
	
	public Diagram diagram;
	private ConnectionPointDropController dropController;

	public enum ConnectionDirection {
		ALL 				{ @Override public boolean isVertical()   { return false; }
							  @Override public boolean isHorizontal() { return false; } },
		DIRECTION_TOP 		{ @Override public boolean isVertical()   { return true;  }
							  @Override public boolean isHorizontal() { return false; } },
		DIRECTION_RIGHT 	{ @Override public boolean isVertical()   { return false; }
							  @Override public boolean isHorizontal() { return true;  } },
		DIRECTION_BOTTOM 	{ @Override public boolean isVertical()   { return true;  }
							  @Override public boolean isHorizontal() { return false; } },
		DIRECTION_LEFT 		{ @Override public boolean isVertical()   { return false; }
							  @Override public boolean isHorizontal() { return true;  } };
		public abstract boolean isVertical();
		public abstract boolean isHorizontal();
	}

	//TODO: RW - nadać właściwą nazwę
	public enum ConnectionPointPosition {
		N   { @Override public boolean isMain() {return true; } }, /* 0/360 degrees */
		NNE { @Override public boolean isMain() {return false;} }, /*  22.5 degrees */
		NE  { @Override public boolean isMain() {return false;} }, /*  45   degrees */
		ENE { @Override public boolean isMain() {return false;} }, /*  67.5 degrees */
		E   { @Override public boolean isMain() {return true; } }, /*  90   degrees */
		ESE { @Override public boolean isMain() {return false;} }, /* 112.5 degrees */
		SE  { @Override public boolean isMain() {return false;} }, /* 135   degrees */
		SSE { @Override public boolean isMain() {return false;} }, /* 157.5 degrees */
		S   { @Override public boolean isMain() {return true; } }, /* 180   degrees */
		SSW { @Override public boolean isMain() {return false;} }, /* 202.5 degrees */
		SW  { @Override public boolean isMain() {return false;} }, /* 225   degrees */
		WSW { @Override public boolean isMain() {return false;} }, /* 247.5 degrees */
		W   { @Override public boolean isMain() {return true; } }, /* 270   degrees */
		WNW { @Override public boolean isMain() {return false;} }, /* 292.5 degrees */
		NW  { @Override public boolean isMain() {return false;} }, /* 315   degrees */
		NNW { @Override public boolean isMain() {return false;} }; /* 337.5 degrees */
		public abstract boolean isMain();
	}
	
	/**
	 * Any element which should be connected by {@link Connector}
	 * must be wrapped by a set of {@link ConnectionPoint}.
	 * {@link ConnectionPoint} is a place where {@link Connector}
	 * can be glued to.
	 * <p>
	 * {@link ConnectionPoint} is not visible until the wrapped
	 * element is focused. If wrapped element is focused all Connection Points 
	 * are visible. Connection Points are represented by small squares.
	 * <p>
	 * Connection Point have its own Drop Controller to allow dropping {@link EndPoint}
	 * on it to make it glued.
	 * 
	 * @param diagram the diagram
	 */
	public ConnectionPoint(Diagram diagram) {
		super();
		this.diagram = diagram;
		gluedEndPoints = new ArrayList<EndPoint>();
		connectionDirection = ConnectionDirection.ALL;
		setTransparent();
//todo remove:
		this.position = ConnectionPointPosition.N;
		this.getElement().getStyle().setZIndex(2);
		dropController = new ConnectionPointDropController(this);
	}

	public ConnectionPoint(Diagram diagram, ConnectionDirection connectionDirection, ConnectionPointPosition position, Widget w) {
//	public ConnectionPoint(Diagram diagram, ConnectionDirection connectionDirection, int position, Widget w) {
		this(diagram);
		this.connectionDirection = connectionDirection;
//todo remove:
		this.position = position;
		this.parentWidget = w;
	}

//TODO:	public ConnectionPoint(Diagram diagram, int connectionDirection, int position, Widget w, int connectionPointImportance) {
//		this(diagram);
//		this.connectionDirection = connectionDirection;
//		this.index = position;
//		this.parentWidget = w;
//	}
	
	/**
	 * 
	 * @param endPoint EndPoint the ConnectionPoint is to be glued to
	 */
	public void glueToEndPoint(EndPoint endPoint){
		gluedEndPoints.add(endPoint);
	}
	
	/**
	 * 
	 * @param endPoint EndPoint the ConnectionPoint is to be unglued from
	 */
	public void unglueFromEndPoint(EndPoint endPoint) {
		gluedEndPoints.remove(endPoint);
	}

	
	/**
	 * <p>Shows ConnectionPoint on a given panel. By default the ConnectionPoint
	 * is invisible. It can be visible when Shape is selected or ConnectionPoint
	 * is focused.</p> 
	 * <p>This method also add a drop controller to the ConnectionPoint.
	 * Drop controller is necessary to allow ConnectionPoints to be glued
	 * with EndPoint.</p>
	 *
	 * @param  panel  an absolute panel on witch the connection point will be drawn
	 * 
	 * @author robert.waszkowski@gmail.com
	 */
	public void showOnDiagram(AbsolutePanel panel) {
	  int left = endPointPosition.getLeft() - EndPoint.RADIUS;
	  int top = endPointPosition.getTop() - EndPoint.RADIUS;
    WidgetUtils.addWidget(panel, this, left, top);
	}

	/**
	 * <p>Changes ConnectionPoint's picture. The ConnectionPoint is represented by a small x, which is visible when wrapped
	 * element is focused.</p>
	 *
	 * <p>The ConnectionPoint's picture is changed to connection_point_selected.png</p>
	 * 
	 * @author robert.waszkowski@gmail.com
	 */
	public void setVisible() {
	  removeStyleName("gwt-connectors-shape-connector-transparent");
	  addStyleName("gwt-connectors-end-point-create-connector");
    diagram.endPointDragController.registerDropController(dropController);
	}

	/**
	 * <p>Changes ConnectionPoint's picture. The ConnectionPoint is represented by invisible element when ConnectionPoint
	 * is not focused and its wrapped element is not focused too.</p>
	 *
	 * <p>The ConnectionPoint's picture is changed to connection_point.png</p>
	 * 
	 * @author robert.waszkowski@gmail.com
	 */
	public void setTransparent() {
    addStyleName("gwt-connectors-shape-connector-transparent");
    removeStyleName("gwt-connectors-end-point-create-connector");
    diagram.endPointDragController.unregisterDropController(dropController);
	}

	public int getWidgetLeft() {
	  WidgetLocation currentLocation = new WidgetLocation(this, diagram.boundaryPanel);
	  return currentLocation.getLeft();
	}
	
	public int getWidgetTop() {
    WidgetLocation currentLocation = new WidgetLocation(this, diagram.boundaryPanel);
    return currentLocation.getTop();
  }
	
	/**
	 * Gets left coordinate of this {@link ConnectionPoint}'s connection position on {@link AbsolutePanel}.
	 * Useful to define {@link Connector} end point left coordinate.
	 * @return distance from left side of {@link AbsolutePanel} to this {@link ConnectionPoint} position
	 */
	public int getConnectionPositionLeft() {
	  WidgetLocation currentLocation = new WidgetLocation(this.getParent(), diagram.boundaryPanel);
    int left = currentLocation.getLeft() + positionOnCPPanel.getLeft();
    return left;
	}
	
	/**
	 * Gets top coordinate of this {@link ConnectionPoint}'s connection position on {@link AbsolutePanel}.
	 * Useful to define {@link Connector} end point top coordinate.
	 * @return distance from top side of {@link AbsolutePanel} to this {@link ConnectionPoint} position
	 */
	public int getConnectionPositionTop() {
	  WidgetLocation currentLocation = new WidgetLocation(this.getParent(), diagram.boundaryPanel);
	  int top = currentLocation.getTop() + positionOnCPPanel.getTop();
	  return top;
	}
	
	/**
	 * Gets {@link Shape} on which lies this {@link ConnectionPoint}
	 * @return parent {@link Shape}
	 */
	public Shape getParentShape() {
		return (Shape) this.getParent().getParent();
	}
	
	public Widget getParentWidget() {
	  return parentWidget;
	}
	
	public void unregisterDropController() {
    diagram.endPointDragController.unregisterDropController(dropController);
	}

}
