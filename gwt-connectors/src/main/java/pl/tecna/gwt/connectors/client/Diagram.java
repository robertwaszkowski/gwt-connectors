package pl.tecna.gwt.connectors.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.drag.EndPointDragController;
import pl.tecna.gwt.connectors.client.drag.ShapePickupDragController;
import pl.tecna.gwt.connectors.client.drop.ConnectionPointDropController;
import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.EndPoint;
import pl.tecna.gwt.connectors.client.elements.Section;
import pl.tecna.gwt.connectors.client.elements.SectionDecoration;
import pl.tecna.gwt.connectors.client.elements.SectionDecoration.DecorationType;
import pl.tecna.gwt.connectors.client.elements.Shape;
import pl.tecna.gwt.connectors.client.listeners.DiagramListener;
import pl.tecna.gwt.connectors.client.listeners.DiagramModeListener;
import pl.tecna.gwt.connectors.client.listeners.Keyboard;
import pl.tecna.gwt.connectors.client.listeners.KeyboardListener;
import pl.tecna.gwt.connectors.client.listeners.event.DiagramAddEvent;
import pl.tecna.gwt.connectors.client.listeners.event.DiagramEvent;
import pl.tecna.gwt.connectors.client.listeners.event.DiagramRemoveEvent;
import pl.tecna.gwt.connectors.client.listeners.event.ElementConnectEvent;
import pl.tecna.gwt.connectors.client.listeners.event.ElementDragEvent;
import pl.tecna.gwt.connectors.client.util.ConnectorStyle;
import pl.tecna.gwt.connectors.client.util.WidgetUtils;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class Diagram {

  private final Logger LOG = Logger.getLogger("Diagram");

  final static int MIN_SELECTION_SIZE = 20; // rect 20x20

  /**
   * Defines weather keyboard events should be fired
   */
  public boolean keyboardEnabled = true;
  
  public int initialDragTolerance = 20;

  /**
   * If true, then connector in 'initializing' state would be
   * drawn in straight lane with shape height/width tolerance
   */
  public boolean drawInitializingConnectorsInLine = false;
  
  private List<DiagramListener> listeners;
  private DiagramModeListener modeListener;
  private boolean enableEvents = true;

  public ShapePickupDragController shapeDragController;
  public EndPointDragController endPointDragController;

  public AbsolutePanel boundaryPanel;

  public ArrayList<Connector> connectors;
  public ArrayList<Shape> shapes;

  private Point startSelectionPoint;
  private HTML selection = null;
  private boolean endPointDragging = false;
  public HandlerRegistration boundarySelectionHandler = null;
  private boolean selectionMode;
  private boolean dragModeOnClick;
  private KeyboardListener keyboardListener;

  private ArrayList<HTML> markers = new ArrayList<HTML>();

  /**
   * Defines whether Ctrl key is currently pressed.
   */
  public boolean ctrlPressed = false;

  public boolean altPressed = false;

  public Diagram(AbsolutePanel boundaryPanel) {
    super();

    addKeyboardListener();

    this.listeners = new ArrayList<DiagramListener>();

    this.boundaryPanel = boundaryPanel;

    this.boundaryPanel.sinkEvents(Event.ONMOUSEDOWN);
    this.boundaryPanel.sinkEvents(Event.ONMOUSEUP);
    this.boundaryPanel.sinkEvents(Event.ONMOUSEMOVE);

    disableTextSelection(boundaryPanel.getElement(), true);

    setSelectionMode(false);

    this.boundaryPanel.addDomHandler(new MouseUpHandler() {

      public void onMouseUp(MouseUpEvent event) {
        Point endSelectionPoint = new Point(event.getX(), event.getY());
        if (startSelectionPoint != null) {

          if (((Math.abs(startSelectionPoint.getLeft() - endSelectionPoint.getLeft())) > MIN_SELECTION_SIZE)
              && (Math.abs((startSelectionPoint.getTop() - endSelectionPoint.getTop()))) > MIN_SELECTION_SIZE) {
            for (Shape s : Diagram.this.shapes) {
              if (s.isInRect(startSelectionPoint, endSelectionPoint)) {
                Diagram.this.shapeDragController.toggleSelection(s);
              }
            }
          }
        }
        startSelectionPoint = null;
        if (selection != null)
          selection.removeFromParent();

        selection = null;
        showActivePoints(false);
      }
    }, MouseUpEvent.getType());

    this.boundaryPanel.addDomHandler(new MouseMoveHandler() {

      public void onMouseMove(MouseMoveEvent event) {
        Point actualPosition = new Point(event.getX(), event.getY());
        if (startSelectionPoint != null) {
          if (((Math.abs(startSelectionPoint.getLeft() - actualPosition.getLeft())) > MIN_SELECTION_SIZE)
              && (Math.abs((startSelectionPoint.getTop() - actualPosition.getTop()))) > MIN_SELECTION_SIZE) {
            if (selection == null) {
              selection = new HTML();
              Diagram.this.boundaryPanel.add(selection);
            }
            int left =
                (startSelectionPoint.getLeft() <= actualPosition.getLeft()) ? startSelectionPoint.getLeft()
                    : actualPosition.getLeft();

            int top =
                (startSelectionPoint.getTop() <= actualPosition.getTop()) ? startSelectionPoint.getTop()
                    : actualPosition.getTop();
            int width = Math.abs(startSelectionPoint.getLeft() - actualPosition.getLeft());
            int height = Math.abs((startSelectionPoint.getTop() - actualPosition.getTop()));
            selection
                .setHTML("<div class'gwt-HTML'"
                    + " style=\"opacity:0.1; filter: alpha(opacity=10); position:absolute; background-color:#00bfff; width: "
                    + width + "px; height:" + height + "px; left:" + left + "px; top:" + top + "px;\">&nbsp;</div>");

          }
        }
      }

    }, MouseMoveEvent.getType());

    // Store all connectors and shapes
    connectors = new ArrayList<Connector>();
    shapes = new ArrayList<Shape>();

    // Create drag controller to control shapes dragging
    shapeDragController = new ShapePickupDragController(boundaryPanel, true, Diagram.this);
    shapeDragController.setBehaviorDragStartSensitivity(2);
    shapeDragController.setBehaviorConstrainedToBoundaryPanel(true);
    shapeDragController.setBehaviorMultipleSelection(true);
    shapeDragController.addDragHandler(new DragHandlerAdapter() {

      @Override
      public void onDragStart(DragStartEvent event) {
        int startX =
            Diagram.this.boundaryPanel.getWidgetLeft(event.getContext().draggable)
                - Diagram.this.boundaryPanel.getAbsoluteLeft();
        int startY =
            Diagram.this.boundaryPanel.getWidgetTop(event.getContext().draggable)
                - Diagram.this.boundaryPanel.getAbsoluteTop();
        Diagram.this.onElementDrag(new ElementDragEvent(event.getContext(), startX, startY,
            ElementDragEvent.DragEventType.DRAG_START));
      }

      @Override
      public void onDragEnd(DragEndEvent event) {

        Widget widget = event.getContext().draggable;
        if (event.getContext().vetoException != null) {
          if (event.getContext().draggable instanceof Shape) {
            Shape draggable = (Shape) event.getContext().draggable;
            draggable.updateConnectors();
          }
        }
        if (widget instanceof Shape) {
          Shape shape = (Shape) event.getContext().draggable;
          if (!ctrlPressed) {
            fixShapePosition(shape);
            for (ConnectionPoint cp : shape.connectionPoints) {
              for (EndPoint ep : cp.gluedEndPoints) {
                // connector between two dragged shapes
                if (ep.connector.startEndPoint.gluedConnectionPoint != null
                    && ep.connector.endEndPoint.gluedConnectionPoint != null
                    && ep.connector.startEndPoint.gluedConnectionPoint.getParentWidget() != null
                    && ep.connector.endEndPoint.gluedConnectionPoint.getParentWidget() != null
                    && event.getContext().selectedWidgets.contains(ep.connector.startEndPoint.gluedConnectionPoint.getParentWidget())
                    && event.getContext().selectedWidgets.contains(ep.connector.endEndPoint.gluedConnectionPoint.getParentWidget())) {
                  ep.connector.moveOffsetFromStartPos(shape.getTranslationX(), shape.getTranslationY());
                } else {
                  ep.connector.updateCornerPoints();
                }
              }
            }

            fixLineSections(shape);
          } else {
            fixShapePosition(shape);
            for (Connector c : shape.getConnectedConnectors()) {
              if (c.startEndPoint.gluedConnectionPoint != null && c.endEndPoint.gluedConnectionPoint != null
                  && c.startEndPoint.gluedConnectionPoint.getParentWidget() != null
                  && c.endEndPoint.gluedConnectionPoint.getParentWidget() != null
                  && event.getContext().selectedWidgets.contains(c.startEndPoint.gluedConnectionPoint.getParentWidget())
                  && event.getContext().selectedWidgets.contains(c.endEndPoint.gluedConnectionPoint.getParentWidget())) {
                c.moveOffsetFromStartPos(shape.getTranslationX(), shape.getTranslationY());
              } else {
                c.fixEndSectionDirection(c.endEndPoint);
                c.fixEndSectionDirection(c.startEndPoint);
                c.fixLineSections(c.getCorners());
                c.drawSections();
              }
            }
          }

          for (ConnectionPoint cp : shape.connectionPoints) {
            for (EndPoint gluedEp : cp.gluedEndPoints) {
              if (gluedEp.isAttached()) {
                WidgetUtils.setWidgetPosition((AbsolutePanel) gluedEp.getParent(), gluedEp, 
                    cp.getCenterLeft() - EndPoint.RADIUS, cp.getCenterTop() - EndPoint.RADIUS);
              }
            }
          }
        }

        int endX =
            Diagram.this.boundaryPanel.getWidgetLeft(event.getContext().draggable)
                - Diagram.this.boundaryPanel.getAbsoluteLeft();
        int endY =
            Diagram.this.boundaryPanel.getWidgetTop(event.getContext().draggable)
                - Diagram.this.boundaryPanel.getAbsoluteTop();

        Diagram.this.onElementDrag(new ElementDragEvent(event.getContext(), endX, endY,
            ElementDragEvent.DragEventType.DRAG_END));
      }

    });

    // Create drag controller to control end point dragging
    endPointDragController = new EndPointDragController(this.boundaryPanel, true, Diagram.this);
    endPointDragController.setBehaviorConstrainedToBoundaryPanel(true);
    endPointDragController.setBehaviorDragStartSensitivity(4);
    endPointDragController.addDragHandler(new DragHandlerAdapter() {

      @Override
      public void onDragStart(DragStartEvent event) {
        endPointDragging = true;
        if (event != null) {
          EndPoint ep = (EndPoint) event.getSource();
          ep.connector.select();
        }

        WidgetLocation location = new WidgetLocation(event.getContext().draggable, event.getContext().boundaryPanel);
        Diagram.this.onElementDrag(new ElementDragEvent(event.getContext(), location.getLeft(), location.getTop(),
            ElementDragEvent.DragEventType.DRAG_START));
      }

      @Override
      public void onDragEnd(DragEndEvent event) {
        endPointDragging = false;
        EndPoint endPoint = (EndPoint) event.getSource();
        
        if (event.getContext().finalDropController != null && 
            !(event.getContext().finalDropController instanceof ConnectionPointDropController)) {
          endPoint.connector.fixEndSectionDirection(endPoint);
          endPoint.connector.drawSections(endPoint.connector.getCorners());
          try {
            endPoint.connector.cornerPoints =
                (ArrayList<CornerPoint>) endPoint.connector.fixLineSections(endPoint.connector.getCorners());
            endPoint.connector.drawSections();
            endPoint.connector.fixSections();
          } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unexpected exception", e);
          }
        }
        
        Integer endX = null;
        Integer endY = null;
        if (event.getContext().draggable.getParent() != null
            && Diagram.this.boundaryPanel.equals(event.getContext().draggable.getParent())) {
          WidgetLocation location = new WidgetLocation(event.getContext().draggable, Diagram.this.boundaryPanel);
          endX = location.getLeft();
          endY = location.getTop();
        }
        Diagram.this.onElementDrag(new ElementDragEvent(event.getContext(), endX, endY,
            ElementDragEvent.DragEventType.DRAG_END));

        endPoint.connector.initalizing = false;
      }
    });
  }

  private void fixLineSections(Shape shape) {

    for (Connector conn : Diagram.this.connectors) {
      List<Section> overlapSections = shape.overlapSections(conn);
      if (overlapSections.size() != 0) {
        List<CornerPoint> corners = conn.getCorners();
        conn.evadeShape(shape, overlapSections, corners);
        conn.fixLineSections(corners);
        conn.drawSections(corners);
      }
    }
  }

  public String saveXML() {
    String xmlString = "connectors:\n";
    // TODO Save diagram to xml
    // Save Shapes

    // Save Connectors
    // TODO Change to StringBuilder
    for (int i = 0; i < connectors.size(); i++) {
      xmlString +=
          "(" + connectors.get(i).startEndPoint.getLeft() + "," + connectors.get(i).startEndPoint.getTop() + ")";

      for (int k = 0; k < connectors.get(k).cornerPoints.size(); k++) {
        xmlString +=
            "(" + connectors.get(i).cornerPoints.get(k).getLeft() + ","
                + connectors.get(i).cornerPoints.get(k).getTop() + ")";
      }

      xmlString += "(" + connectors.get(i).endEndPoint.getLeft() + "," + connectors.get(i).endEndPoint.getTop() + ")\n";
    }

    return xmlString;
  }

  public Shape getStartShapeForConnector(Connector connector) {
    try {
      return (Shape) connector.startEndPoint.gluedConnectionPoint.getParent().getParent();
    } catch (Exception e) {
      return null;
    }
  }

  public Shape getEndShapeForConnector(Connector connector) {
    try {
      return (Shape) connector.endEndPoint.gluedConnectionPoint.getParent().getParent();
    } catch (Exception e) {
      return null;
    }
  }

  public void showActivePoints(boolean show) {
    if (show) {
      for (Shape s : shapes) {
        HTML marker = new HTML();
        marker
            .setHTML("<div class='marker' style='opacity:0.7;position:absolute; background-color:#f00; width:4px; height:4px; left:"
                + (s.getRelativeShapeLeft() + s.getOffsetWidth() / 2 - 2)
                + "px; top:"
                + (s.getRelativeShapeTop() + s.getOffsetHeight() / 2 - 2) + "px'></div>");
        markers.add(marker);
        Diagram.this.boundaryPanel.add(marker);
        // Log.info("Showing active point at shape: "+s.getTitle());
      }
    } else {
      for (HTML m : markers) {
        Diagram.this.boundaryPanel.remove(m.asWidget());
        // markers.remove(m);
      }
    }
  }

  public void addDiagramListener(DiagramListener listener) {
    listeners.add(listener);
  }

  public void removeDiagramListener(DiagramListener listener) {
    listeners.remove(listener);
  }

  public void clearListeners() {
    listeners.clear();
  }

  public void onDiagramAdd(DiagramAddEvent event) {
    if (isEnableEvents()) {
      for (DiagramListener listener : listeners) {
        listener.onDiagramAdd(event);
      }
    }
  }

  public void onDiagramRemove(DiagramRemoveEvent event) {
    if (isEnableEvents()) {
      for (DiagramListener listener : listeners) {
        listener.onDiagramRemove(event);
      }
    }
  }

  public void onElementConnect(ElementConnectEvent event) {
    if (isEnableEvents()) {
      for (DiagramListener listener : listeners) {
        listener.onElementConnect(event);
      }
    }
  }

  public void onElementDrag(pl.tecna.gwt.connectors.client.listeners.event.ElementDragEvent event) {
    if (isEnableEvents()) {
      for (DiagramListener listener : listeners) {
        listener.onElementDrag(event);
      }
    }
  }

  public void addDiagramModeListener(DiagramModeListener listener) {
    modeListener = listener;
  }

  protected void deleteSelectedElements() {
    List<Object> removedElList = new ArrayList<Object>();
    for (Widget widget : shapeDragController.getSelectedWidgets()) {
      removedElList.add(widget);
    }

    List<Connector> toRemove = new ArrayList<Connector>();
    for (Connector conn : connectors) {
      if (conn.isSelected) {
        toRemove.add(conn);
      }
    }

    removedElList.addAll(toRemove);

    onDiagramRemove(new DiagramRemoveEvent(removedElList));

    for (Widget widget : shapeDragController.getSelectedWidgets()) {
      if (widget instanceof Shape) {
        ((Shape) widget).removeFromDiagram(this, false);
      } else {
        shapeDragController.makeNotDraggable(widget);
        boundaryPanel.remove(widget);
      }
    }

    for (Connector conn : toRemove) {
      conn.removeFromDiagram(this, false);
    }

    shapeDragController.clearSelection();
  }

  /**
   * Changes position of dropped Shape to make last section straight (if the section before is
   * shorter than section tolerance (default 8))
   */
  public void fixShapePosition(Shape shape) {

    // LOG.d("fixShapePosition");
    // map with sections of connectors connected to the shape, from this map
    // section with
    // least length is choose
    // horizontal sections map
    Map<Integer, Section> horizontalSectionsMap = new HashMap<Integer, Section>();
    // vertical sections map
    Map<Integer, Section> verticalSectionsMap = new HashMap<Integer, Section>();
    if (shape.getParent() == null) {
      LOG.severe("Shape parent is null");
      return;
    }

    /*
     * Fill maps with sections from shape sections (sections to merge if they are short)
     */
    for (ConnectionPoint cp : shape.connectionPoints) {
      for (EndPoint ep : cp.gluedEndPoints) {
        Connector conn = ep.connector;
        if (conn.sections.size() > 1) {
          Section secondFromShape = null;
          int sectionLength;
          boolean start = false;
          if (conn.endEndPoint.equals(ep)) {
            secondFromShape = conn.sections.get(conn.sections.size() - 2);
          } else if (conn.startEndPoint.equals(ep)) {
            start = true;
            secondFromShape = conn.sections.get(1);
          }

          if (secondFromShape.isHorizontal()) {
            sectionLength = secondFromShape.endPoint.getLeft() - secondFromShape.startPoint.getLeft();
          } else {
            sectionLength = secondFromShape.endPoint.getTop() - secondFromShape.startPoint.getTop();
          }

          if (start) {
            sectionLength = -sectionLength;
          }

          if (Math.abs(sectionLength) < Shape.SECTION_TOLERANCE) {
            if (secondFromShape.isHorizontal()) {
              horizontalSectionsMap.put(sectionLength, secondFromShape);
            } else {
              verticalSectionsMap.put(sectionLength, secondFromShape);
            }
          }
        }
      }
    }

    Integer minHorizontal = Integer.MAX_VALUE;
    Integer minVertical = Integer.MAX_VALUE;
    Section lastHorizontalSection = null;
    Section lastVerticalSection = null;

    for (Integer length : horizontalSectionsMap.keySet()) {
      if (Math.abs(length) < Math.abs(minHorizontal)) {
        minHorizontal = length;
        lastHorizontalSection = horizontalSectionsMap.get(length);
      }
    }

    for (Integer length : verticalSectionsMap.keySet()) {
      if (Math.abs(length) < Math.abs(minVertical)) {
        minVertical = length;
        lastVerticalSection = verticalSectionsMap.get(length);
      }
    }

    // fix section position horizontally
    if (lastHorizontalSection != null) {
      WidgetUtils.setWidgetPosition((AbsolutePanel) shape.getParent(), shape, shape.getRelativeShapeLeft() - minHorizontal, shape.getRelativeShapeTop());
      lastHorizontalSection.connector.drawSections(lastHorizontalSection.connector
          .fixLineSections(lastHorizontalSection.connector.getCorners()));
    }

    // fix section position vertically
    if (lastVerticalSection != null) {
      WidgetUtils.setWidgetPosition((AbsolutePanel) shape.getParent(), shape, shape.getRelativeShapeLeft(), shape.getRelativeShapeTop() - minVertical);
      lastVerticalSection.connector.drawSections(lastVerticalSection.connector
          .fixLineSections(lastVerticalSection.connector.getCorners()));
    }

    shape.updateConnectors();
  }

  /**
   * Defines whether given point is on one of selected elements.
   * 
   * @param point the point
   * @param diagram the diagram
   * @return <code>true</code>, if the point is on the selected elements;
   */
  public boolean isOnElement(Point point, Diagram diagram) {

    for (Widget widget : diagram.shapeDragController.dragableWidgets) {
      boolean isOnWidget = isOnWidget(point, widget);
      if (isOnWidget) {
        return true;
      }
    }

    for (Connector conn : diagram.connectors) {
      for (Section shape : conn.sections) {
        boolean isOnWidget = isOnWidget(point, shape);
        if (isOnWidget) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Defines whether given point is on widget.
   * 
   * @param point the point
   * @param widget the diagram
   * @return <code>true</code>, if the point is on the widget
   */
  private boolean isOnWidget(Point point, Widget widget) {
    int widgetLeft = boundaryPanel.getWidgetLeft(widget);
    int widgetRight = widgetLeft + widget.getOffsetWidth();
    int widgetTop = boundaryPanel.getWidgetTop(widget);
    int widgetBottom = widgetTop + widget.getOffsetHeight();

    if (widgetLeft <= point.getLeft() && widgetRight >= point.getLeft() && widgetTop <= point.getTop()
        && widgetBottom >= point.getTop()) {
      return true;
    } else {
      return false;
    }
  }

  public void deselectAllSections() {
    for (Connector conn : connectors) {
      conn.deselect();
    }
  }

  public void clearSelection() {
    shapeDragController.clearSelection();
    deselectAllSections();
  }

  public static void disableTextSelection(Element elem, boolean disable) {
    elem.setClassName("x-unselectable");
    disableTextSelectInternal(elem, disable);
  }

  public void setSelectionMode(boolean mode) {
    this.selectionMode = mode;
    if (boundarySelectionHandler != null) {
      boundarySelectionHandler.removeHandler();
    }

    boundarySelectionHandler = this.boundaryPanel.addDomHandler(new MouseDownHandler() {

      public void onMouseDown(MouseDownEvent event) {

        int mouseLeft = event.getX();
        int mouseTop = event.getY();
        if (!selectionMode) {
          dragModeOnClick = true;
          for (pl.tecna.gwt.connectors.client.elements.Shape shape : shapes) {
            if (shape.isOnShape(mouseLeft, mouseTop)) {
              dragModeOnClick = false;
            }
          }
          int startLeft = 0;
          int startTop = 0;
          int endLeft = 0;
          int endTop = 0;
          // set diagram drag mode false if connector EndPoint is dragged
          for (Connector connector : connectors) {
            startLeft = connector.startEndPoint.getAbsoluteLeft() - boundaryPanel.getAbsoluteLeft();
            startTop = connector.startEndPoint.getAbsoluteTop() - boundaryPanel.getAbsoluteTop();
            endLeft = connector.endEndPoint.getAbsoluteLeft() - boundaryPanel.getAbsoluteLeft();
            endTop = connector.endEndPoint.getAbsoluteTop() - boundaryPanel.getAbsoluteTop();
            if (((startLeft - 10 < mouseLeft && mouseLeft < endLeft + 10) || (endLeft - 10 < mouseLeft && mouseLeft < startLeft + 10))
                && ((startTop - 10 < mouseTop && mouseTop < endTop + 10) || (endTop - 10 < mouseTop && mouseTop < startTop + 10))) {
              dragModeOnClick = false;
            }
          }

          if (dragModeOnClick && modeListener != null) {
            modeListener.onDiagramModeChanged(new DiagramEvent(new Point(event.getClientX(), event.getClientY())));
          }
        }

        Point clickPoint = new Point(mouseLeft, mouseTop);

        boolean isOnElement = isOnElement(clickPoint, Diagram.this);

        if (!ctrlPressed && !isOnElement) {
          shapeDragController.clearSelection();
          for (Connector conn : connectors) {
            if (conn.isSelected) {
              conn.deselect();
            }
          }
        }
        if (selectionMode && !isOnElement && !endPointDragging)
          beginSelection(clickPoint);
      }

      private void beginSelection(Point clickPoint) {
        startSelectionPoint = clickPoint;
        showActivePoints(true);
      }
    }, MouseDownEvent.getType());
  }

  public void setSelectedPoint(Point point) {
    boolean isOnElement = isOnElement(point, Diagram.this);
    if (!ctrlPressed && !isOnElement) {
      shapeDragController.clearSelection();
      for (Connector conn : connectors) {
        if (conn.isSelected) {
          conn.deselect();
        }
      }
    }
    if (selectionMode && !isOnElement && !endPointDragging) {
      startSelectionPoint = point;
      showActivePoints(true);
    }
  }

  private native static void disableTextSelectInternal(Element e, boolean disable) /*-{
		if (disable) {
			e.ondrag = function() {
				return false;
			};
			e.onselectstart = function() {
				return false;
			};
		} else {
			e.ondrag = null;
			e.onselectstart = null;
		}
  }-*/;

  public boolean isEnableEvents() {
    return enableEvents;
  }

  public void setEnableEvents(boolean enableEvents) {
    this.enableEvents = enableEvents;
  }

  public void addKeyboardListener() {
    // Add keyboard listener
    Keyboard.getInstance().init();

    if (keyboardListener == null) {
      keyboardListener = new KeyboardListener() {

        @Override
        public void onKeyDown(int key, Event e) {

          ctrlPressed = e.getCtrlKey();
          altPressed = e.getAltKey();
        }

        public void onKeyUp(int key, Event e) {
          if (keyboardEnabled) {
            if (e.getKeyCode() == KeyCodes.KEY_DELETE && e.getCtrlKey()) {
              // Delete selected elements
              deleteSelectedElements();
            }

          }
          ctrlPressed = e.getCtrlKey();
          altPressed = e.getAltKey();
        }
      };

      Keyboard.getInstance().addListener(keyboardListener);
    }
  }

  public boolean removeKeyboardListener() {
    return Keyboard.getInstance().removeListener(keyboardListener);
  }

  public Connector createConnector(int startLeft, int startTop, int endLeft, int endTop, EndPoint endEndPoint,
      ConnectorStyle style) {
    SectionDecoration endDecoration;
    if (style == ConnectorStyle.SOLID) {
      endDecoration = new SectionDecoration(DecorationType.ARROW_SOLID);
    } else {
      endDecoration = new SectionDecoration(DecorationType.ARROW_LINE);
    }
    return new Connector(startLeft, startTop, endLeft, endTop, null, endDecoration, endEndPoint, Diagram.this, style);
  }
}
