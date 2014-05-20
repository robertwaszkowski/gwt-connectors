package pl.tecna.gwt.connectors.client.util;


public class BaseCoordinates implements Coordinate {

  protected int left;
  protected int top;

  public BaseCoordinates() {
    this.left = -1;
    this.top = -1;
  }

  public BaseCoordinates(int left, int top) {
    this.left = left;
    this.top = top;
  }

  @Override
  public int getLeft() {
    return left;
  }

  @Override
  public int getTop() {
    return top;
  }

  public void setLeft(int left) {
    this.left = left;
  }
  
  public void setTop(int top) {
    this.top = top;
  }
  
  @Override
  public String toString() {
    return "Position (left: " + left + " top: " + top + ")";
  }

  /**
   * Compares two points using their left and top values. <br>
   * If values are equal 0 is returned. If values are not eqal, sumary of absolute differences
   * between points is returned.
   */
  @Override
  public int compareTo(Coordinate coordinate) {
    return CoordinatesUtils.compare(this, coordinate);
  }

  @Override
  public void set(int left, int top) {
    this.left = left;
    this.top = top;
  }
  
}
