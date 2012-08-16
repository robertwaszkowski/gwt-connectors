package pl.tecna.gwt.connectors.client;

import java.util.ArrayList;

import pl.tecna.gwt.connectors.client.images.ConnectorsBundle;
import pl.tecna.gwt.connectors.client.util.Logger;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author robert.waszkowski@gmail.com
 *
 */
public class ConnectionPoint extends FocusPanel {
	
	@SuppressWarnings("unused")
	private final Logger LOG = new Logger("ConnectionPoint");

	public ArrayList<EndPoint> gluedEndPoints;
	public int connectionDirection;
	public int position;
	public Widget parentWidget;
	
	public static final int ALL    =  0;
	public static final int DIRECTION_TOP    =  1;
	public static final int DIRECTION_RIGHT  =  2;
	public static final int DIRECTION_BOTTOM =  3;
	public static final int DIRECTION_LEFT   =  4;
	
	public static final int CPSize = 13;
	
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
		
		this.setWidget(AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.connection_point()).createImage());
		//this.getElement().setClassName("x-unselectable");
		this.position = 1;
		this.getElement().getStyle().setZIndex(2);
	}
	
	public ConnectionPoint(int connectionDirection, int position, Widget w) {
		this();
		this.connectionDirection = connectionDirection;
		this.position = position;
		this.getElement().getStyle().setZIndex(2);
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
	public void showOnDiagram(final Diagram diagram) {
		
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
	public void setSelected() {
		this.setWidget(AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.connection_point_selected()).createImage());
	}

	/**
	 * Changes ConnectionPoint's picture. The ConnectionPoint is 
	 * represented by a small x, which is visible when ConnectionPoint
	 * is focused.
	 *
	 * @return  the ConnectionPoint's picture is changed to connection_point_focused.png
	 * 
	 * @author robert.waszkowski@gmail.com
	 */
	public void setFocused() { 
		this.setWidget(AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.connection_point_focused()).createImage());
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
	public void setUnfocused() {
		this.setWidget(AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.connection_point()).createImage());
		
	}
	/**
	 * Gets {@link ConnectionPoint}'s left position on {@link AbsolutePanel}
	 * @return distance from left side of {@link AbsolutePanel} to this {@link ConnectionPoint}
	 */
	public int getRelLeft() {
		if (this.getParentShape().diagram != null) {
			return this.getAbsoluteLeft() - this.getParentShape().diagram.boundaryPanel.getAbsoluteLeft();
		} else {
			return -1;
		}
	}
	
	/**
	 * Gets {@link ConnectionPoint}'s top position on {@link AbsolutePanel}
	 * @return distance from top side of {@link AbsolutePanel} to this {@link ConnectionPoint}
	 */
	public int getRelTop() {
		
		if (this.getParentShape().diagram != null) {
			return this.getAbsoluteTop() - this.getParentShape().diagram.boundaryPanel.getAbsoluteTop();
		} else {
			return -1;
		}
	}
	
	/**
	 * Gets left coordinate of this {@link ConnectionPoint}'s center position on {@link AbsolutePanel} </br>
	 * Useful to define {@link Connector} end point left coordinate
	 * @return distance from left side of {@link AbsolutePanel} to this {@link ConnectionPoint} center
	 */
	public int getCenterLeft() {
		
		if (this.getParentShape().diagram != null) {
			return (this.getAbsoluteLeft() - this.getParentShape().diagram.boundaryPanel.getAbsoluteLeft() + (int)Math.floor((double)((double)this.getOffsetWidth() / (double)2)));
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
		
		if (this.getParentShape().diagram != null) {
			return (this.getAbsoluteTop() - this.getParentShape().diagram.boundaryPanel.getAbsoluteTop() + (int)Math.floor((double)((double)this.getOffsetHeight() / (double)2)));
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
