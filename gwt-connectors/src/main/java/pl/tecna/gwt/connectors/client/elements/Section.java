package pl.tecna.gwt.connectors.client.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.CornerPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.Point;
import pl.tecna.gwt.connectors.client.drag.AxisXYDragController;
import pl.tecna.gwt.connectors.client.elements.SectionDecoration.DecorationDirection;
import pl.tecna.gwt.connectors.client.listeners.event.ConnectorClickEvent;
import pl.tecna.gwt.connectors.client.listeners.event.ConnectorDoubleClickEvent;
import pl.tecna.gwt.connectors.client.listeners.event.ElementDragEvent;
import pl.tecna.gwt.connectors.client.util.ConnectorStyle;
import pl.tecna.gwt.connectors.client.util.Position;
import pl.tecna.gwt.connectors.client.util.WidgetUtils;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

public class Section extends HTML {

  private final Logger LOG = Logger.getLogger("Section");
  
  private static final int SIZE = 2;
  private static final int TRANSPARENT_MARGIN_SIZE = 2;

  public Point startPoint;
  public Point endPoint;
  public Connector connector;

  public SectionDecoration startPointDecoration;
  public SectionDecoration endPointDecoration;

  private int height;
  private int width;
  
  public static final int VERTICAL = 0;
  public static final int HORIZONTAL = 1;

  private ConnectorStyle style;

  /**
   * Defines saved orientation, it doesn't contain current orientation
   */
  public int savedOrientation;

  private AxisXYDragController sectionDragController;

  /**
   * Section represents vertical or horizontal part of {@link Connector}.
   * 
   * @param startPoint a {@link CornerPoint} or {@link EndPoint} where the Section starts
   * @param endPoint a {@link CornerPoint} or {@link EndPoint} where the Section ends
   */
  public Section(Point startPoint, Point endPoint, Connector connector) throws IllegalArgumentException {
    super();

    // this.sinkEvents(Event.ONMOUSEDOWN);
    // this.unsinkEvents(Event.ONDBLCLICK);
    // this.unsinkEvents(Event.ONCLICK);

    this.connector = connector;

    this.startPoint = startPoint;
    this.endPoint = endPoint;

    if ((isHorizontal() == false) && (isVertical() == false)) {
      throw new IllegalArgumentException("Sections must be horizontal or vertical! " + "Start :" + startPoint.getLeft()
          + " " + startPoint.getTop() + " end:" + endPoint.getLeft() + " " + endPoint.getTop());
    }

    // Count Section width and height
    this.height = Math.abs(endPoint.getTop() - startPoint.getTop());
    this.width = Math.abs(endPoint.getLeft() - startPoint.getLeft());

    addDoubleClickHandler(new DoubleClickHandler() {

      public void onDoubleClick(DoubleClickEvent event) {
        Section.this.connector.onConnectorDoubleClick(new ConnectorDoubleClickEvent(Section.this.connector,
            Section.this));
      }
    });

    addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        if (!Section.this.connector.diagram.ctrlPressed) {
          Section.this.connector.diagram.deselectAllSections();
          Section.this.connector.diagram.shapeDragController.clearSelection();
        }
        if (Section.this.connector.isSelected) {
          Section.this.connector.deselect();
        } else {
          Section.this.connector.select();
        }
        Section.this.connector.onConnectorClick(new ConnectorClickEvent(Section.this.connector, Section.this));
      }
    });

  }

  /**
   * Shows Section on a given panel. The Section is represented by horizontal or vertical line. The
   * panel argument must be of type of AbsolutePanel.
   * <p>
   * This method also add a focus panel to the Section. Focus panel is necessary to provide drag and
   * drop functionality to the Section. It also makes possible selecting a Section.
   * <p>
   * If the Section is already on the Diagram this method do nothing.
   * 
   * @param diagram an absolute panel on witch the line will be drawn
   */
  public void showOnDiagram(Diagram diagram) {
    showOnDiagram(diagram, false, ConnectorStyle.SOLID);
  }

  /**
   * Shows Section on a given panel. The Section is represented by horizontal or vertical line. The
   * panel argument must be of type of AbsolutePanel.
   * <p>
   * This method also add a focus panel to the Section. Focus panel is necessary to provide drag and
   * drop functionality to the Section. It also makes possible selecting a Section.
   * <p>
   * If the Section is already on the Diagram this method do nothing.
   * 
   * @param diagram an absolute panel on witch the line will be drawn
   * @param isSelected defines whether connector is selected
   */
  public void showOnDiagram(Diagram diagram, boolean isSelected, ConnectorStyle style) {
    // Create DIV to draw a line
    // Using CSS
    /*
     * .gwt-connectors-line { font-size: 1px; line-height:1px; background-color: black }
     * .gwt-connectors-line-vertical { width:1px } .gwt-connectors-line-horizontal { height:1px }
     */

    this.style = style;
    AbsolutePanel panel = diagram.boundaryPanel;

    boolean allowHorizontalDragging = false;
    boolean allowVerticalDragging = false;

    // Set line look and behavior

    if (isVertical()) {
      if (isSelected) {
        setHTML(selectedVerticalLine(this.height, style));
      } else {
        setHTML(verticalLine(this.height, style));
      }
      addStyleName("gwt-connectors-vertical-section");
      allowHorizontalDragging = true;
    } else if (isHorizontal()) {
      if (isSelected) {
        this.setHTML(selectedHorizontalLine(this.width, style));
      } else {
        this.setHTML(horizontalLine(this.width, style));
      }
      addStyleName("gwt-connectors-horizontal-section");
      allowVerticalDragging = true;
    }

    // Add drag and drop functionality
    this.sectionDragController = new AxisXYDragController(panel, true, allowHorizontalDragging, allowVerticalDragging) {

      @Override
      public void dragStart() {

        // If dragged section startPoint or dragged section endPoint
        // is glued to connectionPoint then split section into three
        // to draw new lines to connectionPoint
        try {
          if (Section.this.startPointIsGluedToConnectionPoint() || Section.this.endPointIsGluedToConnectionPoint()) {
            // Calculate new CornerPoints
            ArrayList<CornerPoint> newCornerPoints = new ArrayList<CornerPoint>();
            Point sp = Section.this.startPoint;
            Point ep = Section.this.endPoint;
            CornerPoint cp1 =
                new CornerPoint(sp.getLeft() + (ep.getLeft() - sp.getLeft()) / 2, sp.getTop()
                    + (ep.getTop() - sp.getTop()) / 2);
            CornerPoint cp2 =
                new CornerPoint(sp.getLeft() + (ep.getLeft() - sp.getLeft()) / 2, sp.getTop()
                    + (ep.getTop() - sp.getTop()) / 2);
            newCornerPoints.add(cp1);
            newCornerPoints.add(cp2);
            // Split Section
            Section.this.splitSection(newCornerPoints);
          }
        } catch (Exception e) {
          LOG.severe("Section drag start error " + e.getMessage());
          e.printStackTrace();
        }
        try {
          super.dragStart();
        } catch (Exception e) {
          LOG.severe("Section (super) drag start error " + e.getMessage());
          e.printStackTrace();
        }
      }

      @Override
      public void dragMove() {
        try {
          if (isAllowHorizontalDragging()) {
            if (Section.this.startPoint.getLeft() < Section.this.endPoint.getLeft()) {
              Section.this.startPoint.setLeftPosition(context.draggable.getAbsoluteLeft()
                  - context.boundaryPanel.getAbsoluteLeft() + SIZE);
              Section.this.endPoint.setLeftPosition(context.draggable.getAbsoluteLeft()
                  - context.boundaryPanel.getAbsoluteLeft() + width + SIZE);
            } else {
              Section.this.startPoint.setLeftPosition(context.draggable.getAbsoluteLeft()
                  - context.boundaryPanel.getAbsoluteLeft() + width + SIZE);
              Section.this.endPoint.setLeftPosition(context.draggable.getAbsoluteLeft()
                  - context.boundaryPanel.getAbsoluteLeft() + SIZE);
            }
          }

          if (isAllowVerticalDragging()) {
            if (Section.this.startPoint.getTop() < Section.this.endPoint.getTop()) {
              Section.this.startPoint.setTopPosition(context.draggable.getAbsoluteTop()
                  - context.boundaryPanel.getAbsoluteTop() + SIZE);
              Section.this.endPoint.setTopPosition(context.draggable.getAbsoluteTop() - 
                  context.boundaryPanel.getAbsoluteTop() + height + SIZE);
            } else {
              Section.this.startPoint.setTopPosition(context.draggable.getAbsoluteTop()
                  - context.boundaryPanel.getAbsoluteTop() + height + SIZE);
              Section.this.endPoint.setTopPosition(context.draggable.getAbsoluteTop() - context.boundaryPanel.getAbsoluteTop() + SIZE);
            }
          }
          
          if (Section.this.connector.getNextSection(Section.this) != null) {
            Section.this.connector.getNextSection(Section.this).update();
          };
          if (Section.this.connector.getPrevSection(Section.this) != null) {
            Section.this.connector.getPrevSection(Section.this).update();
          };

          Section.this.connector.endEndPoint.update();
          Section.this.connector.startEndPoint.update();

          if (startPointDecoration != null) {
            startPointDecoration.update(calculateStartPointDecorationDirection(), startPoint.getLeft(), startPoint
                .getTop());
          }
          if (endPointDecoration != null) {
            endPointDecoration.update(calculateEndPointDecorationDirection(), endPoint.getLeft(), endPoint.getTop());
          }
        } catch (Exception e) {
          LOG.severe("Section drag move error " + e.getMessage());
          e.printStackTrace();
        }

        try {
          // super.dragMove();

          // To provide XY drag feature (BEGIN)
          if (isAllowHorizontalDragging() == false) {
            context.desiredDraggableX = initialDraggableLocation.getLeft() + boundaryOffsetX;
          }
          if (isAllowVerticalDragging() == false) {
            context.desiredDraggableY = initialDraggableLocation.getTop() + boundaryOffsetY;
          }
          // To provide XY drag feature (END)

          int desiredLeft = context.desiredDraggableX - boundaryOffsetX;
          int desiredTop = context.desiredDraggableY - boundaryOffsetY;

          if (getBehaviorConstrainedToBoundaryPanel()) {
            desiredLeft =
                Math.max(0, Math.min(desiredLeft, dropTargetClientWidth - context.draggable.getOffsetWidth()));
            desiredTop =
                Math.max(0, Math.min(desiredTop, dropTargetClientHeight - context.draggable.getOffsetHeight()));
          }

          if (isAllowHorizontalDragging()) {
            if (startPoint.getTop().intValue() > endPoint.getTop().intValue()) {
              desiredTop = endPoint.getTop();
            } else {
              desiredTop = startPoint.getTop();
            }
            desiredLeft += SIZE;
          }
          if (isAllowVerticalDragging()) {
            if (startPoint.getLeft().intValue() > endPoint.getLeft().intValue()) {
              desiredLeft = endPoint.getLeft();
            } else {
              desiredLeft = startPoint.getLeft();
            }
            desiredTop += SIZE;
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

        } catch (Exception e) {
          LOG.severe("Section (super) drag move error " + e.getMessage());
          e.printStackTrace();
        }
      }

      @Override
      public void dragEnd() {

        // If after dragging two or more neighbor Sections are aligned to the line
        // (they form one single line), those neighbor Sections are merged to one.
        if (Section.this.connector.sections.size() > 2) {
          if ((Section.this.connector.getPrevSection(Section.this) != null)
              && (Section.this.connector.getPrevSection(Section.this).hasNoDimensions())) {
            System.out.println("merge with preceding Section");
            // Loop 2 times to remove two preceding Sections
            try {
              for (int i = 0; i < 2; i++) {
                Section.this.startPoint = Section.this.connector.getPrevSection(Section.this).startPoint;
                Section.this.startPointDecoration =
                    Section.this.connector.getPrevSection(Section.this).startPointDecoration;
                Section.this.connector.getPrevSection(Section.this).removeFromDiagram();
                Section.this.connector.sections.remove(Section.this.connector.getPrevSection(Section.this));
              }
            } catch (Exception e) {
              // LOG.e("error merging sections", e);
            }
          }
          if ((Section.this.connector.getNextSection(Section.this) != null)
              && (Section.this.connector.getNextSection(Section.this).hasNoDimensions())) {
            System.out.println("merge with succeeding Section");
            // Loop 2 times to remove two succeeding Sections
            for (int i = 0; i < 2; i++) {
              try {
                Section.this.endPoint = Section.this.connector.getNextSection(Section.this).endPoint;
                Section.this.endPointDecoration =
                    Section.this.connector.getNextSection(Section.this).endPointDecoration;
                Section.this.connector.getNextSection(Section.this).removeFromDiagram();
                Section.this.connector.sections.remove(Section.this.connector.getNextSection(Section.this));
              } catch (Exception e) {
                // LOG.e("Error while connecting sections...");
              }
            }
          }
        }
        super.dragEnd();
        connector.updateCornerPoints();
      }

    };

    int positionLeft = Math.min(this.startPoint.getLeft(), this.endPoint.getLeft());
    int positionTop = Math.min(this.startPoint.getTop(),this.endPoint.getTop());
    if (isVertical()) {
      positionLeft -= TRANSPARENT_MARGIN_SIZE;
    } else {
      positionTop -= TRANSPARENT_MARGIN_SIZE;
    }
    // Add line to given panel
    WidgetUtils.addWidget(panel, this, positionLeft, positionTop);
    this.sectionDragController.makeDraggable(this);
    this.sectionDragController.setBehaviorDragStartSensitivity(5);

    this.sectionDragController.addDragHandler(new DragHandlerAdapter() {

      @Override
      public void onPreviewDragStart(DragStartEvent event) {
        if (event.getContext().draggable != null) {
          event.getContext().draggable.getElement().scrollIntoView();
          if (sectionDragController.getBoundaryPanel().getParent() == null
              || (sectionDragController.getBoundaryPanel().getParent().getOffsetHeight() < event.getContext().draggable
                  .getOffsetHeight() || sectionDragController.getBoundaryPanel().getParent().getOffsetWidth() < event
                  .getContext().draggable.getOffsetWidth())) {
            sectionDragController.setBehaviorScrollIntoView(false);
          } else {
            sectionDragController.setBehaviorScrollIntoView(true);
          }
        }
      }

      @Override
      public void onDragStart(DragStartEvent event) {

        int startX =
            connector.diagram.boundaryPanel.getWidgetLeft(event.getContext().draggable)
                - connector.diagram.boundaryPanel.getAbsoluteLeft();
        int startY =
            connector.diagram.boundaryPanel.getWidgetTop(event.getContext().draggable)
                - connector.diagram.boundaryPanel.getAbsoluteTop();
        connector.diagram.onElementDrag(new pl.tecna.gwt.connectors.client.listeners.event.ElementDragEvent(event
            .getContext(), startX, startY,
            pl.tecna.gwt.connectors.client.listeners.event.ElementDragEvent.DragEventType.DRAG_START));
        Section.this.connector.select();
      }

      @Override
      public void onDragEnd(DragEndEvent event) {

        if (Section.this.isAttached()) {
          Section.this.update();
        }

        // update end points
        Section.this.connector.endEndPoint.update();
        Section.this.connector.startEndPoint.update();

        List<CornerPoint> corners = connector.getCorners();
        if (connector.fixOverlapSections(corners)) {
          connector.drawSections(corners);
        }

        // Merge last sections if length is lesser than defined
        if (connector.startEndPoint.isGluedToConnectionPoint()) {
          connector.mergeTwoFirstSections(connector.sections.get(0), corners);
        }
        if (connector.endEndPoint.isGluedToConnectionPoint()) {
          connector.mergeTwoLastSections(connector.sections.get(connector.sections.size() - 1), corners);
        }

        connector.fixLineSections(corners);

        connector.drawSections(corners, true);

        // int endX =
        // connector.diagram.boundaryPanel.getWidgetLeft(event.getContext().draggable)
        // - connector.diagram.boundaryPanel.getAbsoluteLeft();
        // int endY =
        // connector.diagram.boundaryPanel.getWidgetTop(event.getContext().draggable)
        // - connector.diagram.boundaryPanel.getAbsoluteTop();

        connector.diagram.onElementDrag(new ElementDragEvent(event.getContext(),
            event.getContext().desiredDraggableX, event.getContext().desiredDraggableY,
            ElementDragEvent.DragEventType.DRAG_END));

      }
    });

    // Calculate decoration's direction and add SectionDecorations to diagram
    if (startPointDecoration != null) {
      this.startPointDecoration.showOnDiagram(panel, calculateStartPointDecorationDirection(), startPoint.getLeft(),
          startPoint.getTop());
    }
    if (endPointDecoration != null) {
      this.endPointDecoration.showOnDiagram(panel, calculateEndPointDecorationDirection(), endPoint.getLeft(), endPoint
          .getTop());
    }
  }

  public DecorationDirection calculateEndPointDecorationDirection() {
    if (isHorizontal()) {
      if (this.endPoint.getLeft() < this.startPoint.getLeft()) {
        return DecorationDirection.HORIZONTAL_LEFT;
      } else {
        return DecorationDirection.HORIZONTAL_RIGHT;
      }
    } else if (isVertical()) {
      if (this.endPoint.getTop() < this.startPoint.getTop()) {
        return DecorationDirection.VERTICAL_UP;
      } else {
        return DecorationDirection.VERTICAL_DOWN;
      }
    }
    return DecorationDirection.HORIZONTAL_LEFT;
  }

  public DecorationDirection calculateStartPointDecorationDirection() {
    if (isHorizontal()) {
      if (this.startPoint.getLeft() < this.endPoint.getLeft()) {
        return DecorationDirection.HORIZONTAL_LEFT;
      } else {
        return DecorationDirection.HORIZONTAL_RIGHT;
      }
    } else if (isVertical()) {
      if (this.startPoint.getTop() < this.endPoint.getTop()) {
        return DecorationDirection.VERTICAL_UP;
      } else {
        return DecorationDirection.VERTICAL_DOWN;
      }
    }
    return DecorationDirection.HORIZONTAL_LEFT;
  }

  /**
   * Returns true if Section has no dimensions. It means that Section's width and height equals
   * zero.
   * 
   * @return true if Section has no dimensions false if Section has dimensions
   */
  protected boolean hasNoDimensions() {
    if ((this.startPoint.getLeft().intValue() == this.endPoint.getLeft().intValue())
        && (this.startPoint.getTop().intValue() == this.endPoint.getTop().intValue())) {
      return true;
    } else {
      return false;
    }
  }

  protected boolean startPointIsGluedToConnectionPoint() {
    if (Section.this.startPoint instanceof EndPoint) {
      if (((EndPoint) Section.this.startPoint).isGluedToConnectionPoint()) {
        return true;
      }
    }
    return false;
  }

  protected boolean endPointIsGluedToConnectionPoint() {
    if (Section.this.endPoint instanceof EndPoint) {
      if (((EndPoint) Section.this.endPoint).isGluedToConnectionPoint()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Splits the Section using CornerPoints given as parameter.
   * 
   * @param newCornerPoints an array of CornerPoints that determines a new shape of Section split up
   *          into few new Sections.
   */
  protected void splitSection(ArrayList<CornerPoint> newCornerPoints) {

    if (this.startPointIsGluedToConnectionPoint()) {
      // Add a new horizontal Section as the first Section in Connector and move decorations into
      // this section
      Section s1 = new Section(this.startPoint, newCornerPoints.get(0), this.connector);
      s1.setStartPointDecoration(this.startPointDecoration);
      this.startPointDecoration = null;
      s1.showOnDiagram(this.connector.diagram);
      this.connector.sections.add(0, s1);

      // Add a new vertical section as the second in Connector
      Section s2 = new Section(newCornerPoints.get(0), newCornerPoints.get(1), this.connector);
      s2.showOnDiagram(this.connector.diagram);
      this.connector.sections.add(1, s2);

      // Reconnect dragged Section to the second CornerPoint
      this.startPoint = newCornerPoints.get(1);
      this.update();
    }

    if (this.endPointIsGluedToConnectionPoint()) {
      // Add a new vertical Section as the last but one Section in Connector
      Section s1 = new Section(newCornerPoints.get(0), newCornerPoints.get(1), this.connector);
      s1.showOnDiagram(this.connector.diagram);
      this.connector.sections.add(s1);

      // Add a new horizontal section as the last in Connector and move decorations into this
      // section
      Section s2 = new Section(newCornerPoints.get(1), this.endPoint, this.connector);
      s2.setEndPointDecoration(this.endPointDecoration);
      this.endPointDecoration = null;
      s2.showOnDiagram(this.connector.diagram);
      this.connector.sections.add(s2);

      // Reconnect dragged Section to the first CornerPoint
      this.endPoint = newCornerPoints.get(0);
      this.update();
    }

  }

  public boolean removeFromDiagram() {
    if (endPointDecoration != null && endPointDecoration.isAttached()) {
      endPointDecoration.removeFromParent();
    }
    if (startPointDecoration != null && startPointDecoration.isAttached()) {
      startPointDecoration.removeFromParent();
    }
    return connector.diagram.boundaryPanel.remove(this);
  }

  private String verticalLine(int height, ConnectorStyle style) {
    return "<div style=\"border-left:" + SIZE + "px " + style.name().toLowerCase() + 
        " #B2B2B2; height:" + (height + SIZE) + "px\">";
  }

  private String horizontalLine(int width, ConnectorStyle style) {
    return "<div style=\"border-top:" + SIZE + "px " + style.name().toLowerCase() + 
        " #B2B2B2; width:" + width + "px\">";
  }

  private String selectedVerticalLine(int height, ConnectorStyle style) {
    return "<div style=\"border-left:" + SIZE + "px " + style.name().toLowerCase() + 
        " #00BFFF; height:" + (height + SIZE) + "px\">";
  }

  private String selectedHorizontalLine(int width, ConnectorStyle style) {
    return "<div style=\"border-top:" + SIZE + "px " + style.name().toLowerCase() + 
        " #00BFFF; width:" + width + "px\">";
  }

  /**
   * Updates section displayed on a diagram. Recalculates new position and size of the Section. Also
   * sets the functionality of the horizontal or vertical dragging.
   */
  public void update() {
    try {
      this.height = Math.abs(endPoint.getTop() - startPoint.getTop());
      this.width = Math.abs(endPoint.getLeft() - startPoint.getLeft());

      if (isVertical()) {

        this.setHTML(verticalLine(this.height, style));

        sectionDragController.setAllowHorizontalDragging(true);
        sectionDragController.setAllowVerticalDragging(false);

        updateWidgetPosition(false);

      } else if (isHorizontal()) {
        if (this.connector.isSelected) {
          this.setHTML(selectedHorizontalLine(this.width, style));
        } else {
          this.setHTML(horizontalLine(this.width, style));
        }

        sectionDragController.setAllowHorizontalDragging(false);
        sectionDragController.setAllowVerticalDragging(true);

        updateWidgetPosition(true);
      }

      // Calculate decoration's direction and update decorations
      if (startPointDecoration != null) {
        this.startPointDecoration.update(calculateStartPointDecorationDirection(), startPoint.getLeft(), startPoint
            .getTop());
      }
      if (endPointDecoration != null) {
        this.endPointDecoration.update(calculateEndPointDecorationDirection(), endPoint.getLeft(), endPoint.getTop());
      }
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error updating section", e);
      connector.calculateStandardPointsPositions();
      connector.drawSections();
    }
  }

  public void select() {

    if (getElement().getChildCount() != 0 && getElement().getChild(0) instanceof com.google.gwt.user.client.Element) {
      ((com.google.gwt.user.client.Element) getElement().getChild(0)).getStyle().setBorderColor("#00BFFF");
    }

    // Select Section Decorations
    if (startPointDecoration != null) {
      this.startPointDecoration.select();
    }

    if (endPointDecoration != null) {
      this.endPointDecoration.select();
    }
  }

  public void deselect() {

    if (getElement().getChildCount() != 0 && getElement().getChild(0) instanceof com.google.gwt.user.client.Element) {
      ((com.google.gwt.user.client.Element) getElement().getChild(0)).getStyle().setBorderColor("#B2B2B2");
    }

    // Deselect Section Decorations
    if (startPointDecoration != null) {
      this.startPointDecoration.deselect();
    }
    if (endPointDecoration != null) {
      this.endPointDecoration.deselect();
    }
  }

  public Point getStartPoint() {
    return startPoint;
  }

  public void setStartPoint(Point startPoint) {
    this.startPoint = startPoint;
  }

  public Point getEndPoint() {
    return endPoint;
  }

  public void setEndPoint(Point endPoint) {
    this.endPoint = endPoint;
  }

  public boolean isVertical() {
    return isVertical(new ArrayList<Section>());
  }

  public boolean isVertical(List<Section> checkedSections) {
    checkedSections.add(this);
    if (!(this.hasNoDimensions())) {
      if (this.startPoint.getLeft().intValue() == this.endPoint.getLeft().intValue()) {
        return true;
      }
    } else {
      if (!checkedSections.contains(this.connector.getNextSection(this))
          && (this.connector.getNextSection(this) != null)
          && (this.connector.getNextSection(this).isHorizontal(checkedSections))) {
        return true;
      }
      if (!checkedSections.contains(this.connector.getPrevSection(this))
          && (this.connector.getPrevSection(this) != null)
          && (this.connector.getPrevSection(this).isHorizontal(checkedSections))) {
        return true;
      }
      if (this.connector.getPrevSection(this) == null) {
        return true;
      }
    }

    return false;
  }

  public boolean isHorizontal() {

    return isHorizontal(new ArrayList<Section>());
  }

  public boolean isHorizontal(List<Section> checkedSections) {
    checkedSections.add(this);
    if (!(this.hasNoDimensions())) {
      if (this.startPoint.getTop().intValue() == this.endPoint.getTop().intValue()) {
        return true;
      }
    } else {
      if (!checkedSections.contains(this.connector.getNextSection(this))
          && (this.connector.getNextSection(this) != null)
          && (this.connector.getNextSection(this).isVertical(checkedSections))) {
        return true;
      }
      if (!checkedSections.contains(this.connector.getPrevSection(this))
          && (this.connector.getPrevSection(this) != null)
          && (this.connector.getPrevSection(this).isVertical(checkedSections))) {
        return true;
      }
      if (this.connector.getPrevSection(this) == null) {
        return true;
      }
    }
    return false;
  }

  public SectionDecoration getStartPointDecoration() {
    return startPointDecoration;
  }

  public void setStartPointDecoration(SectionDecoration startPointDecoration) {
    this.startPointDecoration = startPointDecoration;
  }

  public SectionDecoration getEndPointDecoration() {
    return endPointDecoration;
  }

  public void setEndPointDecoration(SectionDecoration endPointDecoration) {
    this.endPointDecoration = endPointDecoration;
  }

  public int getLength() {
    if (isVertical()) {
      return Math.abs(startPoint.getTop() - endPoint.getTop());
    } else {
      return Math.abs(startPoint.getLeft() - endPoint.getLeft());
    }
  }

  public void makeNotDragable() {

    this.sectionDragController.makeNotDraggable(this);
  }
  
  public int getCurrentTop() {
    WidgetLocation location = new WidgetLocation(this, this.getParent());
    if (isHorizontal()) {
      return location.getTop() + TRANSPARENT_MARGIN_SIZE;
    } else {
      return location.getTop();
    }
  }
  
  public int getCurrentLeft() {
    WidgetLocation location = new WidgetLocation(this, this.getParent());
    if (isVertical()) {
      return location.getLeft() + TRANSPARENT_MARGIN_SIZE;
    } else {
      return location.getLeft();
    }
  }
  
  public void setPosition(int left, int top) {
    updateEndPointsPositions(left, top);
    updateWidgetPosition();
    if (startPointDecoration != null) {
      startPointDecoration.update(calculateStartPointDecorationDirection(),
          startPoint.getLeft(), startPoint.getTop());
    }
    if (endPointDecoration != null) {
      endPointDecoration.update(calculateEndPointDecorationDirection(), 
          endPoint.getLeft(), endPoint.getTop());
    }
  }

  /**
   * Updates start and end point position after {@link Shape} move
   * @param sectionLeft
   * @param sectionTop
   */
  public void updateEndPointsPositions(int sectionLeft, int sectionTop) {
    Position startPosition = new Position();
    Position endPosition = new Position();
    int length = getLength();
    if (isHorizontal()) {
      if (startPoint.getLeft() < endPoint.getLeft()) {
        startPosition.setLeft(sectionLeft);
        endPosition.setLeft(sectionLeft + length);
      } else {
        startPosition.setLeft(sectionLeft + length);
        endPosition.setLeft(sectionLeft);
      }
      startPosition.setTop(sectionTop);
      endPosition.setTop(sectionTop);
    } else {
      if (startPoint.getTop() < endPoint.getTop()) {
        startPosition.setTop(sectionTop);
        endPosition.setTop(sectionTop + length);
      } else {
        startPosition.setTop(sectionTop + length);
        endPosition.setTop(sectionTop);
      }
      startPosition.setLeft(sectionLeft);
      endPosition.setLeft(sectionLeft);
    }
  }
  
  public void updateWidgetPosition() {
    updateWidgetPosition(isHorizontal());
  }
  
  /**
   * Updates {@link Shape} position on boundary panel based on start and end points.
   * @param horizontal true if section is horizontal
   */
  public void updateWidgetPosition(boolean horizontal) {
    if (horizontal) {
      WidgetUtils.setWidgetPosition(((AbsolutePanel) this.getParent()), this, 
          Math.min(this.startPoint.getLeft(), this.endPoint.getLeft()),
          this.startPoint.getTop() - TRANSPARENT_MARGIN_SIZE);
    } else {
      WidgetUtils.setWidgetPosition(((AbsolutePanel) this.getParent()), this, 
          this.startPoint.getLeft() - TRANSPARENT_MARGIN_SIZE, 
          Math.min(this.startPoint.getTop(), this.endPoint.getTop()));
    }
  }
  
  public String toDebugString() {

    StringBuilder builder = new StringBuilder();
    builder.append("Start : ");
    builder.append("top-");
    builder.append(startPoint.getTop());
    builder.append(" left-");
    builder.append(startPoint.getLeft());
    builder.append(" End : ");
    builder.append("top-");
    builder.append(endPoint.getTop());
    builder.append(" left-");
    builder.append(endPoint.getLeft());
    builder.append(" isHorizontal-");
    builder.append(isHorizontal());
    return builder.toString();
  }
}
