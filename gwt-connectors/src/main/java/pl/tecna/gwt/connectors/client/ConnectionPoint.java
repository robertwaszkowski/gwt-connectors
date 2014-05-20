package pl.tecna.gwt.connectors.client;

import java.util.ArrayList;
import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.EndPoint;
import pl.tecna.gwt.connectors.client.elements.Shape;
import pl.tecna.gwt.connectors.client.util.ConnectorsClientBundle;

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
  
	public ArrayList<EndPoint> gluedEndPoints;
	
	/**
	 * Connection point is on left/right/top/bottom edge of shape
	 */
	public int connectionDirection;
	public int index;
	public Widget parentWidget;
	public Point positionOnCPPanel;
	public Diagram diagram;
	
	public static final int ALL    =  0;
	public static final int DIRECTION_TOP    =  1;
	public static final int DIRECTION_RIGHT  =  2;
	public static final int DIRECTION_BOTTOM =  3;
	public static final int DIRECTION_LEFT   =  4;
	
	public static final int CPSize = 11;
	
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
	 */
	public ConnectionPoint() {
		super();
		gluedEndPoints = new ArrayList<EndPoint>();
		connectionDirection = ConnectionPoint.ALL;
		setTransparent();
		this.index = 1;
		this.getElement().getStyle().setZIndex(2);
	}
	
	public ConnectionPoint(int connectionDirection, int position, Widget w) {
		this();
		this.connectionDirection = connectionDirection;
		this.index = position;
		this.parentWidget = w;
	}
	
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
	 * Shows ConnectionPoint on a given panel. By default the ConnectionPoint
	 * is invisible. It can be visible when Shape is selected or ConnectionPoint
	 * is focused. 
	 * <p>
	 * This method also add a drop controller to the ConnectionPoint.
	 * Drop controller is necessary to allow ConnectionPoints to be glued
	 * with EndPoint.
	 *
	 * @param  panel  an absolute panel on witch the connection point will be drawn
	 * @return        the connection point drawn on specified panel
	 * 
	 * @author robert.waszkowski@gmail.com
	 */
	public void showOnDiagram(Diagram diagram) {
		this.diagram = diagram;
	}

	/**
	 * Changes ConnectionPoint's picture. The ConnectionPoint is 
	 * represented by a small x, which is visible when wrapped
	 * element is focused.
	 *
	 * @return  the ConnectionPoint's picture is changed to connection_point_selected.png
	 * 
	 * @author robert.waszkowski@gmail.com
	 */
	public void setVisible() {
	  this.removeStyleName(ConnectorsClientBundle.INSTANCE.css().gwtConnectorsShapeConnectorTransparent());
    this.addStyleName(ConnectorsClientBundle.INSTANCE.css().gwtConnectorsShapeConnectorInner());
	}

	/**
	 * Changes ConnectionPoint's picture. The ConnectionPoint is 
	 * represented by invisible element when ConnectionPoint
	 * is not focused and its wrapped element is not focused too.
	 *
	 * @return  the ConnectionPoint's picture is changed to connection_point.png
	 * 
	 * @author robert.waszkowski@gmail.com
	 */
	public void setTransparent() {
    this.addStyleName(ConnectorsClientBundle.INSTANCE.css().gwtConnectorsShapeConnectorTransparent());
    this.removeStyleName(ConnectorsClientBundle.INSTANCE.css().gwtConnectorsShapeConnectorInner());
	}

	public int getCurrentLeft() {
	  WidgetLocation currentLocation = new WidgetLocation(this, diagram.boundaryPanel);
	  return currentLocation.getLeft() - CPSize / 2;
	}
	
	public int getCurrentTop() {
    WidgetLocation currentLocation = new WidgetLocation(this, diagram.boundaryPanel);
    return currentLocation.getTop() - CPSize / 2;
  }
	
	/**
	 * Gets left coordinate of this {@link ConnectionPoint}'s center position on {@link AbsolutePanel} </br>
	 * Useful to define {@link Connector} end point left coordinate
	 * @return distance from left side of {@link AbsolutePanel} to this {@link ConnectionPoint} center
	 */
	public int getCenterLeft() {
	  WidgetLocation currentLocation = new WidgetLocation(this, diagram.boundaryPanel);
		int left;
		if (this.getParentShape().diagram != null) {
		  left = (int) Math.floor(currentLocation.getLeft() + ((double) CPSize / 2.0));
		  if (connectionDirection == DIRECTION_LEFT) {
		    left += 1;
		  }
		  return left;
		} else {
			return -1;
		}
	}
	
	/**
	 * Gets top coordinate of this {@link ConnectionPoint}'s center position on {@link AbsolutePanel} </br>
	 * Useful to define {@link Connector} end point top coordinate
	 * @return distance from top side of {@link AbsolutePanel} to this {@link ConnectionPoint} center
	 */
	public int getCenterTop() {
    WidgetLocation currentLocation = new WidgetLocation(this, diagram.boundaryPanel);
		int top;
		if (this.getParentShape().diagram != null) {
		  top = (int) Math.floor(currentLocation.getTop() + ((double) CPSize / 2.0));
			return top;
		} else {
			return -1;
		}
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

}
