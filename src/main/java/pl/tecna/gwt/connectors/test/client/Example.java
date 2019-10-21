package pl.tecna.gwt.connectors.test.client;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.*;
import pl.tecna.gwt.connectors.client.CornerPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.SectionDecoration;
import pl.tecna.gwt.connectors.client.elements.SectionDecoration.DecorationType;
import pl.tecna.gwt.connectors.client.elements.Shape;
import pl.tecna.gwt.connectors.client.elements.Shape.CPShapeType;
import pl.tecna.gwt.connectors.client.images.ConnectorsBundle;
import pl.tecna.gwt.connectors.client.util.ConnectorStyle;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.DOM;


public class Example implements EntryPoint {
  private Logger LOG = Logger.getLogger("Example");

  public void onModuleLoad() {

    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

      public void execute() {
        loadApplication();
      }
    });

  }

  private void loadApplication() { 	

    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

      public void onUncaughtException(Throwable e) {
        LOG.log(Level.SEVERE, "Uncaught error", e);
      }
    });

    AbsolutePanel viewPanel = new AbsolutePanel();
    DOM.setStyleAttribute(viewPanel.getElement(), "overflow", "scroll");
    viewPanel.setSize("100%", "100%");

    // Create boundary panel
    final AbsolutePanel boundaryPanel = new AbsolutePanel();
    boundaryPanel.setSize("20000px", "20000px");
    viewPanel.add(boundaryPanel, -10000, -10000);

    RootPanel.get().add(viewPanel, 0, 0);

    final Diagram diagram = new Diagram(boundaryPanel);
    diagram.drawInitializingConnectorsInLine = true;

    boundaryPanel.add(new Label("Connectors example for GWT 2.7"), 10010, 10002);
    Connector connector1 = new Connector(10050, 10080, 10150, 10200, new SectionDecoration(DecorationType.ARROW_SOLID), new SectionDecoration(DecorationType.ARROW_SOLID));
    connector1.showOnDiagram(diagram);

    ArrayList<CornerPoint> cp = new ArrayList<CornerPoint>();
    cp.add(new CornerPoint(10370, 10200));
    cp.add(new CornerPoint(10370, 10120));
    cp.add(new CornerPoint(10270, 10120));
    SectionDecoration startDecoration = new SectionDecoration(DecorationType.ARROW_LINE);
    SectionDecoration endDecoration = new SectionDecoration(DecorationType.ARROW_LINE);
    Connector connector2 = new Connector(10350, 10200, 10270, 10080, cp, startDecoration, endDecoration);
    connector2.style = ConnectorStyle.DASHED;
    connector2.showOnDiagram(diagram);

    Connector connector3 = new Connector(10450, 10120, 10500, 10080, new SectionDecoration(DecorationType.ARROW_SOLID), new SectionDecoration(DecorationType.ARROW_SOLID));
    connector3.style = ConnectorStyle.DOTTED;
    connector3.showOnDiagram(diagram);

    final FocusPanel diamond = new FocusPanel();
    Image diamondImg = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.diamondImg()).createImage();
    diamondImg.getElement().getStyle().setDisplay(Display.BLOCK);
    diamond.setWidget(diamondImg);
    boundaryPanel.add(diamond, 10400, 10300);

    final FocusPanel oval = new FocusPanel();
    Image ovalImg = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.ovalImg()).createImage();
    ovalImg.getElement().getStyle().setDisplay(Display.BLOCK);
    oval.setWidget(ovalImg);
    boundaryPanel.add(oval, 10300, 10300);

    final Label label = new Label("Test label");
    final Label label2 = new Label("LABEL_2");

    final Image image = new Image("https://upload.wikimedia.org/wikipedia/en/3/36/Google_Web_Toolkit_%28icon%29.png");
    boundaryPanel.add(image, 10300, 10500);

    final Label label3 = new Label("LABEL_3 Test Longer Label with rectangle connection points");

    image.setPixelSize(100, 100);

    final BPMNTask task = new BPMNTask();
    boundaryPanel.add(task, 10100, 10300);

    boundaryPanel.add(label, 10050, 10250);
    boundaryPanel.add(label2, 10450, 10200);
    boundaryPanel.add(label3, 10300, 10400);

    Shape shapeForLabel = new Shape(label, CPShapeType.DIAMOND);
    shapeForLabel.showOnDiagram(diagram);
    shapeForLabel.setTitle("shapeForLabel");
    shapeForLabel.enableConnectionCreate(true);

    Shape shapeForLabel2 = new Shape(label2, CPShapeType.OVAL);
    shapeForLabel2.showOnDiagram(diagram);
    shapeForLabel2.setTitle("shapeForLabel2");

    Shape shapeForLabel3 = new Shape(label3, CPShapeType.RECTANGLE);
    shapeForLabel3.showOnDiagram(diagram);
    shapeForLabel3.setTitle("shapeForLabel");
    shapeForLabel3.enableConnectionCreate(true);

    final Shape shapeForTask = new Shape(task, CPShapeType.RECTANGLE);
    shapeForTask.connectorsStyle = ConnectorStyle.SOLID;
    shapeForTask.showOnDiagram(diagram);
    shapeForTask.enableConnectionCreate(true);
    shapeForTask.setTitle("Shape for task");
    
    Shape shapeForOval = new Shape(oval, CPShapeType.OVAL);
    shapeForOval.connectorsStyle = ConnectorStyle.SOLID;
    shapeForOval.showOnDiagram(diagram);
    shapeForOval.enableConnectionCreate(true);
    
    Shape shapeForDiamond = new Shape(diamond, CPShapeType.DIAMOND);
    shapeForDiamond.connectorsStyle = ConnectorStyle.SOLID;
    shapeForDiamond.showOnDiagram(diagram);
    shapeForDiamond.enableConnectionCreate(true);

    Shape shapeForImage = new Shape(image, CPShapeType.RECTANGLE);
    shapeForImage.connectorsStyle = ConnectorStyle.SOLID;
    shapeForImage.showOnDiagram(diagram);
    shapeForImage.enableConnectionCreate(true);

    task.addDoubleClickHandler(new DoubleClickHandler() {

      @Override
      public void onDoubleClick(DoubleClickEvent event) {
        if (!diamond.isAttached()) {
          boundaryPanel.add(diamond, 10700, 10400);
        }
        shapeForTask.changeConnectedWidget(diamond, CPShapeType.DIAMOND);
        shapeForTask.enableConnectionCreate(true);
      }
    });

    diamond.addDoubleClickHandler(new DoubleClickHandler() {

      @Override
      public void onDoubleClick(DoubleClickEvent event) {
        shapeForTask.changeConnectedWidget(oval, CPShapeType.OVAL);
        shapeForTask.enableConnectionCreate(false);
      }
    });

    oval.addDoubleClickHandler(new DoubleClickHandler() {

      @Override
      public void onDoubleClick(DoubleClickEvent event) {
        shapeForTask.changeConnectedWidget(task, CPShapeType.RECTANGLE);
        shapeForTask.enableConnectionCreate(false);
      }
    });

    connector3.endEndPoint.linkShape(shapeForLabel2);
  }

}
