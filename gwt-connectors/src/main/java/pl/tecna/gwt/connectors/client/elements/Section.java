package pl.tecna.gwt.connectors.client.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.CornerPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.drag.AxisXYDragController;
import pl.tecna.gwt.connectors.client.elements.SectionDecoration.DecorationDirection;
import pl.tecna.gwt.connectors.client.listeners.event.ConnectorClickEvent;
import pl.tecna.gwt.connectors.client.listeners.event.ConnectorDoubleClickEvent;
import pl.tecna.gwt.connectors.client.listeners.event.ElementDragEvent;
import pl.tecna.gwt.connectors.client.util.BaseCoordinates;
import pl.tecna.gwt.connectors.client.util.ConnectorStyle;
import pl.tecna.gwt.connectors.client.util.WidgetUtils;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

public class Section extends HTML {

  private final Logger LOG = Logger.getLogger("Section");

  public EndPoint startCoordinates;
  public EndPoint endCoordinates;
  public Connector connector;

  public SectionDecoration startPointDecoration;
  public SectionDecoration endPointDecoration;

  private int height;
  private int width;

  private final int additionalHeight = 2;
  private final int additionalWidth = 0;

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
  public Section(EndPoint startPoint, EndPoint endPoint, Connector connector) throws IllegalArgumentException {
    super();

    // this.sinkEvents(Event.ONMOUSEDOWN);
    // this.unsinkEvents(Event.ONDBLCLICK);
    // this.unsinkEvents(Event.ONCLICK);

    this.connector = connector;

    this.startCoordinates = startPoint;
    this.endCoordinates = endPoint;

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
        this.setHTML(selectedVerticalLine(this.height + additionalHeight, style));
      } else {
        this.setHTML(verticalLine(this.height + additionalHeight, style));
      }
      allowHorizontalDragging = true;
    } else if (isHorizontal()) {
      if (isSelected) {
        this.setHTML(selectedHorizontalLine(this.width + additionalWidth, style));
      } else {
        this.setHTML(horizontalLine(this.width + additionalWidth, style));
      }
      allowVerticalDragging = true;
    }

    // Set Section's cursor
    if (isVertical()) {
      DOM.setStyleAttribute(this.getElement(), "cursor", "w-resize");
    } else if (isHorizontal()) {
      DOM.setStyleAttribute(this.getElement(), "cursor", "n-resize");
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
            ArrayList<BaseCoordinates> newCornerPoints = new ArrayList<BaseCoordinates>();
            EndPoint sp = Section.this.startCoordinates;
            EndPoint ep = Section.this.endCoordinates;
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
          LOG.info("Section drag start error " + e.getMessage());
          e.printStackTrace();
        }
        try {
          super.dragStart();
        } catch (Exception e) {
          LOG.info("Section (super) drag start error " + e.getMessage());
          e.printStackTrace();
        }
      }

      @Override
      public void dragMove() {
        try {

          if (isAllowHorizontalDragging()) {
            if (Section.this.startCoordinates.getLeft() < Section.this.endCoordinates.getLeft()) {
              Section.this.startCoordinates.setLeft(context.draggable.getAbsoluteLeft()
                  - context.boundaryPanel.getAbsoluteLeft());
              Section.this.endCoordinates.setLeft(context.draggable.getAbsoluteLeft()
                  - context.boundaryPanel.getAbsoluteLeft() + width);
            } else {
              Section.this.startCoordinates.setLeft(context.draggable.getAbsoluteLeft()
                  - context.boundaryPanel.getAbsoluteLeft() + width);
              Section.this.endCoordinates.setLeft(context.draggable.getAbsoluteLeft()
                  - context.boundaryPanel.getAbsoluteLeft());
            }
          }

          if (isAllowVerticalDragging()) {
            if (Section.this.startCoordinates.getTop() < Section.this.endCoordinates.getTop()) {
              Section.this.startCoordinates.setTop(context.draggable.getAbsoluteTop()
                  - context.boundaryPanel.getAbsoluteTop());
              Section.this.endCoordinates.setTop(context.draggable.getAbsoluteTop() - context.boundaryPanel.getAbsoluteTop()
                  + height);
            } else {
              Section.this.startCoordinates.setTop(context.draggable.getAbsoluteTop()
                  - context.boundaryPanel.getAbsoluteTop() + height);
              Section.this.endCoordinates.setTop(context.draggable.getAbsoluteTop() - context.boundaryPanel.getAbsoluteTop());
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
            startPointDecoration.update(calculateStartPointDecorationDirection(), startCoordinates.getLeft(), startCoordinates
                .getTop());
          }
          if (endPointDecoration != null) {
            endPointDecoration.update(calculateEndPointDecorationDirection(), endCoordinates.getLeft(), endCoordinates.getTop());
          }
        } catch (Exception e) {
          LOG.info("Section drag move error " + e.getMessage());
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
            if (startCoordinates.getTop() > endCoordinates.getTop()) {
              desiredTop = endCoordinates.getTop();
            } else {
              desiredTop = startCoordinates.getTop();
            }
          }
          if (isAllowVerticalDragging()) {
            if (startCoordinates.getLeft() > endCoordinates.getLeft()) {
              desiredLeft = endCoordinates.getLeft();
            } else {
              desiredLeft = startCoordinates.getLeft();
            }
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
          LOG.info("Section (super) drag move error " + e.getMessage());
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
                Section.this.startCoordinates = Section.this.connector.getPrevSection(Section.this).startCoordinates;
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
                Section.this.endCoordinates = Section.this.connector.getNextSection(Section.this).endCoordinates;
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

    // Add line to given panel
    WidgetUtils.addWidget(panel, this, Math.min(this.startCoordinates.getLeft(), this.endCoordinates.getLeft()), 
        Math.min(this.startCoordinates.getTop(),this.endCoordinates.getTop()));
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
      this.startPointDecoration.showOnDiagram(panel, calculateStartPointDecorationDirection(), startCoordinates.getLeft(),
          startCoordinates.getTop());
    }
    if (endPointDecoration != null) {
      this.endPointDecoration.showOnDiagram(panel, calculateEndPointDecorationDirection(), endCoordinates.getLeft(), endCoordinates
          .getTop());
    }
  }

  public DecorationDirection calculateEndPointDecorationDirection() {
    if (isHorizontal()) {
      if (this.endCoordinates.getLeft() < this.startCoordinates.getLeft()) {
        return DecorationDirection.HORIZONTAL_LEFT;
      } else {
        return DecorationDirection.HORIZONTAL_RIGHT;
      }
    } else if (isVertical()) {
      if (this.endCoordinates.getTop() < this.startCoordinates.getTop()) {
        return DecorationDirection.VERTICAL_UP;
      } else {
        return DecorationDirection.VERTICAL_DOWN;
      }
    }
    return DecorationDirection.HORIZONTAL_LEFT;
  }

  public DecorationDirection calculateStartPointDecorationDirection() {
    if (isHorizontal()) {
      if (this.startCoordinates.getLeft() < this.endCoordinates.getLeft()) {
        return DecorationDirection.HORIZONTAL_LEFT;
      } else {
        return DecorationDirection.HORIZONTAL_RIGHT;
      }
    } else if (isVertical()) {
      if (this.startCoordinates.getTop() < this.endCoordinates.getTop()) {
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
    if ((this.startCoordinates.getLeft() == this.endCoordinates.getLeft())
        && (this.startCoordinates.getTop() == this.endCoordinates.getTop())) {
      return true;
    } else {
      return false;
    }
  }

  protected boolean startPointIsGluedToConnectionPoint() {
    if (Section.this.startCoordinates instanceof EndPoint) {
      if (((EndPoint) Section.this.startCoordinates).isGluedToConnectionPoint()) {
        return true;
      }
    }
    return false;
  }

  protected boolean endPointIsGluedToConnectionPoint() {
    if (Section.this.endCoordinates instanceof EndPoint) {
      if (((EndPoint) Section.this.endCoordinates).isGluedToConnectionPoint()) {
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
  protected void splitSection(ArrayList<BaseCoordinates> newCornerPoints) {

    if (this.startPointIsGluedToConnectionPoint()) {
      // Add a new horizontal Section as the first Section in Connector and move decorations into
      // this section
      Section s1 = new Section(this.startCoordinates, newCornerPoints.get(0), this.connector);
      s1.setStartPointDecoration(this.startPointDecoration);
      this.startPointDecoration = null;
      s1.showOnDiagram(this.connector.diagram);
      this.connector.sections.add(0, s1);

      // Add a new vertical section as the second in Connector
      Section s2 = new Section(newCornerPoints.get(0), newCornerPoints.get(1), this.connector);
      s2.showOnDiagram(this.connector.diagram);
      this.connector.sections.add(1, s2);

      // Reconnect dragged Section to the second CornerPoint
      this.startCoordinates = newCornerPoints.get(1);
      this.update();
    }

    if (this.endPointIsGluedToConnectionPoint()) {
      // Add a new vertical Section as the last but one Section in Connector
      Section s1 = new Section(newCornerPoints.get(0), newCornerPoints.get(1), this.connector);
      s1.showOnDiagram(this.connector.diagram);
      this.connector.sections.add(s1);

      // Add a new horizontal section as the last in Connector and move decorations into this
      // section
      Section s2 = new Section(newCornerPoints.get(1), this.endCoordinates, this.connector);
      s2.setEndPointDecoration(this.endPointDecoration);
      this.endPointDecoration = null;
      s2.showOnDiagram(this.connector.diagram);
      this.connector.sections.add(s2);

      // Reconnect dragged Section to the first CornerPoint
      this.endCoordinates = newCornerPoints.get(0);
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
    return "<div style=\"border-left:2px " + style.name().toLowerCase() + " #B2B2B2; height:" + height + "px\">";
  }

  private String horizontalLine(int width, ConnectorStyle style) {
    return "<div style=\"border-top:2px " + style.name().toLowerCase() + " #B2B2B2; width:" + width + "px\">";
  }

  private String selectedVerticalLine(int height, ConnectorStyle style) {
    return "<div style=\"border-left:2px " + style.name().toLowerCase() + " #00BFFF; height:" + height + "px\">";
  }

  private String selectedHorizontalLine(int width, ConnectorStyle style) {
    return "<div style=\"border-top:2px " + style.name().toLowerCase() + " #00BFFF; width:" + width + "px\">";
  }

  /**
   * Updates section displayed on a diagram. Recalculates new position and size of the Section. Also
   * sets the functionality of the horizontal or vertical dragging.
   */
  public void update() {
    try {

      this.height = Math.abs(endCoordinates.getTop() - startCoordinates.getTop());
      this.width = Math.abs(endCoordinates.getLeft() - startCoordinates.getLeft());

      if (isVertical()) {

        this.setHTML(verticalLine(this.height + additionalHeight, style));

        sectionDragController.setAllowHorizontalDragging(true);
        sectionDragController.setAllowVerticalDragging(false);

        WidgetUtils.setWidgetPosition(((AbsolutePanel) this.getParent()), this, this.startCoordinates.getLeft(), Math.min(this.startCoordinates
            .getTop(), this.endCoordinates.getTop()));

      } else if (isHorizontal()) {
        if (this.connector.isSelected) {
          this.setHTML(selectedHorizontalLine(this.width + additionalWidth, style));
        } else {
          this.setHTML(horizontalLine(this.width + additionalWidth, style));
        }

        sectionDragController.setAllowHorizontalDragging(false);
        sectionDragController.setAllowVerticalDragging(true);

        WidgetUtils.setWidgetPosition(((AbsolutePanel) this.getParent()), this, Math.min(this.startCoordinates.getLeft(), this.endCoordinates
            .getLeft()), this.endCoordinates.getTop());
      }

      // Calculate decoration's direction and update decorations
      if (startPointDecoration != null) {
        this.startPointDecoration.update(calculateStartPointDecorationDirection(), startCoordinates.getLeft(), startCoordinates
            .getTop());
      }
      if (endPointDecoration != null) {
        this.endPointDecoration.update(calculateEndPointDecorationDirection(), endCoordinates.getLeft(), endCoordinates.getTop());
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

  public EndPoint getStartPoint() {
    return startCoordinates;
  }

  public void setStartPoint(EndPoint startPoint) {
    this.startCoordinates = startPoint;
  }

  public EndPoint getEndPoint() {
    return endCoordinates;
  }

  public void setEndPoint(EndPoint endPoint) {
    this.endCoordinates = endPoint;
  }

  public boolean isVertical() {
    return isVertical(new ArrayList<Section>());
  }

  public boolean isVertical(List<Section> checkedSections) {
    checkedSections.add(this);
    if (!(this.hasNoDimensions())) {
      if (this.startCoordinates.getLeft().intValue() == this.endCoordinates.getLeft().intValue()) {
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
      if (this.startCoordinates.getTop().intValue() == this.endCoordinates.getTop().intValue()) {
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
      return Math.abs(startCoordinates.getTop() - endCoordinates.getTop());
    } else {
      return Math.abs(startCoordinates.getLeft() - endCoordinates.getLeft());
    }
  }

  public void makeNotDragable() {

    this.sectionDragController.makeNotDraggable(this);
  }

  public String toDebugString() {

    StringBuilder builder = new StringBuilder();
    builder.append("Start : ");
    builder.append("top-");
    builder.append(startCoordinates.getTop());
    builder.append(" left-");
    builder.append(startCoordinates.getLeft());
    builder.append(" End : ");
    builder.append("top-");
    builder.append(endCoordinates.getTop());
    builder.append(" left-");
    builder.append(endCoordinates.getLeft());
    builder.append(" isHorizontal-");
    builder.append(isHorizontal());
    return builder.toString();
  }
}
