package pl.tecna.gwt.connectors.example.client;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;


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
		AbsolutePanel boundaryPanel = new AbsolutePanel();
		boundaryPanel.setSize("20000px", "20000px");
		viewPanel.add(boundaryPanel, -10000, -10000);
		
		RootPanel.get().add(viewPanel, 0, 0);
		
		final Diagram diagram = new Diagram(boundaryPanel);
		diagram.drawInitializingConnectorsInLine = true;

		boundaryPanel.add(new Label("Connectors example for GWT 2.4"), 10010, 10002);
		Connector connector1 = new Connector(10050, 10080, 10150, 10200, new SectionDecoration(DecorationType.ARROW_SOLID), new SectionDecoration(DecorationType.ARROW_SOLID));
		connector1.showOnDiagram(diagram);

		ArrayList<CornerPoint> cp = new ArrayList<CornerPoint>();
		cp.add(new CornerPoint(10370, 10200));
		cp.add(new CornerPoint(10370, 10120));
		cp.add(new CornerPoint(10270, 10120));
		SectionDecoration startDecoration = new SectionDecoration(DecorationType.ARROW_LINE);
		SectionDecoration endDecoration = new SectionDecoration(
				new Image("http://code.google.com/images/code_sm.png"), 
				new Image("http://code.google.com/images/code_sm.png"));
		Connector connector2 = new Connector(10350, 10200, 10270, 10080, cp, startDecoration, endDecoration);
		connector2.style = ConnectorStyle.DASHED;
		connector2.showOnDiagram(diagram);

		Connector connector3 = new Connector(10450, 10120, 10500, 10080, new SectionDecoration(DecorationType.ARROW_SOLID), new SectionDecoration(DecorationType.ARROW_SOLID));
		connector3.style = ConnectorStyle.DOTTED;
		connector3.showOnDiagram(diagram);

		FocusPanel diamond = new FocusPanel();
		Image img = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.diamondImg()).createImage();
		img.getElement().getStyle().setDisplay(Display.BLOCK);
		diamond.setWidget(img);
		boundaryPanel.add(diamond, 10700, 10400);

		// Add some elements that can be connected
		final Label label = new Label("LABEL Label Label");
        final Label labelChanged = new Label("Changed changedchangedchanged ...");
		
		final Label label2 = new Label("LABEL_2");
		final Image image = new Image("http://code.google.com/images/code_sm.png");

		final Label label3 = new Label("LABEL_3 Test Longer Label with rectangle connection points");

		image.setPixelSize(10153, 10055);

		BPMNTask task = new BPMNTask();
		boundaryPanel.add(task, 10500, 10300);
		
		boundaryPanel.add(label, 10050, 10250);
		boundaryPanel.add(label2, 10450, 10200);
		boundaryPanel.add(label3, 10700, 10500);

		final Shape shapeForLabel = new Shape(label, CPShapeType.DIAMOND);
		shapeForLabel.showOnDiagram(diagram);
		shapeForLabel.setTitle("shapeForLabel");
		shapeForLabel.enableConnectionCreate(true);
		
		label.addDoubleClickHandler(new DoubleClickHandler() {

		  @Override
		  public void onDoubleClick(DoubleClickEvent event) {
		    shapeForLabel.changeConnectedWidget(labelChanged, CPShapeType.RECTANGLE);
		  }
		});

		labelChanged.addDoubleClickHandler(new DoubleClickHandler() {

		  @Override
		  public void onDoubleClick(DoubleClickEvent event) {
		    shapeForLabel.changeConnectedWidget(label, CPShapeType.DIAMOND);
		  }
		});
		
		Shape shapeForLabel2 = new Shape(label2, CPShapeType.OVAL);
		shapeForLabel2.showOnDiagram(diagram);
		shapeForLabel2.setTitle("shapeForLabel2");
		
		Shape shapeForLabel3 = new Shape(label3, CPShapeType.RECTANGLE);
		shapeForLabel3.showOnDiagram(diagram);
		shapeForLabel3.setTitle("shapeForLabel");
		shapeForLabel3.enableConnectionCreate(true);

		Shape shapeForDiamond = new Shape(diamond, CPShapeType.DIAMOND);
		shapeForDiamond.showOnDiagram(diagram);
		shapeForDiamond.enableConnectionCreate(true);
		shapeForDiamond.setTitle("shapeForDiamond");
		
		Shape shapeForTask = new Shape(task, CPShapeType.RECTANGLE);
		shapeForTask.connectorsStyle = ConnectorStyle.DASHED;
		shapeForTask.showOnDiagram(diagram);
		shapeForTask.enableConnectionCreate(true);
		shapeForTask.setTitle("Shape for task");
		
		connector3.endEndPoint.linkShape(shapeForLabel2);
	}
	
}
