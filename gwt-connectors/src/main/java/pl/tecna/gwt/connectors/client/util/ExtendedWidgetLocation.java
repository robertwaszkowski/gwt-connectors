package pl.tecna.gwt.connectors.client.util;

import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.user.client.ui.Widget;

public class ExtendedWidgetLocation extends WidgetLocation {

  protected double height;
  protected double width;
  
  public ExtendedWidgetLocation(Widget widget, Widget reference) {
    super(widget, reference);
    height = widget.getOffsetHeight();
    width = widget.getOffsetWidth();
  }
  
  public double getCenterLeft() {
    return (double) getLeft() + width / 2.0;
  }

  public double getCenterTop() {
    return (double) getTop() + height / 2.0;
  }
}
