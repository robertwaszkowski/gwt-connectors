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
      parent.setWidgetPosition(widget, left + (left - location.getLeft()), top + (top - location.getTop()));
      location = new WidgetLocation(widget, parent);
      if (location.getLeft() != left || location.getTop() != top) {
      if (location.getLeft() != left) {
        LOG.warning("Moved widget to wrong LEFT location (current: " + location.getLeft() + 
            " should be: " + left + ")");
      }
      if (location.getTop() != top) {
        LOG.warning("Moved widget to wrong TOP location (current: " + location.getTop() + 
            " should be: " + top + ")");
      }
      }
    }
  }

  public static void addWidget(AbsolutePanel parent, Widget widget, int left, int top) {
    parent.add(widget, left, top);
    WidgetLocation location = new WidgetLocation(widget, parent);
    if (location.getLeft() != left || location.getTop() != top) {
      parent.setWidgetPosition(widget, left + (left - location.getLeft()), top + (top - location.getTop()));
      location = new WidgetLocation(widget, parent);
      if (location.getLeft() != left || location.getTop() != top) {
        if (location.getLeft() != left) {
          LOG.warning("Added widget to wrong LEFT location (current: " + location.getLeft() + 
              " should be: " + left + ")");
        }
        if (location.getTop() != top) {
          LOG.warning("Added widget to wrong TOP location (current: " + location.getTop() + 
              " should be: " + top + ")");
        }
      }
    }
  }
  
}
