package pl.tecna.gwt.connectors.client.util;

public class Position {

  private double left;
  private double top;

  public Position() {

    this.left = -1;
    this.top = -1;
  }

  public Position(double left, double top) {

    this.left = left;
    this.top = top;
  }

  public void setLeft(double left) {
    this.left = left;
  }

  public double getLeft() {
    return left;
  }

  public void setTop(double top) {
    this.top = top;
  }

  public double getTop() {
    return top;
  }

}
