package pl.tecna.gwt.connectors.client;

import com.google.gwt.user.client.ui.FocusPanel;

public class Point extends FocusPanel implements Comparable<Point> {

  protected double top;
  protected double left;

  public Point(double left, double top) {
    super();
    this.left = left;
    this.top = top;
  }

  public void showOnDiagram() {

  }
  
  public void initPosition(double left, double top) {
    this.left = left;
    this.top = top;
  }

  public void setLeftPosition(double left) { 
    setPosition(left, top);
  }
  
  public void setTopPosition(double top) { 
    setPosition(left, top);
  }
  
  public void setPosition(double left, double top) {
    this.left = left;
    this.top = top;
  }
  
  public double getTop() {
    return top;
  }
  
  public double getLeft() {
    return left;
  }

  public String toDebugString() {
    return "top:" + top + " left:" + left;
  }
  
  /**
   * Compares two points using their left and top values. <br>
   * If values are equal 0 is returned. If values are not eqal, sumary of absolute differences
   * between points is returned.
   */
  public int compareTo(Point o) {
    int leftCompare = Double.compare(o.getLeft(), this.getLeft());
    int topCompare = Double.compare(o.getTop(), this.getTop());
    if (leftCompare == 0 && topCompare == 0) {
      return 0;
    } else {
      return Math.abs(leftCompare) + Math.abs(topCompare);
    }
  }

}
