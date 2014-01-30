package pl.tecna.gwt.connectors.client.drag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.BoundaryDropController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.util.Area;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetArea;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * DragController used for drag-and-drop operations where a draggable widget or drag proxy is
 * temporarily picked up and dragged around the boundary panel. Be sure to register a
 * {@link DropController} for each drop target.
 * 
 * @see #registerDropController(DropController)
 */
public class AxisXYDragController extends AbstractDragController {

  class DropControllerCollection {
    protected class Candidate implements Comparable<Candidate> {

      private final DropController dropController;

      private final Area targetArea;

      Candidate(DropController dropController) {
        this.dropController = dropController;
        Widget target = dropController.getDropTarget();
        if (!target.isAttached()) {
          throw new IllegalStateException(
              "Unattached drop target. You must call DragController#unregisterDropController for all drop targets not attached to the DOM.");
        }
        targetArea = new WidgetArea(target, null);
      }

      public int compareTo(Candidate other) {
        Element myElement = getDropTarget().getElement();
        Element otherElement = other.getDropTarget().getElement();
        if (myElement == otherElement) {
          return 0;
        } else if (DOM.isOrHasChild(myElement, otherElement)) {
          return -1;
        } else if (DOM.isOrHasChild(otherElement, myElement)) {
          return 1;
        } else {
          return 0;
        }
      }

      @Override
      public boolean equals(Object other) {
        throw new RuntimeException("hash code not implemented");
      }

      @Override
      public int hashCode() {
        throw new RuntimeException("hash code not implemented");
      }

      DropController getDropController() {
        return dropController;
      }

      Widget getDropTarget() {
        return dropController.getDropTarget();
      }

      Area getTargetArea() {
        return targetArea;
      }
    }

    private final ArrayList<DropController> dropControllerList;

    private Candidate[] sortedCandidates = null;

    /**
     * Default constructor.
     */
    DropControllerCollection(ArrayList<DropController> dropControllerList) {
      this.dropControllerList = dropControllerList;
    }

    /**
     * Determines which DropController represents the deepest DOM descendant drop target located at
     * the provided location <code>(x, y)</code>.
     * 
     * @param x offset left relative to document body
     * @param y offset top relative to document body
     * @return a drop controller for the intersecting drop target or <code>null</code> if none are
     *         applicable
     */
    DropController getIntersectDropController(int x, int y) {
      Location location = new CoordinateLocation(x, y);
      for (int i = sortedCandidates.length - 1; i >= 0; i--) {
        Candidate candidate = sortedCandidates[i];
        Area targetArea = candidate.getTargetArea();
        if (targetArea.intersects(location)) {
          return candidate.getDropController();
        }
      }
      return null;
    }

    /**
     * Cache a list of eligible drop controllers, sorted by relative DOM positions of their
     * respective drop targets. Called at the beginning of each drag operation, or whenever drop
     * target eligibility has changed while dragging.
     * 
     * @param boundaryPanel boundary area for drop target eligibility considerations
     * @param context the current drag context
     */
    void resetCache(Panel boundaryPanel, DragContext context) {
      ArrayList<Candidate> list = new ArrayList<Candidate>();

      if (context.draggable != null) {
        WidgetArea boundaryArea = new WidgetArea(boundaryPanel, null);
        for (DropController dropController : dropControllerList) {
          Candidate candidate = new Candidate(dropController);
          if (DOM.isOrHasChild(context.draggable.getElement(), candidate.getDropTarget().getElement())) {
            continue;
          }
          if (candidate.getTargetArea().intersects(boundaryArea)) {
            list.add(candidate);
          }
        }
      }

      sortedCandidates = list.toArray(new Candidate[list.size()]);
      Arrays.sort(sortedCandidates);
    }
  }

  private static class SavedWidgetInfo {
    int initialDraggableIndex;
    String initialDraggableMargin;
    Widget initialDraggableParent;
    Location initialDraggableParentLocation;
  }

  /**
   * @deprecated Instead selectively use your own CSS classes.
   */
  protected static final String CSS_MOVABLE_PANEL;

  /**
   * @deprecated Instead selectively use your own CSS classes.
   */
  protected static final String CSS_PROXY;
  private static final String PRIVATE_CSS_MOVABLE_PANEL = "dragdrop-movable-panel";
  private static final String PRIVATE_CSS_PROXY = "dragdrop-proxy";

  static {
    CSS_MOVABLE_PANEL = PRIVATE_CSS_MOVABLE_PANEL;
    CSS_PROXY = PRIVATE_CSS_PROXY;
  }

  private BoundaryDropController boundaryDropController;
  protected int boundaryOffsetX;
  protected int boundaryOffsetY;
  private boolean dragProxyEnabled = false;
  private DropControllerCollection dropControllerCollection;
  @SuppressWarnings("rawtypes")
  private ArrayList dropControllerList = new ArrayList();
  protected int dropTargetClientHeight;
  protected int dropTargetClientWidth;
  protected Widget movablePanel;
  @SuppressWarnings("rawtypes")
  private HashMap savedWidgetInfoMap;

  // To provide XY drag feature (BEGIN)
  protected WidgetLocation initialDraggableLocation;
  private boolean allowHorizontalDragging;

  public boolean isAllowHorizontalDragging() {
    return allowHorizontalDragging;
  }

  public void setAllowHorizontalDragging(boolean allowHorizontalDragging) {
    this.allowHorizontalDragging = allowHorizontalDragging;
  }

  public boolean isAllowVerticalDragging() {
    return allowVerticalDragging;
  }

  public void setAllowVerticalDragging(boolean allowVerticalDragging) {
    this.allowVerticalDragging = allowVerticalDragging;
  }

  private boolean allowVerticalDragging;

  // To provide XY drag feature (END)

  // To provide XY drag feature (BEGIN)
  /**
   * Create a new pickup-and-move style drag controller. Allows widgets or a suitable proxy to be
   * temporarily picked up and moved around the specified boundary panel.
   * 
   * <p>
   * Note: An implicit {@link BoundaryDropController} is created and registered automatically.
   * </p>
   * 
   * @param boundaryPanel the desired boundary panel or <code>RootPanel.get()</code> if entire
   *          document body is to be the boundary
   * @param allowDroppingOnBoundaryPanel whether or not boundary panel should allow dropping
   * @param allowHorizontalDragging whether or not the Widget can be dragged horizontally
   * @param allowVerticalDragging whether or not the Widget can be dragged vertically
   */
  @SuppressWarnings("unchecked")
  public AxisXYDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel,
      boolean allowHorizontalDragging, boolean allowVerticalDragging) {
    super(boundaryPanel);
    this.allowHorizontalDragging = allowHorizontalDragging;
    this.allowVerticalDragging = allowVerticalDragging;
    assert boundaryPanel != null : "Use 'RootPanel.get()' instead of 'null'.";
    boundaryDropController = newBoundaryDropController(boundaryPanel, allowDroppingOnBoundaryPanel);
    registerDropController(boundaryDropController);
    dropControllerCollection = new DropControllerCollection(dropControllerList);
  }

  /**
   * Create a new pickup-and-move style drag controller. Allows widgets or a suitable proxy to be
   * temporarily picked up and moved around the specified boundary panel.
   * 
   * <p>
   * Note: An implicit {@link BoundaryDropController} is created and registered automatically.
   * </p>
   * 
   * @param boundaryPanel the desired boundary panel or <code>RootPanel.get()</code> if entire
   *          document body is to be the boundary
   * @param allowDroppingOnBoundaryPanel whether or not boundary panel should allow dropping
   */
  public AxisXYDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
    this(boundaryPanel, allowDroppingOnBoundaryPanel, true, true);
  }

  // To provide XY drag feature (END)

  public void dragEnd() {
    assert context.finalDropController == null == (context.vetoException != null);
    if (context.vetoException != null) {
      if (!getBehaviorDragProxy()) {
        restoreSelectedWidgetsLocation();
      }
    } else {
      context.dropController.onDrop(context);
    }
    context.dropController.onLeave(context);
    context.dropController = null;

    if (!getBehaviorDragProxy()) {
      restoreSelectedWidgetsStyle();
    }
    movablePanel.removeFromParent();
    movablePanel = null;
    super.dragEnd();
  }

  public void dragMove() {

    // To provide XY drag feature (BEGIN)
    if (allowHorizontalDragging == false) {
      context.desiredDraggableX = initialDraggableLocation.getLeft() + boundaryOffsetX;
    }
    if (allowVerticalDragging == false) {
      context.desiredDraggableY = initialDraggableLocation.getTop() + boundaryOffsetY;
    }
    // To provide XY drag feature (END)

    int desiredLeft = context.desiredDraggableX - boundaryOffsetX;
    int desiredTop = context.desiredDraggableY - boundaryOffsetY;

    if (getBehaviorConstrainedToBoundaryPanel()) {
      desiredLeft = Math.max(0, Math.min(desiredLeft, dropTargetClientWidth - context.draggable.getOffsetWidth()));
      desiredTop = Math.max(0, Math.min(desiredTop, dropTargetClientHeight - context.draggable.getOffsetHeight()));
    }

    DOMUtil.fastSetElementPosition(movablePanel.getElement(), desiredLeft, desiredTop);

    DropController newDropController = getIntersectDropController(context.mouseX, context.mouseY);
    if (context.dropController != newDropController) {
      if (context.dropController != null) {
        context.dropController.onLeave(context);
      }
      context.dropController = newDropController;
      if (context.dropController != null) {
        context.dropController.onEnter(context);
      }
    }

    if (context.dropController != null) {
      context.dropController.onMove(context);
    }
  }

  @SuppressWarnings("rawtypes")
  public void dragStart() {
    super.dragStart();

    WidgetLocation currentDraggableLocation = new WidgetLocation(context.draggable, context.boundaryPanel);

    // To provide XY drag feature (BEGIN)
    initialDraggableLocation = currentDraggableLocation;
    // To provide XY drag feature (END)

    if (getBehaviorDragProxy()) {
      movablePanel = newDragProxy(context);
      context.boundaryPanel.add(movablePanel, currentDraggableLocation.getLeft(), currentDraggableLocation.getTop());
    } else {
      saveSelectedWidgetsLocationAndStyle();
      AbsolutePanel container = new AbsolutePanel();
      DOM.setStyleAttribute(container.getElement(), "overflow", "visible");

      container.setPixelSize(context.draggable.getOffsetWidth(), context.draggable.getOffsetHeight());
      context.boundaryPanel.add(container, currentDraggableLocation.getLeft(), currentDraggableLocation.getTop());

      int draggableAbsoluteLeft = context.draggable.getAbsoluteLeft();
      int draggableAbsoluteTop = context.draggable.getAbsoluteTop();
      for (Iterator iterator = context.selectedWidgets.iterator(); iterator.hasNext();) {
        Widget widget = (Widget) iterator.next();
        if (widget != context.draggable) {
          int relativeX = widget.getAbsoluteLeft() - draggableAbsoluteLeft;
          int relativeY = widget.getAbsoluteTop() - draggableAbsoluteTop;
          container.add(widget, relativeX, relativeY);
        }
      }
      container.add(context.draggable, 0, 0);
      movablePanel = container;
    }
    movablePanel.addStyleName(PRIVATE_CSS_MOVABLE_PANEL);

    // one time calculation of boundary panel location for efficiency during dragging
    Location widgetLocation = new WidgetLocation(context.boundaryPanel, null);
    boundaryOffsetX = widgetLocation.getLeft() + DOMUtil.getBorderLeft(context.boundaryPanel.getElement());
    boundaryOffsetY = widgetLocation.getTop() + DOMUtil.getBorderTop(context.boundaryPanel.getElement());

    dropTargetClientWidth = DOMUtil.getClientWidth(context.boundaryPanel.getElement());
    dropTargetClientHeight = DOMUtil.getClientHeight(context.boundaryPanel.getElement());
  }

  /**
   * Whether or not dropping on the boundary panel is permitted.
   * 
   * @return <code>true</code> if dropping on the boundary panel is allowed
   */
  public boolean getBehaviorBoundaryPanelDrop() {
    return boundaryDropController.getBehaviorBoundaryPanelDrop();
  }

  /**
   * Determine whether or not this controller automatically creates a drag proxy for each drag
   * operation. Whether or not a drag proxy is used is ultimately determined by the return value of
   * {@link #maybeNewDraggableProxy(Widget)}
   * 
   * @return <code>true</code> if drag proxy behavior is enabled
   */
  public boolean getBehaviorDragProxy() {
    return dragProxyEnabled;
  }

  /**
   * @deprecated Use {@link #getBehaviorDragProxy()} instead.
   */
  public boolean isDragProxyEnabled() {
    return getBehaviorDragProxy();
  }

  public void previewDragEnd() throws VetoDragException {
    assert context.finalDropController == null;
    assert context.vetoException == null;
    // Does the DropController allow the drop?
    try {
      context.dropController.onPreviewDrop(context);
      context.finalDropController = context.dropController;
    } catch (VetoDragException ex) {
      context.finalDropController = null;
      throw ex;
    } finally {
      super.previewDragEnd();
    }
  }

  /**
   * Register a new DropController, representing a new drop target, with this drag controller.
   * 
   * @see #unregisterDropController(DropController)
   * 
   * @param dropController the controller to register
   */
  @SuppressWarnings("unchecked")
  public void registerDropController(DropController dropController) {
    dropControllerList.add(dropController);
  }

  public void resetCache() {
    super.resetCache();
    dropControllerCollection.resetCache(context.boundaryPanel, context);
  }

  /**
   * Set whether or not widgets may be dropped anywhere on the boundary panel. Set to
   * <code>false</code> when you only want explicitly registered drop controllers to accept drops.
   * Defaults to <code>true</code>.
   * 
   * @param allowDroppingOnBoundaryPanel <code>true</code> to allow dropping
   */
  public void setBehaviorBoundaryPanelDrop(boolean allowDroppingOnBoundaryPanel) {
    boundaryDropController.setBehaviorBoundaryPanelDrop(allowDroppingOnBoundaryPanel);
  }

  /**
   * Set whether or not this controller should automatically create a drag proxy for each drag
   * operation. Whether or not a drag proxy is used is ultimately determined by the return value of
   * {@link #maybeNewDraggableProxy(Widget)}.
   * 
   * @param dragProxyEnabled <code>true</code> to enable drag proxy behavior
   */
  public void setBehaviorDragProxy(boolean dragProxyEnabled) {
    this.dragProxyEnabled = dragProxyEnabled;
  }

  /**
   * @deprecated Use {@link #setBehaviorDragProxy(boolean)} instead.
   */
  public void setDragProxyEnabled(boolean dragProxyEnabled) {
    setBehaviorDragProxy(dragProxyEnabled);
  }

  /**
   * Unregister a DropController from this drag controller.
   * 
   * @see #registerDropController(DropController)
   * 
   * @param dropController the controller to register
   */
  public void unregisterDropController(DropController dropController) {
    dropControllerList.remove(dropController);
  }

  /**
   * @deprecated Use {@link #newDragProxy(DragContext)} and {@link #setBehaviorDragProxy(boolean)}
   *             instead.
   */
  protected final Widget maybeNewDraggableProxy(Widget draggable) {
    throw new UnsupportedOperationException();
  }

  /**
   * Create a new BoundaryDropController to manage our boundary panel as a drop target. To ensure
   * that draggable widgets can only be dropped on registered drop targets, set
   * <code>allowDroppingOnBoundaryPanel</code> to <code>false</code>.
   * 
   * @param boundaryPanel the panel to which our drag-and-drop operations are constrained
   * @param allowDroppingOnBoundaryPanel whether or not dropping is allowed on the boundary panel
   * @return the new BoundaryDropController
   */
  protected BoundaryDropController newBoundaryDropController(AbsolutePanel boundaryPanel,
      boolean allowDroppingOnBoundaryPanel) {
    return new BoundaryDropController(boundaryPanel, allowDroppingOnBoundaryPanel);
  }

  /**
   * Called by {@link PickupDragController#dragStart()} to allow subclasses to provide their
   * own drag proxies.
   * 
   * @param context the current drag context
   * @return a new drag proxy
   */
  @SuppressWarnings("rawtypes")
  protected Widget newDragProxy(DragContext context) {
    AbsolutePanel container = new AbsolutePanel();
    DOM.setStyleAttribute(container.getElement(), "overflow", "visible");

    WidgetArea draggableArea = new WidgetArea(context.draggable, null);
    for (Iterator iterator = context.selectedWidgets.iterator(); iterator.hasNext();) {
      Widget widget = (Widget) iterator.next();
      WidgetArea widgetArea = new WidgetArea(widget, null);
      Widget proxy = new SimplePanel();
      proxy.setPixelSize(widget.getOffsetWidth(), widget.getOffsetHeight());
      proxy.addStyleName(PRIVATE_CSS_PROXY);
      container
          .add(proxy, widgetArea.getLeft() - draggableArea.getLeft(), widgetArea.getTop() - draggableArea.getTop());
    }

    return container;
  }

  /**
   * Restore the selected widgets to their original location.
   * 
   * @see #saveSelectedWidgetsLocationAndStyle()
   * @see #restoreSelectedWidgetsStyle()
   */
  @SuppressWarnings({"rawtypes", "deprecation"})
  protected void restoreSelectedWidgetsLocation() {
    for (Iterator iterator = context.selectedWidgets.iterator(); iterator.hasNext();) {
      Widget widget = (Widget) iterator.next();
      SavedWidgetInfo info = (SavedWidgetInfo) savedWidgetInfoMap.get(widget);

      // TODO simplify after enhancement for issue 1112 provides InsertPanel interface
      // http://code.google.com/p/google-web-toolkit/issues/detail?id=1112
      if (info.initialDraggableParent instanceof AbsolutePanel) {
        ((AbsolutePanel) info.initialDraggableParent).add(widget, info.initialDraggableParentLocation.getLeft(),
            info.initialDraggableParentLocation.getTop());
      } else if (info.initialDraggableParent instanceof HorizontalPanel) {
        ((HorizontalPanel) info.initialDraggableParent).insert(widget, info.initialDraggableIndex);
      } else if (info.initialDraggableParent instanceof VerticalPanel) {
        ((VerticalPanel) info.initialDraggableParent).insert(widget, info.initialDraggableIndex);
      } else if (info.initialDraggableParent instanceof FlowPanel) {
        ((FlowPanel) info.initialDraggableParent).insert(widget, info.initialDraggableIndex);
      } else if (info.initialDraggableParent instanceof SimplePanel) {
        ((SimplePanel) info.initialDraggableParent).setWidget(widget);
      } else {
        throw new RuntimeException("Unable to handle initialDraggableParent "
            + GWT.getTypeName(info.initialDraggableParent));
      }
    }
  }

  /**
   * Restore the selected widgets with their original style.
   * 
   * @see #saveSelectedWidgetsLocationAndStyle()
   * @see #restoreSelectedWidgetsLocation()
   */
  @SuppressWarnings("rawtypes")
  protected void restoreSelectedWidgetsStyle() {
    for (Iterator iterator = context.selectedWidgets.iterator(); iterator.hasNext();) {
      Widget widget = (Widget) iterator.next();
      SavedWidgetInfo info = (SavedWidgetInfo) savedWidgetInfoMap.get(widget);
      DOM.setStyleAttribute(widget.getElement(), "margin", info.initialDraggableMargin);
    }
  }

  /**
   * Save the selected widgets' current location in case they much be restored due to a canceled
   * drop.
   * 
   * @see #restoreSelectedWidgetsLocation()
   */
  @SuppressWarnings({"rawtypes", "deprecation", "unchecked"})
  protected void saveSelectedWidgetsLocationAndStyle() {
    savedWidgetInfoMap = new HashMap();
    for (Iterator iterator = context.selectedWidgets.iterator(); iterator.hasNext();) {
      Widget widget = (Widget) iterator.next();

      SavedWidgetInfo info = new SavedWidgetInfo();
      info.initialDraggableParent = widget.getParent();

      // TODO simplify after enhancement for issue 1112 provides InsertPanel interface
      // http://code.google.com/p/google-web-toolkit/issues/detail?id=1112
      if (info.initialDraggableParent instanceof AbsolutePanel) {
        info.initialDraggableParentLocation = new WidgetLocation(widget, info.initialDraggableParent);
      } else if (info.initialDraggableParent instanceof HorizontalPanel) {
        info.initialDraggableIndex = ((HorizontalPanel) info.initialDraggableParent).getWidgetIndex(widget);
      } else if (info.initialDraggableParent instanceof VerticalPanel) {
        info.initialDraggableIndex = ((VerticalPanel) info.initialDraggableParent).getWidgetIndex(widget);
      } else if (info.initialDraggableParent instanceof FlowPanel) {
        info.initialDraggableIndex = ((FlowPanel) info.initialDraggableParent).getWidgetIndex(widget);
      } else if (info.initialDraggableParent instanceof SimplePanel) {
        // save nothing
      } else {
        throw new RuntimeException(
            "Unable to handle 'initialDraggableParent instanceof "
                + GWT.getTypeName(info.initialDraggableParent)
                + "'; Please create your own DragController and override saveDraggableLocationAndStyle() and restoreDraggableLocation()");
      }

      info.initialDraggableMargin = DOM.getStyleAttribute(widget.getElement(), "margin");
      DOM.setStyleAttribute(widget.getElement(), "margin", "0px");
      savedWidgetInfoMap.put(widget, info);
    }
  }

  protected DropController getIntersectDropController(int x, int y) {
    DropController dropController = dropControllerCollection.getIntersectDropController(x, y);
    return dropController != null ? dropController : boundaryDropController;
  }
}
