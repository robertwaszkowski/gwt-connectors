package pl.tecna.gwt.connectors.client.util;

public interface Coordinate extends Comparable<Coordinate> {

  int getLeft();
  
  int getTop();
  
  void set(int left, int top);
  
}
