package pl.tecna.gwt.connectors.example.client;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.CornerPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.SectionDecoration;
import pl.tecna.gwt.connectors.client.elements.Shape;
import pl.tecna.gwt.connectors.client.elements.Shape.CPShapeType;
import pl.tecna.gwt.connectors.client.images.ConnectorsBundle;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.Scheduler;
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

		// Create boundary panel
		AbsolutePanel boundaryPanel = new AbsolutePanel();
		boundaryPanel.setSize("20000px", "20000px");
		RootPanel.get().add(boundaryPanel, 10, 10);

		final Diagram diagram = new Diagram(boundaryPanel);

		boundaryPanel.add(new Label("Connectors example for GWT 2.1"), 10, 2);
		Connector connector1 = new Connector(50, 80, 150, 200);
		connector1.showOnDiagram(diagram);

		ArrayList<CornerPoint> cp = new ArrayList<CornerPoint>();
		cp.add(new CornerPoint(370, 200));
		cp.add(new CornerPoint(370, 120));
		cp.add(new CornerPoint(270, 120));
		SectionDecoration startDecoration = new SectionDecoration(SectionDecoration.DECORATE_ARROW);
		SectionDecoration endDecoration = new SectionDecoration(SectionDecoration.DECORATE_ARROW);
		Connector connector2 = new Connector(350, 200, 270, 80, cp, startDecoration, endDecoration);
		connector2.showOnDiagram(diagram);

		Connector connector3 = new Connector(450, 120, 500, 80);
		connector3.showOnDiagram(diagram);

		FocusPanel diamond = new FocusPanel();
		diamond.setWidget(AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.diamondImg()).createImage());
		boundaryPanel.add(diamond, 700, 400);

		// Add some elements that can be connected
		final Label label = new Label("LABEL");
		final Label label2 = new Label("LABEL_2");
		final Image image = new Image("http://code.google.com/images/code_sm.png");

		image.setPixelSize(153, 55);

		boundaryPanel.add(label, 50, 250);
		boundaryPanel.add(label2, 450, 200);

		Shape shapeForLabel = new Shape(label, CPShapeType.DIAMOND);
		shapeForLabel.showOnDiagram(diagram);
		shapeForLabel.setTitle("shapeForLabel");
		shapeForLabel.enableConnectionCreate(true);

		Shape shapeForLabel2 = new Shape(label2, CPShapeType.OVAL);
		shapeForLabel2.showOnDiagram(diagram);
		shapeForLabel2.setTitle("shapeForLabel2");

		Shape shapeForDiamond = new Shape(diamond, CPShapeType.DIAMOND);
		shapeForDiamond.setEnableOverlap(true);
		shapeForDiamond.showOnDiagram(diagram);
		shapeForDiamond.makeConnectable(false);
		shapeForDiamond.setTitle("shapeForDiamond");
		
	}
	
}
