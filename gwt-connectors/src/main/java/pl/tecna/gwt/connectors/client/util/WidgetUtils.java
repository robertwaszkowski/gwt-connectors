package pl.tecna.gwt.connectors.client.util;

import java.util.logging.Logger;

import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class WidgetUtils {

  private static final Logger LOG = Logger.getLogger("WidgetUtils");
  
  public static void setWidgetPosition(AbsolutePanel parent, Widget widget, int left, int top) {
    parent.setWidgetPosition(widget, left, top);
    WidgetLocation location = new WidgetLocation(widget, parent);
    if (location.getLeft() != left || location.getTop() != top) {
      LOG.warning("Widget position wrong set (offset left: " + (left - location.getLeft()) + 
          " offset top: " + (top - location.getTop()) + ")");
      parent.setWidgetPosition(widget, left + (left - location.getLeft()), top + (top - location.getTop()));
    }
  }
  
}
