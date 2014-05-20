package pl.tecna.gwt.connectors.client.util;

public class CoordinatesUtils {

  public static int compare(Coordinate coord1, Coordinate coord2) {
    int leftCompare = coord1.getLeft() - coord2.getLeft();
    int topCompare = coord1.getTop() - coord2.getTop();
    if (leftCompare == 0 && topCompare == 0) {
      return 0;
    } else {
      return Math.abs(leftCompare) + Math.abs(topCompare);
    }
  }
  
}
