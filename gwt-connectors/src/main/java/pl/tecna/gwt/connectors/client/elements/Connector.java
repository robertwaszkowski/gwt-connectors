package pl.tecna.gwt.connectors.client.elements;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.CornerPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.Point;
import pl.tecna.gwt.connectors.client.listeners.ConnectorListener;
import pl.tecna.gwt.connectors.client.listeners.event.ConnectorClickEvent;
import pl.tecna.gwt.connectors.client.listeners.event.ConnectorDoubleClickEvent;
import pl.tecna.gwt.connectors.client.listeners.event.DiagramAddEvent;
import pl.tecna.gwt.connectors.client.listeners.event.DiagramRemoveEvent;
import pl.tecna.gwt.connectors.client.util.ConnectorStyle;
import pl.tecna.gwt.connectors.client.util.SectionData;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class Connector implements Element {
  public static final int OVERLAP_MARGIN = 5;

  private final Logger LOG = Logger.getLogger("Connector");
  
  /**
   * Connector is in 'initializing' state while it is dragged from 
   * shape
   */
  public boolean initalizing = false;
  
  public ArrayList<Section> sections;
  public ArrayList<CornerPoint> cornerPoints;

  public boolean isSelected = false;

  public EndPoint startEndPoint;
  public EndPoint endEndPoint;
  public Diagram diagram;
  public SectionDecoration startPointDecoration;
  public SectionDecoration endPointDecoration;

  public List<SectionData> savedSectionsData;

  public ConnectorStyle style = ConnectorStyle.SOLID;

  private List<ConnectorListener> listeners = new ArrayList<ConnectorListener>();

  private final int sectionMargin = 20;
  private final int lastSectionTolerance = 10;

  /**
   * Connector is a rectilinear connection that connects two {@link EndPoint}. Connector is made of
   * a couple of {@link Section} and it always starts and ends on {@link EndPoint}.
   * 
   * @param startLeft a left position of the point where the Connector starts
   * @param startTop a top position of the point where the Connector starts
   * @param endLeft a left position of the point where the Connector ends
   * @param endTop a top position of the point where the Connector ends
   */
  public Connector(double startLeft, double startTop, double endLeft, double endTop) {
    super();
    init(startLeft, startTop, endLeft, endTop, null, null, new ArrayList<CornerPoint>());
  }

  /**
   * Connector is a rectilinear connection that connects two {@link EndPoint}. Connector is made of
   * a couple of {@link Section} and it always starts and ends on {@link EndPoint}.
   * 
   * @param startLeft a left position of the point where the Connector starts
   * @param startTop a top position of the point where the Connector starts
   * @param endLeft a left position of the point where the Connector ends
   * @param endTop a top position of the point where the Connector ends
   * @param cornerPoints a list of corner points of the Connector
   */
  public Connector(double startLeft, double startTop, double endLeft, double endTop, ArrayList<CornerPoint> cornerPoints) {

    super();
    init(startLeft, startTop, endLeft, endTop, null, null, cornerPoints);
  }

  /**
   * Connector is a rectilinear connection that connects two {@link EndPoint}. Connector is made of
   * a couple of {@link Section} and it always starts and ends on {@link EndPoint}.
   * 
   * @param startLeft a left position of the point where the Connector starts
   * @param startTop a top position of the point where the Connector starts
   * @param endLeft a left position of the point where the Connector ends
   * @param endTop a top position of the point where the Connector ends
   * @param cornerPoints a list of corner points of the Connector
   * @param startDecoration a way the connector is decorated at its start
   * @param endDecoration a way the connector is decorated at its end
   */
  public Connector(double startLeft, double startTop, double endLeft, double endTop, ArrayList<CornerPoint> cornerPoints,
      SectionDecoration startDecoration, SectionDecoration endDecoration) {

    super();
    init(startLeft, startTop, endLeft, endTop, startDecoration, endDecoration, cornerPoints);
  }

  /**
   * Connector is a rectilinear connection that connects two {@link EndPoint}. Connector is made of
   * a couple of {@link Section} and it always starts and ends on {@link EndPoint}.
   * 
   * @param startLeft a left position of the point where the Connector starts
   * @param startTop a top position of the point where the Connector starts
   * @param endLeft a left position of the point where the Connector ends
   * @param endTop a top position of the point where the Connector ends
   * @param startDecoration a way the connector is decorated at its start
   * @param endDecoration a way the connector is decorated at its end
   */
  public Connector(double startLeft, double startTop, double endLeft, double endTop, SectionDecoration startDecoration,
      SectionDecoration endDecoration) {

    super();
    init(startLeft, startTop, endLeft, endTop, startDecoration, endDecoration, new ArrayList<CornerPoint>());
  }

  public Connector(double startLeft, double startTop, double endLeft, double endTop, SectionDecoration startDecoration,
      SectionDecoration endDecoration, EndPoint endEndPoint, Diagram diagram, ConnectorStyle style) {

    this.style = style;
    this.startEndPoint = new EndPoint(startLeft, startTop, this);
    this.endEndPoint = endEndPoint;
    endEndPoint.initPosition(endLeft, endTop);
    this.startEndPoint.connector = this;
    this.endEndPoint.connector = this;

    this.sections = new ArrayList<Section>();
    cornerPoints = new ArrayList<CornerPoint>();

    // Add decorations
    this.startPointDecoration = startDecoration;
    this.endPointDecoration = endDecoration;

    // Remember the Diagram
    this.diagram = diagram;

    // Add Connector to the Diagram
    diagram.connectors.add(this);

    // Calculate standard corner points positions
    if (cornerPoints.isEmpty()) {
      calculateStandardPointsPositions();
    }

    // Recreate Sections between start, end, and corner points
    this.drawSections(cornerPoints, isSelected);

    // Set start and end Sections decorated
    try {
      // TODO this is DIRTY FIX, to make it better fix section horizontal or vertical error
      sections.get(0).setStartPointDecoration(this.startPointDecoration);
      sections.get(sections.size() - 1).setEndPointDecoration(this.endPointDecoration);
    } catch (Exception e) {
      LOG.severe("Error while setting decorations" + e.getStackTrace());
    }

    startEndPoint.showOnDiagram(diagram);

    int connectorX = diagram.boundaryPanel.getWidgetLeft(startEndPoint) - diagram.boundaryPanel.getAbsoluteLeft();
    int connectorY = diagram.boundaryPanel.getWidgetTop(startEndPoint) - diagram.boundaryPanel.getAbsoluteTop();
    diagram.onDiagramAdd(new DiagramAddEvent(this, connectorX, connectorY));

  }

  private void init(double startLeft, double startTop, double endLeft, double endTop, SectionDecoration startDecoration,
      SectionDecoration endDecoration, ArrayList<CornerPoint> cornerPoints) {

    this.startEndPoint = new EndPoint(startLeft, startTop, this);
    this.endEndPoint = new EndPoint(endLeft, endTop, this);
    this.startEndPoint.connector = this;
    this.endEndPoint.connector = this;

    this.sections = new ArrayList<Section>();

    // Add decorations
    this.startPointDecoration = startDecoration;
    this.endPointDecoration = endDecoration;

    // TODO Change workaround for trouble of 3 CornerPoint in a row
    // removing neighbour corner points with same coordinates (3 in a row)
    List<CornerPoint> toRemove = new ArrayList<CornerPoint>();
    for (int i = 1; i < cornerPoints.size() - 1; i++) {
      if (cornerPoints.get(i).compareTo(cornerPoints.get(i + 1)) == 0
          && cornerPoints.get(i).compareTo(cornerPoints.get(i - 1)) == 0) {
        toRemove.add(cornerPoints.get(i + 1));
      }
    }
    for (CornerPoint removed : toRemove) {
      cornerPoints.remove(removed);
    }

    if (toRemove.size() > 0) {
      logCornerPointsData(cornerPoints);
    }

    this.cornerPoints = cornerPoints;
  }

  /**
   * Calculates positions of Connector's sections and shows Connector on a given panel. The
   * Connector is represented by horizontal or vertical lines which are {@link Section}s. The panel
   * argument's type must be AbsolutePanel.
   * <p>
   * This method also add all necessary {@link Point}s: {@link EndPoint}s at the beginning and at
   * the end of the Connector and {@link CornerPoint}s at the Connector's corners.
   * <p>
   * The way sections are generated: </br> Let "width" a width of rectangle drown on connectors
   * start point and end point </br> Let "height" a height of rectangle drown on connectors start
   * point and end point </br> If ("width" < "height") the connection contains two vertical sections
   * and one horizontal section. </br> If ("height" < "width") the connection contains two
   * horizontal sections and one vertical section.
   * 
   * @param diagram a Diagram the Connector will be added to
   */
  public void showOnDiagram(Diagram diagram) {

    // Remember the Diagram
    this.diagram = diagram;

    // Add Connector to the Diagram
    diagram.connectors.add(this);

    // Calculate standard corner points positions
    if (cornerPoints.isEmpty()) {
      calculateStandardPointsPositions();
    }

    // Recreate Sections between start, end, and corner points
    this.drawSections(cornerPoints, isSelected);

    // Set start and end Sections decorated
    sections.get(0).setStartPointDecoration(this.startPointDecoration);
    sections.get(sections.size() - 1).setEndPointDecoration(this.endPointDecoration);

    // Show startEndPoint and endEndPoint
    startEndPoint.showOnDiagram(diagram);
    endEndPoint.showOnDiagram(diagram);

    double connectorX = diagram.boundaryPanel.getWidgetLeft(startEndPoint) - diagram.boundaryPanel.getAbsoluteLeft();
    double connectorY = diagram.boundaryPanel.getWidgetTop(startEndPoint) - diagram.boundaryPanel.getAbsoluteTop();
    diagram.onDiagramAdd(new DiagramAddEvent(this, connectorX, connectorY));
  }

  /**
   * Removes Connector from Diagram and from its boundaryPanel.
   * 
   * @param diagram a Diagram the Connector will be removed from
   */
  public void removeFromDiagram(Diagram diagram) {
    removeFromDiagram(diagram, true);
  }

  /**
   * Removes Connector from Diagram and from its boundaryPanel
   * 
   * @param diagram a Diagram the Connector will be removed from
   */
  public void removeFromDiagram(Diagram diagram, boolean fireEvent) {

    // Remove Connector from Diagram
    diagram.connectors.remove(this);

    // Remove Connector from Diagram's boundaryPanel
    for (int i = 0; i < sections.size(); i++) {
      diagram.boundaryPanel.remove(sections.get(i));

    }
    sections.removeAll(sections);

    // Remove connector's decorations
    if (startPointDecoration != null) {
      diagram.boundaryPanel.remove(startPointDecoration);
    }
    if (endPointDecoration != null) {
      diagram.boundaryPanel.remove(endPointDecoration);
    }

    if (startEndPoint.isGluedToConnectionPoint()) {
      startEndPoint.unglueFromConnectionPoint();
    }
    if (endEndPoint.isGluedToConnectionPoint()) {
      endEndPoint.unglueFromConnectionPoint();
    }

    // Remove end points
    startEndPoint.clear();
    endEndPoint.clear();

    if (fireEvent) {
      diagram.onDiagramRemove(new DiagramRemoveEvent(this, -1, -1));
    }
  }

  /**
   * Calculates values of local variables cornerPoint1Left, cornerPoint1Top, cornerPoint2Left,
   * cornerPoint2Top to draw a standard rectilinear tree-sections connector. Points positions
   * depends on connector's width and height. If the width is less than the height the connection
   * contains two vertical sections and one horizontal section. If the width is greater than height
   * the connection contains two horizontal sections and one vertical section.
   */
  public void calculateStandardPointsPositions() {
    
    cornerPoints.clear();

    double distanceX = startEndPoint.getLeft() - endEndPoint.getLeft();
    double distanceY = startEndPoint.getTop() - endEndPoint.getTop();
    
    int firstSectionDirection = -1;
    if (startEndPoint.isGluedToConnectionPoint()) {
      firstSectionDirection = startEndPoint.gluedConnectionPoint.connectionDirection;
    }

    CornerPoint cp1 = new CornerPoint(0, 0);
    CornerPoint cp2 = new CornerPoint(0, 0);
    if (firstSectionDirection == ConnectionPoint.DIRECTION_TOP ||
        firstSectionDirection == ConnectionPoint.DIRECTION_BOTTOM || (
        firstSectionDirection == -1 && Math.abs(distanceX) < Math.abs(distanceY))) {
      // the connection contains two vertical sections and one horizontal section
      cp1.setPosition(startEndPoint.getLeft(), startEndPoint.getTop() - (distanceY / 2.0));
      cp2.setPosition(endEndPoint.getLeft(), cp1.getTop());
    } else {
      // the connection contains two horizontal sections and one vertical section
      cp1.setPosition(startEndPoint.getLeft() - (distanceX / 2), startEndPoint.getTop());
      cp2.setPosition(cp1.getLeft(), endEndPoint.getTop());
    }
    cornerPoints.add(cp1);
    cornerPoints.add(cp2);

  }

  public void updateCornerPoints() {
    // Log.info("updateCornerPoints - Connector - 275");
    this.cornerPoints = (ArrayList<CornerPoint>) getCorners(sections);
  }

  public Section findNeighborSection(Section section, Point point) {
    Section sec = null;
    for (int i = 0; i < sections.size(); i++) {
      if ((sections.get(i) != section)
          && ((sections.get(i).startPoint == point) || (sections.get(i).endPoint == point))) {
        sec = sections.get(i);
      }
    }
    return sec;
  }

  public Section findSectionWithThisStartPoint(Point point) {
    Section sec = null;
    for (int i = 0; i < sections.size(); i++) {
      if (sections.get(i).startPoint == point) {
        sec = sections.get(i);
      }
    }
    return sec;
  }

  public Section findSectionWithThisEndPoint(Point point) {
    Section sec = null;
    for (int i = 0; i < sections.size(); i++) {
      if (sections.get(i).endPoint == point) {
        sec = sections.get(i);
      }
    }
    return sec;
  }

  public Section prevSectionForPoint(Point point) {
    Section sec = null;
    for (int i = 0; i < sections.size(); i++) {
      if (sections.get(i).endPoint == point) {
        sec = sections.get(i);
      }
    }
    return sec;
  }

  public Section nextSectionForPoint(Point point) {
    Section sec = null;
    for (int i = 0; i < sections.size(); i++) {
      if (sections.get(i).startPoint == point) {
        sec = sections.get(i);
      }
    }
    return sec;
  }

  public void update() {
    for (int i = 0; i < sections.size(); i++) {
      sections.get(i).update();
    }
  }

  public boolean isOnDiagram(AbsolutePanel panel) {
    if (this.diagram == null) {
      return false;
    }
    if (panel == this.diagram.boundaryPanel) {
      return true;
    } else {
      return false;
    }
  }

  public Section getPrevSection(Section currentSection) {

    if (this.sections.indexOf(currentSection) > 0) {
      return this.sections.get(this.sections.indexOf(currentSection) - 1);
    } else {
      return null;
    }
  }

  public Section getNextSection(Section currentSection) {

    if (this.sections.size() > this.sections.indexOf(currentSection) + 1) {
      return this.sections.get(this.sections.indexOf(currentSection) + 1);
    } else {
      return null;
    }
  }

  /**
   * Disconnects {@link Connector} end point (if attached), and creates dragable {@link EndPoint}
   */
  public void disconnectEnd() {
    if (endEndPoint.isGluedToConnectionPoint()) {
      double left = endEndPoint.getLeft();
      double top = endEndPoint.getTop();
      endEndPoint.unglueFromConnectionPoint();
      endEndPoint = new EndPoint(left, top, this);
      endEndPoint.showOnDiagram(diagram);

      try {
        Section endSection = sections.get(sections.size() - 1);
        endSection.setEndPoint(endEndPoint);
      } catch (IndexOutOfBoundsException e) {
        LOG.log(Level.SEVERE, "Error disconnecting end point : no end section", e);
      }
    }
  }

  /**
   * Disconnects {@link Connector} start point (if attached), and creates dragable {@link EndPoint}
   */
  public void disconnectStart() {
    if (startEndPoint.isGluedToConnectionPoint()) {
      double left = startEndPoint.getLeft();
      double top = startEndPoint.getTop();
      startEndPoint.unglueFromConnectionPoint();
      startEndPoint = new EndPoint(left, top, this);
      startEndPoint.showOnDiagram(diagram);

      try {
        Section startSection = sections.get(0);
        startSection.setStartPoint(startEndPoint);
      } catch (IndexOutOfBoundsException e) {
        LOG.log(Level.SEVERE, "Error disconnecting start point : no start section", e);
      }
    }
  }

  /**
   * Creates new {@link Section} when {@link Connector} is connected to the {@link ConnectionPoint}
   * </br> Section is created when direction of last {@link Section} is wrong (horizontal when
   * {@link ConnectionPoint} is on top or bottom of {@link Shape} or vertical when
   * {@link ConnectionPoint} is on left or right of {@link Shape}
   * 
   * @param endPoint
   */
  public boolean fixEndSectionDirection(EndPoint endPoint) {
    // end point must be connected
    if (!endPoint.isGluedToConnectionPoint()) {
      LOG.severe("End point is not glued to the connection point");
      return false;
    }

    // defines if endPoint is end or start of connector
    boolean last = false;
    if (this.endEndPoint.equals(endPoint)) {
      last = true;
    } else {
      last = false;
    }

    // defines if section is horizontal or vertical
    boolean sectionHorizontal = true;
    if (last) {
      sectionHorizontal = sections.get(sections.size() - 1).isHorizontal();
    } else {
      sectionHorizontal = sections.get(0).isHorizontal();
    }

    try {
      List<CornerPoint> cornerPoints = getCorners(sections);
      ConnectionPoint connectionPoint = endPoint.gluedConnectionPoint;

      // Connect element to the center of connection point
      if (last) {
        endEndPoint.setPosition(connectionPoint.getCenterLeft(), connectionPoint.getCenterTop());
        if (!sectionHorizontal) {
          cornerPoints.get(cornerPoints.size() - 1).setLeftPosition(endEndPoint.getLeft());
        } else {
          cornerPoints.get(cornerPoints.size() - 1).setTopPosition(endEndPoint.getTop());
        }
      } else {
        startEndPoint.setPosition(connectionPoint.getCenterLeft(), connectionPoint.getCenterTop());
        if (!sectionHorizontal) {
          cornerPoints.get(0).setLeftPosition(startEndPoint.getLeft());
        } else {
          cornerPoints.get(0).setTopPosition(startEndPoint.getTop());
        }
      }

      if (((connectionPoint.connectionDirection == ConnectionPoint.DIRECTION_BOTTOM || connectionPoint.connectionDirection == ConnectionPoint.DIRECTION_TOP))
          && !sectionHorizontal) {
        drawSections(cornerPoints, isSelected);
        return false;
      } else if (((connectionPoint.connectionDirection == ConnectionPoint.DIRECTION_LEFT || connectionPoint.connectionDirection == ConnectionPoint.DIRECTION_RIGHT))
          && sectionHorizontal) {
        drawSections(cornerPoints, isSelected);
        return false;
      }

      // Last or first cornerPont, depends on endPoint place (start or end of Connector)
      CornerPoint extremeCorner = null;

      // New cornerPoint which is added to make last section perpendicular to shape
      CornerPoint newCornerPoint = null;

      // Creating new CornerPoint
      if (connectionPoint.connectionDirection == ConnectionPoint.DIRECTION_LEFT
          || connectionPoint.connectionDirection == ConnectionPoint.DIRECTION_RIGHT) {
        // Horizontal
        if (last) {
          extremeCorner = cornerPoints.get(cornerPoints.size() - 1);
        } else {
          extremeCorner = cornerPoints.get(0);
        }

        if (connectionPoint.connectionDirection == ConnectionPoint.DIRECTION_RIGHT) {
          extremeCorner.setLeftPosition(extremeCorner.getLeft() + sectionMargin);
        } else {
          extremeCorner.setLeftPosition(extremeCorner.getLeft() - sectionMargin);
        }

        if (last) {
          newCornerPoint = new CornerPoint(extremeCorner.getLeft(), endEndPoint.getTop());
        } else {
          newCornerPoint = new CornerPoint(extremeCorner.getLeft(), startEndPoint.getTop());
        }
      } else if (connectionPoint.connectionDirection == ConnectionPoint.DIRECTION_BOTTOM
          || connectionPoint.connectionDirection == ConnectionPoint.DIRECTION_TOP) {
        // Vertical
        if (last) {
          extremeCorner = cornerPoints.get(cornerPoints.size() - 1);
        } else {
          extremeCorner = cornerPoints.get(0);
        }
        if (connectionPoint.connectionDirection == ConnectionPoint.DIRECTION_BOTTOM) {
          extremeCorner.setTopPosition(extremeCorner.getTop() + sectionMargin);
        } else {
          extremeCorner.setTopPosition(extremeCorner.getTop() - sectionMargin);
        }
        if (last) {
          newCornerPoint = new CornerPoint(endEndPoint.getLeft(), extremeCorner.getTop());
        } else {
          newCornerPoint = new CornerPoint(startEndPoint.getLeft(), extremeCorner.getTop());
        }
      }

      if (newCornerPoint != null) {
        if (last) {
          cornerPoints.add(newCornerPoint);
        } else {
          cornerPoints.add(0, newCornerPoint);
        }
      }

      drawSections(cornerPoints, isSelected);
      return true;
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Unexpected exception", e);
      return false;
    }
  }

  /**
   * Retrieve list of {@link CornerPoint}'s from list of {@link Section} (without {@link Connector}
   * end points)
   * 
   * @param sectionList
   * @return the list of corner points
   */
  public List<CornerPoint> getCorners(List<Section> sectionList) {
    List<CornerPoint> retList = new ArrayList<CornerPoint>();
    for (int i = 0; i < sectionList.size() - 1; i++) {
      retList.add(new CornerPoint(sectionList.get(i).endPoint.getLeft(), sectionList.get(i).endPoint.getTop()));
    }

    return retList;
  }

  /**
   * Retrieve list of {@link CornerPoint}'s from list of {@link Section} of this {@link Connector}
   * (without {@link Connector}'s end points)
   * 
   * @return the list of corner points
   */
  public List<CornerPoint> getCorners() {
    return getCorners(sections);
  }

  public void drawSections(List<CornerPoint> cp, EndPoint startEndPoint, EndPoint endEndPoint) {
    this.startEndPoint = startEndPoint;
    this.endEndPoint = endEndPoint;

    drawSections(cp, isSelected);
  }

  public void drawSections() {
    drawSections(cornerPoints, isSelected);
  }

  /**
   * Removes old connector's sections from diagram, and shows new ones on diagram depending on old
   * sections data and given list of {@link CornerPoint}
   * 
   * @param cp list of {@link CornerPoint} containing {@link Connector} shape
   */
  public void drawSections(List<CornerPoint> cp) {
    drawSections(cp, isSelected);
  }

  /**
   * Removes old connector's sections from diagram, and shows new ones on diagram depending on old
   * sections data and given list of {@link CornerPoint}
   * 
   * @param cp list of {@link CornerPoint} containing {@link Connector} shape
   */
  public void drawSections(List<CornerPoint> cp, boolean isSelected) {
    this.cornerPoints = (ArrayList<CornerPoint>) cp;
    // logCornerPointsData();

    try {
      for (Section section : sections) {
        // LOG.i(section.toDebugString());
        section.removeFromDiagram();
      }

      sections.clear();

      if (cornerPoints.size() == 0) {
        Section section = new Section(startEndPoint, endEndPoint, this);
        sections.add(section);
      } else {
        Section startSection = new Section(startEndPoint, cp.get(0), this);
        if (this.startPointDecoration != null) {
          startSection.startPointDecoration = startPointDecoration;
        }
        sections.add(startSection);

        for (int i = 0; i < cp.size() - 1; i++) {
          sections.add(new Section(cp.get(i), cp.get(i + 1), this));
        }

        Section endSection = new Section(cp.get(cp.size() - 1), endEndPoint, this);
        if (this.endPointDecoration != null) {
          endSection.endPointDecoration = this.endPointDecoration;
        }
        sections.add(endSection);
      }

      for (Section section : sections) {
        section.showOnDiagram(diagram, isSelected, style);
      }
      refreshCursorStyles();
    } catch (IllegalArgumentException e) {
      logCornerPointsData();
      this.calculateStandardPointsPositions();
      LOG.log(Level.SEVERE, "Section must be horizontal or vertical, calculating standard connection points", e);
    }
  }

  public void select() {
    this.isSelected = true;

    for (Section section : sections) {
      section.select();
    }
  }

  public void deselect() {
    this.isSelected = false;

    for (Section section : sections) {
      section.deselect();
    }
  }

  /**
   * Merges two {@link Section} on end of {@link Connector} </br> {@link Connector} sections length
   * must be greater equals 3, and last section length must be lesser than defined length (default -
   * 10)
   * 
   * @param endSection last {@link Section} of {@link Connection}
   */
  public boolean mergeTwoLastSections(Section endSection, List<CornerPoint> cornerPoints) {
    int connDirection = this.endEndPoint.gluedConnectionPoint.connectionDirection;

    if (!this.endEndPoint.isGluedToConnectionPoint() || endSection.getLength() > lastSectionTolerance
        || this.sections.size() < 2 || cornerPoints.size() < 2) {
      return false;
    }

    CornerPoint beforeLast = cornerPoints.get(cornerPoints.size() - 2);
    CornerPoint newCorner = null;

    switch (connDirection) {
      case 1:
        if (endSection.isHorizontal()) {
          newCorner = new CornerPoint(endSection.endPoint.getLeft(), beforeLast.getTop());
        }
        break;

      case 2:
        if (endSection.isVertical()) {
          newCorner = new CornerPoint(beforeLast.getLeft(), endSection.endPoint.getTop());
        }
        break;

      case 3:
        if (endSection.isHorizontal()) {
          newCorner = new CornerPoint(endSection.endPoint.getLeft(), beforeLast.getTop());
        }
        break;

      case 4:
        if (endSection.isVertical()) {
          newCorner = new CornerPoint(beforeLast.getLeft(), endSection.endPoint.getTop());
        }
        break;

    }
    if (newCorner != null) {
      cornerPoints.remove(cornerPoints.size() - 1);
      cornerPoints.remove(cornerPoints.size() - 1);
      cornerPoints.add(newCorner);
      drawSections(cornerPoints, isSelected);
      return true;
    } else {
      LOG.severe("New corner is null");
    }

    return false;
  }

  /**
   * Merges two {@link Section} on start of {@link Connector} </br> {@link Connector} sections
   * length must be greater equals 3, and first section length must be lesser than defined length
   * (default - 10)
   */
  public boolean mergeTwoFirstSections(Section startSection, List<CornerPoint> cornerPoints) {
    int connDirection = this.startEndPoint.gluedConnectionPoint.connectionDirection;

    if (!this.startEndPoint.isGluedToConnectionPoint() || startSection.getLength() > lastSectionTolerance
        || this.sections.size() < 2 || cornerPoints.size() < 2) {
      return false;
    }

    CornerPoint second = cornerPoints.get(1);

    CornerPoint newCorner = null;
    switch (connDirection) {
      case 1:
        if (startSection.isHorizontal()) {
          newCorner = new CornerPoint(startSection.startPoint.getLeft(), second.getTop());
        }
        break;

      case 2:
        if (startSection.isVertical()) {
          newCorner = new CornerPoint(second.getLeft(), startSection.startPoint.getTop());
        }
        break;

      case 3:
        if (startSection.isHorizontal()) {
          newCorner = new CornerPoint(startSection.startPoint.getLeft(), second.getTop());
        }
        break;

      case 4:
        if (startSection.isVertical()) {
          newCorner = new CornerPoint(second.getLeft(), startSection.startPoint.getTop());
        }
        break;
    }

    if (newCorner != null) {
      cornerPoints.remove(0);
      cornerPoints.remove(0);
      cornerPoints.add(0, newCorner);
      drawSections(cornerPoints, isSelected);
      return true;
    }

    return false;
  }

  /**
   * Check if some sections in connection are mergeable (some section's length is shorter than
   * defined value) and merges them.
   * 
   * @return true, if some sections were merged
   */
  public boolean fixSections() {
    try {
      if (!fixOverlapSections()) {
        fixLineSections(cornerPoints);
        drawSections(cornerPoints, isSelected);
      }
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "fixSections error :", e);
    }

    return true;
  }

  public void logCornerPointsData() {
    LOG.info("--- Start end point : top:" + startEndPoint.getTop() + " left:" + startEndPoint.getLeft());

    if (cornerPoints != null && cornerPoints.size() != 0) {
      for (CornerPoint cp : cornerPoints) {
        LOG.info("--- CornerPoint top:" + cp.getTop() + " left:" + cp.getLeft());
      }
    }

    LOG.info("--- End end point : top:" + endEndPoint.getTop() + " left:" + endEndPoint.getLeft());
  }

  public void logCornerPointsData(List<CornerPoint> corners) {
    LOG.info("--- Start end point : top:" + startEndPoint.getTop() + " left:" + startEndPoint.getLeft());

    if (corners != null && corners.size() != 0) {
      for (CornerPoint cp : corners) {
        LOG.info("---CornerPoint top:" + cp.getTop() + " left:" + cp.getLeft());
      }
    }

    LOG.info("--- End end point : top:" + endEndPoint.getTop() + " left:" + endEndPoint.getLeft());
  }

  public void logSectionData() {
    LOG.info("---Start end point : top:" + startEndPoint.getTop() + " left:" + startEndPoint.getLeft());

    if (sections != null && sections.size() != 0) {
      for (int i = 0; i < sections.size() - 1; i++) {
        LOG.info("---Section top:" + sections.get(i).endPoint.getTop() + " left:" + sections.get(i).endPoint.getLeft());
      }
    }

    LOG.info("---End end point : top:" + endEndPoint.getTop() + " left:" + endEndPoint.getLeft());
  }

  /**
   * Removes short sections, and merges sections that are in one line
   * 
   * @param corners
   */
  public List<CornerPoint> fixLineSections(List<CornerPoint> corners) {
    if (corners.size() < 3) {
      return corners;
    }

    CornerPoint startCorner = new CornerPoint(startEndPoint.getLeft(), startEndPoint.getTop());
    CornerPoint endCorner = new CornerPoint(endEndPoint.getLeft(), endEndPoint.getTop());
    corners.add(0, startCorner);
    corners.add(endCorner);

    List<CornerPoint> toRemove = new ArrayList<CornerPoint>();
    // fix corners that are in one exact line
    for (int i = 1; i < corners.size() - 1; i++) {
      if (corners.get(i).getLeft() == corners.get(i + 1).getLeft()
          && corners.get(i).getLeft() == corners.get(i - 1).getLeft()) {
        toRemove.add(corners.get(i));
      } else if (corners.get(i).getTop() == corners.get(i + 1).getTop()
          && corners.get(i).getTop() == corners.get(i - 1).getTop()) {
        toRemove.add(corners.get(i));
      }
    }

    for (CornerPoint removed : toRemove) {
      corners.remove(removed);
    }

    boolean allCornersChecked = false;

    while (!allCornersChecked && corners.size() > 4) {
      toRemove = new ArrayList<CornerPoint>();
      boolean findShortSection = true;
      int i = 2;
      CornerPoint toChange = null;
      CornerPoint nextToChanged = null;
      while (i < corners.size() - 1 && findShortSection) {
        if ((Math.abs(corners.get(i - 1).getLeft() - corners.get(i).getLeft()) < Shape.SECTION_TOLERANCE)
            && ((Math.abs(corners.get(i - 1).getTop() - corners.get(i).getTop()) < Shape.SECTION_TOLERANCE))) {

          boolean doProceed = true;
          if (i < corners.size() - 2) {
            toChange = corners.get(i + 1);
            nextToChanged = corners.get(i - 2);
          } else if (i > 2) {
            toChange = corners.get(i - 2);
            nextToChanged = corners.get(i + 1);
          } else {
            LOG.severe("No condition was meet");
            doProceed = false;
          }

          if (doProceed) {
            toRemove.add(corners.get(i - 1));
            toRemove.add(corners.get(i));

            if (Double.compare(toRemove.get(0).getLeft(), toRemove.get(1).getLeft()) == 0) {
              // removed corners points were vertically matched
              toChange.setTopPosition(nextToChanged.getTop());
            } else {
              toChange.setLeftPosition(nextToChanged.getLeft());
            }

            findShortSection = false;
          }
        }

        i++;
      }
      for (CornerPoint removed : toRemove) {
        corners.remove(removed);
      }

      if (i >= corners.size() - 1) {
        allCornersChecked = true;
      }
    }

    corners.remove(startCorner);
    corners.remove(endCorner);

    return corners;
  }

  /**
   * Refresh mouse over styles of containing {@link Section}
   */
  public void refreshCursorStyles() {

    for (Section section : sections) {
      if (section.isVertical()) {
        DOM.setStyleAttribute(section.getElement(), "cursor", "w-resize");
      } else if (section.isHorizontal()) {
        DOM.setStyleAttribute(section.getElement(), "cursor", "n-resize");
      }
    }
  }

  /**
   * If there are {@link Section}'s, that overlap some {@link Shape}, the {@link Shape} is evaded
   */
  public boolean fixOverlapSections() {
    return fixOverlapSections(getCorners());
  }

  /**
   * If there are {@link Section}'s, that overlap some {@link Shape}, the {@link Shape} is evaded
   */
  public boolean fixOverlapSections(List<CornerPoint> corners) {
    boolean result = false;

    for (Shape shape : diagram.shapes) {
      List<Section> overlapSections = shape.overlapSections(this);
      if (overlapSections.size() != 0) {
        result = true;
        evadeShape(shape, overlapSections, corners);
      }
    }

    return result;
  }

  /**
   * Evade {@link Shape} by creating new {@link Section}s which omit given {@link Shape}
   * 
   * @param shape {@link Shape} to omit
   * @param overlapSections {@link Section}s which overlap given {@link Section}s
   */
  public void evadeShape(Shape shape, List<Section> overlapSections, List<CornerPoint> corners) {
    if (shape.isEnableOverlap()) {
      return;
    }

    double shapeLeft = shape.getRelativeShapeLeft();
    double shapeTop = shape.getRelativeShapeTop();
    double shapeRight = shapeLeft + shape.getOffsetWidth();
    double shapeBottom = shapeTop + shape.getOffsetHeight();

    Section first = null;
    Section last = null;
    if (overlapSections.size() == 1) {
      first = overlapSections.get(0);
      last = overlapSections.get(0);
    } else {
      first = overlapSections.get(0);
      last = overlapSections.get(overlapSections.size() - 1);
    }

    int shapeEnterDirection = 0;
    int shapeLeaveDirection = 0;

    /*
     * define from which direction shape is leaved by overlap sections 0 - left 1 - top 2 - right 3
     * - bottom
     */
    if (first.isHorizontal()) {
      if (first.startPoint.getLeft() <= shapeLeft) {
        shapeEnterDirection = 0;
      } else if (first.startPoint.getLeft() >= shapeRight) {
        shapeEnterDirection = 2;
      }
    } else if (first.isVertical()) {
      if (first.startPoint.getTop() <= shapeTop) {
        shapeEnterDirection = 1;
      } else if (first.startPoint.getTop() >= shapeBottom) {
        shapeEnterDirection = 3;
      }
    }

    if (last.isHorizontal()) {
      if (last.endPoint.getLeft() <= shapeLeft) {
        shapeLeaveDirection = 0;
      } else if (last.endPoint.getLeft() >= shapeRight) {
        shapeLeaveDirection = 2;
      }
    } else if (last.isVertical()) {
      if (last.endPoint.getTop() <= shapeTop) {
        shapeLeaveDirection = 1;
      } else if (last.endPoint.getTop() >= shapeBottom) {
        shapeLeaveDirection = 3;
      }
    }

    /*
     * define which way from start section to the end section is shorter : clockwise or not 0 -
     * clockwise 1 - not clockwise
     */
    int direction = 0;
    boolean proceed = true;
    int clockwiseDistance = 0;
    int i = shapeEnterDirection;
    while (proceed) {
      if (i == shapeLeaveDirection) {
        proceed = false;
      } else {
        clockwiseDistance++;
        i++;
        if (i == 4) {
          i = 0;
        }
      }
    }

    int notClockwiseDistance = 0;
    proceed = true;
    i = shapeEnterDirection;
    while (proceed) {
      if (i == shapeLeaveDirection) {
        proceed = false;
      } else {
        notClockwiseDistance++;
        i--;
        if (i == -1) {
          i = 3;
        }
      }
    }

    if (clockwiseDistance < notClockwiseDistance) {
      direction = 0;
    } else if (clockwiseDistance > notClockwiseDistance) {
      direction = 1;
    } else {
      if (first.isHorizontal() && last.isHorizontal()) {
        double topDistance = first.endPoint.getTop() - shapeTop + last.endPoint.getTop() - shapeTop;
        double bottomDistance = shapeBottom - first.endPoint.getTop() + shapeBottom - last.endPoint.getTop();
        if (shapeEnterDirection == 0) {
          if (topDistance >= bottomDistance) {
            direction = 1;
          } else {
            direction = 0;
          }
        } else {
          if (topDistance >= bottomDistance) {
            direction = 0;
          } else {
            direction = 1;
          }
        }
      } else {
        double leftDistance = first.endPoint.getLeft() - shapeLeft + last.endPoint.getLeft() - shapeLeft;
        double rightDistance = shapeRight - first.endPoint.getLeft() + shapeRight - last.endPoint.getLeft();
        if (shapeEnterDirection == 1) {
          if (leftDistance >= rightDistance) {
            direction = 0;
          } else {
            direction = 1;
          }
        } else {
          if (leftDistance >= rightDistance) {
            direction = 1;
          } else {
            direction = 0;
          }
        }
      }
    }

    // Define start connection point
    CornerPoint startCorner = null;
    CornerPoint endCorner = null;

    switch (shapeEnterDirection) {
      case 0:
        startCorner = new CornerPoint(shapeLeft, first.endPoint.getTop());
        break;
      case 1:
        startCorner = new CornerPoint(first.endPoint.getLeft(), shapeTop);
        break;
      case 2:
        startCorner = new CornerPoint(shapeRight, first.endPoint.getTop());
        break;
      case 3:
        startCorner = new CornerPoint(first.endPoint.getLeft(), shapeBottom);
        break;
    }

    // Define leave connection point
    switch (shapeLeaveDirection) {
      case 0:
        endCorner = new CornerPoint(shapeLeft, last.endPoint.getTop());
        break;
      case 1:
        endCorner = new CornerPoint(last.endPoint.getLeft(), shapeTop);
        break;
      case 2:
        endCorner = new CornerPoint(shapeRight, last.endPoint.getTop());
        break;
      case 3:
        endCorner = new CornerPoint(last.endPoint.getLeft(), shapeBottom);
        break;
    }

    // Define middle connection points
    i = shapeEnterDirection;
    proceed = true;
    CornerPoint tempCorner = null;
    List<CornerPoint> middleCorners = new ArrayList<CornerPoint>();

    while (i != shapeLeaveDirection) {
      if (direction == 0) {
        i++;
        if (i == 4) {
          i = 0;
        }
      } else {
        i--;
        if (i == -1) {
          i = 3;
        }
      }

      switch (i) {
        case 0:
          if (direction == 0) {
            tempCorner = new CornerPoint(shapeLeft, shapeBottom);
          } else {
            tempCorner = new CornerPoint(shapeLeft, shapeTop);
          }
          break;

        case 1:
          if (direction == 0) {
            tempCorner = new CornerPoint(shapeLeft, shapeTop);
          } else {
            tempCorner = new CornerPoint(shapeRight, shapeTop);
          }
          break;

        case 2:
          if (direction == 0) {
            tempCorner = new CornerPoint(shapeRight, shapeTop);
          } else {
            tempCorner = new CornerPoint(shapeRight, shapeBottom);
          }
          break;

        case 3:
          if (direction == 0) {
            tempCorner = new CornerPoint(shapeRight, shapeBottom);
          } else {
            tempCorner = new CornerPoint(shapeLeft, shapeBottom);
          }
          break;
      }
      middleCorners.add(tempCorner);
    }

    // add startEndPoint and endEndPoint to corners, for easier defining
    // insert position
    CornerPoint firstSectionStart = new CornerPoint(first.startPoint.getLeft(), first.startPoint.getTop());
    CornerPoint lastSectionEnd = new CornerPoint(last.endPoint.getLeft(), last.endPoint.getTop());
    boolean addToRemove = false;

    // Index in cornerpoints list after which new corner points will be inserted
    int insertIndex = -1;

    List<CornerPoint> toRemove = new ArrayList<CornerPoint>();

    CornerPoint startEndPointCorner =
        new CornerPoint(this.startEndPoint.getLeft(), this.startEndPoint.getTop());
    CornerPoint endEndPointCorner =
        new CornerPoint(this.endEndPoint.getLeft(), this.endEndPoint.getTop());

    corners.add(0, startEndPointCorner);
    corners.add(endEndPointCorner);

    // define corner points to remove (corners lying inside evaded shape)
    // and index of first removed corner (index to add new corners)
    for (int j = 0; j < corners.size(); j++) {
      if (corners.get(j).compareTo(lastSectionEnd) == 0) {
        addToRemove = false;
      }

      if (addToRemove) {
        toRemove.add(corners.get(j));
      }

      if (corners.get(j).compareTo(firstSectionStart) == 0) {
        addToRemove = true;
        insertIndex = j;
      }
    }

    for (CornerPoint removed : toRemove) {
      corners.remove(removed);
    }

    if (insertIndex == corners.size() - 1) {
      corners.add(startCorner);

      for (CornerPoint added : middleCorners) {
        corners.add(added);
      }
      corners.add(endCorner);
    } else {
      corners.add(insertIndex + 1, endCorner);

      for (int j = middleCorners.size() - 1; j >= 0; j--) {
        corners.add(insertIndex + 1, middleCorners.get(j));
      }
      corners.add(insertIndex + 1, startCorner);
    }

    // remove startEndPoint and endEndPoint from corner points list
    corners.remove(startEndPointCorner);
    corners.remove(endEndPointCorner);
  }

  public void rememberSectionsPositions() {
    savedSectionsData = new ArrayList<SectionData>();

    for (Section section : sections) {
      savedSectionsData.add(new SectionData(section.startPoint.getLeft(), section.startPoint.getTop(), 
          section.endPoint.getLeft(), section.endPoint.getTop(), section.isVertical()));
    }
  }

  /**
   * Move {@link Connector} by defined x and y offset from saved {@link Section}'s positions.
   * 
   * @param xOffset
   * @param yOffset
   */
  public void moveOffsetFromStartPos(double xOffset, double yOffset) {
    xOffset = xOffset - diagram.boundaryPanel.getAbsoluteLeft();
    yOffset = yOffset - diagram.boundaryPanel.getAbsoluteTop();

    for (int i = 0; i < sections.size(); i++) {

      Point sectionTopLeft = null;
      if (savedSectionsData.get(i).startPoint.getLeft() <= savedSectionsData.get(i).endPoint.getLeft()
          && savedSectionsData.get(i).startPoint.getTop() <= savedSectionsData.get(i).endPoint.getTop()) {
        sectionTopLeft = savedSectionsData.get(i).startPoint;
      } else {
        sectionTopLeft = savedSectionsData.get(i).endPoint;
      }

      diagram.boundaryPanel.setWidgetPosition(sections.get(i), (int) (sectionTopLeft.getLeft() + xOffset), 
          (int) (sectionTopLeft.getTop() + yOffset));
      sections.get(i).startPoint.setPosition(savedSectionsData.get(i).startPoint.getLeft() + xOffset, 
          savedSectionsData.get(i).startPoint.getTop() + yOffset);
      sections.get(i).endPoint.setPosition(savedSectionsData.get(i).endPoint.getLeft() + xOffset, 
          savedSectionsData.get(i).endPoint.getTop() + yOffset);

      if (sections.get(i).startPointDecoration != null) {
        this.startPointDecoration.update(sections.get(i).calculateStartPointDecorationDirection(),
            sections.get(i).startPoint.getLeft(), sections.get(i).startPoint.getTop());
      }
      if (sections.get(i).endPointDecoration != null) {
        this.endPointDecoration.update(sections.get(i).calculateEndPointDecorationDirection(), sections.get(i).endPoint
            .getLeft(), sections.get(i).endPoint.getTop());
      }
    }
  }

  public boolean isOnThisConnector(Point point) {
    for (Section section : sections) {
      if (((point.getLeft() >= section.startPoint.getLeft() && point.getLeft() <= section.endPoint.getLeft()) || (point
          .getLeft() <= section.startPoint.getLeft() && point.getLeft() >= section.endPoint.getLeft()))
          && ((point.getTop() >= section.startPoint.getTop() && point.getTop() <= section.endPoint.getTop()) || (point
              .getTop() <= section.startPoint.getTop() && point.getTop() >= section.endPoint.getTop()))) {
        return true;
      }
    }

    return false;
  }

  public void addConnectorListener(ConnectorListener listener) {
    listeners.add(listener);
  }

  public void removeConnectorListener(ConnectorListener listener) {
    listeners.remove(listener);
  }

  public List<ConnectorListener> getListeners() {
    return listeners;
  }

  public void onConnectorClick(ConnectorClickEvent event) {
    for (ConnectorListener listener : listeners) {
      listener.onConnectorClick(event);
    }
  }

  public void onConnectorDoubleClick(ConnectorDoubleClickEvent event) {
    for (ConnectorListener listener : listeners) {
      listener.onConnectorDoubleClick(event);
    }
  }

}
