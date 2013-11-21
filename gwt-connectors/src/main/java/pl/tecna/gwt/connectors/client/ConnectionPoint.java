package pl.tecna.gwt.connectors.client;

import java.util.ArrayList;

import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.EndPoint;
import pl.tecna.gwt.connectors.client.elements.Shape;
import pl.tecna.gwt.connectors.client.images.ConnectorsBundle;
import pl.tecna.gwt.connectors.client.util.ConnectorsClientBundle;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author robert.waszkowski@gmail.com
 * 
 */
public class ConnectionPoint extends FocusPanel {

  public static final int ALL = 0;
  public static final int DIRECTION_TOP = 1;
  public static final int DIRECTION_RIGHT = 2;
  public static final int DIRECTION_BOTTOM = 3;
  public static final int DIRECTION_LEFT = 4;

  public static final int CPSize = 13;

  public ArrayList<EndPoint> gluedEndPoints;
  public int connectionDirection;
  public int position;
  public Widget parentWidget;

  /**
   * Any element which should be connected by {@link Connector} must be wrapped by a set of
   * {@link ConnectionPoint}. {@link ConnectionPoint} is a place where {@link Connector} can be
   * glued to.
   * <p>
   * {@link ConnectionPoint} is not visible until the wrapped element is focused. If wrapped element
   * is focused all Connection Points are visible. Connection Points are represented by small
   * squares.
   * <p>
   * Connection Point have its own Drop Controller to allow dropping {@link EndPoint} on it to make
   * it glued.
   */
  public ConnectionPoint() {
    super();
    gluedEndPoints = new ArrayList<EndPoint>();
    connectionDirection = ConnectionPoint.ALL;

    Image img = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.connection_point()).createImage();
    img.addStyleName(ConnectorsClientBundle.INSTANCE.css().imageDispBlock());
    this.setWidget(img);
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
  public void glueToEndPoint(EndPoint endPoint) {
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
   * Shows ConnectionPoint on a given panel. By default the ConnectionPoint is invisible. It can be
   * visible when Shape is selected or ConnectionPoint is focused.
   * <p>
   * This method also add a drop controller to the ConnectionPoint. Drop controller is necessary to
   * allow ConnectionPoints to be glued with EndPoint.
   * 
   * @param diagram an absolute panel on witch the connection point will be drawn
   */
  public void showOnDiagram(Diagram diagram) {
  }

  /**
   * Changes ConnectionPoint's picture. The ConnectionPoint is represented by a small x, which is
   * visible when wrapped element is focused. The ConnectionPoint's picture is changed to
   * connection_point_selected.png.
   */
  public void setSelected() {
    Image img = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.connection_point_selected()).createImage();
    img.addStyleName(ConnectorsClientBundle.INSTANCE.css().imageDispBlock());
    this.setWidget(img);
  }

  /**
   * Changes ConnectionPoint's picture. The ConnectionPoint is represented by a small x, which is
   * visible when ConnectionPoint is focused. The ConnectionPoint's picture is changed to
   * connection_point_focused.png.
   */
  public void setFocused() {
    Image img = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.connection_point_focused()).createImage();
    img.addStyleName(ConnectorsClientBundle.INSTANCE.css().imageDispBlock());
    this.setWidget(img);
  }

  /**
   * Changes ConnectionPoint's picture. The ConnectionPoint is represented by invisible element when
   * ConnectionPoint is not focused and its wrapped element is not focused too. The
   * ConnectionPoint's picture is changed to connection_point.png.
   */
  public void setUnfocused() {
    Image img = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.connection_point()).createImage();
    img.addStyleName(ConnectorsClientBundle.INSTANCE.css().imageDispBlock());
    this.setWidget(img);
  }

  /**
   * Gets left coordinate of this {@link ConnectionPoint}'s center position on {@link AbsolutePanel}
   * </br> Useful to define {@link Connector} end point left coordinate
   * 
   * @return distance from left side of {@link AbsolutePanel} to this {@link ConnectionPoint} center
   */
  public int getCenterLeft() {
    int left;
    if (this.getParentShape().diagram != null) {
      left =
          (this.getAbsoluteLeft() - this.getParentShape().diagram.boundaryPanel.getAbsoluteLeft() + (int) Math
              .floor((double) ((double) this.getOffsetWidth() / (double) 2)));
      return left;
    } else {
      return -1;
    }
  }

  /**
   * Gets top coordinate of this {@link ConnectionPoint}'s center position on {@link AbsolutePanel}
   * </br> Useful to define {@link Connector} end point top coordinate
   * 
   * @return distance from top side of {@link AbsolutePanel} to this {@link ConnectionPoint} center
   */
  public int getCenterTop() {
    int top;
    if (this.getParentShape().diagram != null) {
      top =
          (this.getAbsoluteTop() - this.getParentShape().diagram.boundaryPanel.getAbsoluteTop() + (int) Math
              .floor((double) ((double) this.getOffsetHeight() / (double) 2)));
      if (connectionDirection == DIRECTION_TOP) {
        top -= 1;
      } else if (connectionDirection == DIRECTION_BOTTOM) {
        top += 1;
      }
      return top;
    } else {
      return -1;
    }
  }

  /**
   * Gets {@link Shape} on which lies this {@link ConnectionPoint}
   * 
   * @return parent {@link Shape}
   */
  public Shape getParentShape() {
    return (Shape) this.getParent().getParent();
  }

  public Widget getParentWidget() {
    return parentWidget;
  }
}
