package pl.tecna.gwt.connectors.client.util;

import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.user.client.ui.Widget;

public class ExtendedWidgetLocation extends WidgetLocation {

  protected int height;
  protected int width;
  
  public ExtendedWidgetLocation(Widget widget, Widget reference) {
    super(widget, reference);
    height = widget.getOffsetHeight();
    width = widget.getOffsetWidth();
  }
  
  public int getCenterLeft() {
    return getLeft() + (int) Math.round(((double) width) / 2);
  }

  public int getCenterTop() {
    return getTop() + (int) Math.round(((double) height) / 2);
  }
}
