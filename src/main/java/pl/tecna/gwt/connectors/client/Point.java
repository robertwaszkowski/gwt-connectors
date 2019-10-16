package pl.tecna.gwt.connectors.client;

import com.google.gwt.user.client.ui.FocusPanel;

public class Point extends FocusPanel implements Comparable<Point> {

  protected Integer top;
  protected Integer left;

  public Point(Integer left, Integer top) {
    super();
    this.left = left;
    this.top = top;
  }

  public void showOnDiagram() {

  }
  
  public void initPosition(Integer left, Integer top) {
    this.left = left;
    this.top = top;
  }

  public void setLeftPosition(Integer left) { 
    setPosition(left, top);
  }
  
  public void setTopPosition(Integer top) { 
    setPosition(left, top);
  }
  
  public void setPosition(Integer left, Integer top) {
    this.left = left;
    this.top = top;
  }
  
  public Integer getTop() {
    return top;
  }
  
  public Integer getLeft() {
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
    int leftCompare = o.getLeft().compareTo(this.getLeft());
    int topCompare = o.getTop().compareTo(this.getTop());
    if (leftCompare == 0 && topCompare == 0) {
      return 0;
    } else {
      return Math.abs(leftCompare) + Math.abs(topCompare);
    }
  }

  @Override
  public String toString() {
    return "top:" + top + " left:" + left;
  }
  
}
