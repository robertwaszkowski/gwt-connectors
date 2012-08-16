package pl.tecna.gwt.connectors.example.client;

import java.util.ArrayList;

import pl.tecna.gwt.connectors.client.ConnectionPoint;
import pl.tecna.gwt.connectors.client.Connector;
import pl.tecna.gwt.connectors.client.CornerPoint;
import pl.tecna.gwt.connectors.client.Diagram;
import pl.tecna.gwt.connectors.client.SectionDecoration;
import pl.tecna.gwt.connectors.client.Shape;
import pl.tecna.gwt.connectors.client.Shape.CPShapeType;
import pl.tecna.gwt.connectors.client.drop.DiagramWidgetDropController;
import pl.tecna.gwt.connectors.client.images.ConnectorsBundle;
import pl.tecna.gwt.connectors.client.util.Log;
import pl.tecna.gwt.connectors.client.util.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;


public class Example implements EntryPoint {
	
	private final Logger LOG = new Logger("Example");

	private Shape shapeForImage;
//	private DivLogger logger;

	public void onModuleLoad() {
				
		LOG.i("Load module ................");
		
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			public void onUncaughtException(Throwable e) {

				Log.severe("Uncaught error", e);
			}
		});
		
//		CSS.addStyleSheet("gxt-base", GWT.getModuleBaseURL() + "../resources/gxt/css/gxt-all.css");
//		CSS.addStyleSheet("gxt-gray", GWT.getModuleBaseURL() + "../resources/gxt/css/gxt-gray.css");
//		CSS.addStyleSheet("gwt-connectors", GWT.getModuleBaseURL() + "../resources/css/gwt-connectors.css");
//		CSS.addStyleSheet("gwt-dnd", GWT.getModuleBaseURL() + "../resources/css/gwt-dnd.css");
//		CSS.addStyleSheet("aur-mdl-styles", GWT.getModuleBaseURL() + "../resources/css/style.css");
//		
		
		// Create boundary panel
		AbsolutePanel boundaryPanel = new AbsolutePanel();
		boundaryPanel.setSize("1224px", "1024px");
		RootPanel.get().add(boundaryPanel, 10, 10);
		// Add a border so we can see what's going on
//		DOM.setStyleAttribute(boundaryPanel.getElement(),
//		      "border", "1px solid black");

		final Diagram diagram = new Diagram(boundaryPanel);
		
		boundaryPanel.add(new Label("Connectors example for GWT 2.1"), 10, 2);

		// Add connectors
//		boundaryPanel.add(new Label("(50, 80, 100, 100)"), 50, 50);
		Connector connector1 = new Connector(50, 80, 150, 200);
		connector1.showOnDiagram(diagram);
		
//		boundaryPanel.add(new Label("(350, 200, 300, 80)"), 250, 50);
		
		
		
		ArrayList<CornerPoint> cp = new ArrayList<CornerPoint>();
		cp.add(new CornerPoint(370, 200));
		cp.add(new CornerPoint(370, 120));
		cp.add(new CornerPoint(270, 120));
		SectionDecoration startDecoration = new SectionDecoration(SectionDecoration.DECORATE_ARROW);
		SectionDecoration endDecoration = new SectionDecoration(SectionDecoration.DECORATE_ARROW);
		Connector connector2 = new Connector(350, 200, 270, 80, cp, startDecoration, endDecoration);
		connector2.showOnDiagram(diagram);
		
//		boundaryPanel.add(new Label("(450, 120, 500, 80)"), 450, 50);
		Connector connector3 = new Connector(450, 120, 500, 80);
		connector3.showOnDiagram(diagram);
		
		FocusPanel diamond = new FocusPanel();
//		HTML diamondHtml = new HTML();
//		diamondHtml.setHTML("Diamond");
//		diamond.setWidget(diamondHtml);
		diamond.setWidget(AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.diamondImg()).createImage());
		boundaryPanel.add(diamond, 700, 400);
		//diagram.shapeDragController.makeDraggable(diamond);

		// Add some elements that can be connected
		final Label label = new Label("LABEL");
		final Label label2 = new Label("LABEL_2");
		final Image image = new Image("http://code.google.com/images/code_sm.png");
//		final Image image = new Image("resources/gatewaydbxor_diagram.png");
		image.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
//				for (ConnectionPoint cp : shapeForImage.connectionPoints) {
//					int[] tab = shapeForImage.getCPCenterRelativePos(cp);
//				}
			}
		});
		image.setPixelSize(153, 55);
		final HTML html = new HTML("<b>HTML<br>ELEMENT</b>");
		
		final Label label3 = new Label("Label without Shape");
		final Image image2 = new Image("http://code.google.com/images/code_sm.png");

		boundaryPanel.add(label, 50, 250);
		boundaryPanel.add(label2, 450, 200);
   // diagram.shapeDragController.makeDraggable(label);
   // diagram.shapeDragController.makeDraggable(label2);
    
    Shape shapeForLabel = new Shape(label, CPShapeType.DIAMOND);
    shapeForLabel.showOnDiagram(diagram);
    shapeForLabel.setTitle("shapeForLabel");
    
    Shape shapeForLabel2 = new Shape(label2, CPShapeType.OVAL);
    shapeForLabel2.showOnDiagram(diagram);
    shapeForLabel2.setTitle("shapeForLabel2");
    
    Shape shapeForDiamond = new Shape(diamond, CPShapeType.DIAMOND);
    shapeForDiamond.setEnableOverlap(true);
    shapeForDiamond.showOnDiagram(diagram);
    shapeForDiamond.disableConnectors(true);
    shapeForDiamond.setTitle("shapeForDiamond");
    shapeForDiamond.disableConnectors(false);
    /* 
		boundaryPanel.add(image, 200, 300);
		boundaryPanel.add(html, 450, 300);
		boundaryPanel.add(label3, 200, 400);
		boundaryPanel.add(image2, 300, 400);
		diagram.shapeDragController.makeDraggable(image);
		diagram.shapeDragController.makeDraggable(html);
		diagram.shapeDragController.makeDraggable(label3);
		diagram.shapeDragController.makeDraggable(image2);

//		image.addLoadListener(new LoadListener() {
//
//			public void onError(Widget sender) {
//				Window.alert("Image can not be loaded!");
//			}
//
//			public void onLoad(Widget sender) {
//System.out.println("onLoad");
//				Shape shapeForImage = new Shape(image);
//				shapeForImage.showOnDiagram(diagram);
//			}
//			
//		});
		
		shapeForImage = new Shape(image);
		shapeForImage.showOnDiagram(diagram);
		shapeForImage.setTitle("shapeForImage-1");
		
		Shape shapeForHtml = new Shape(html);
		shapeForHtml.showOnDiagram(diagram);
		shapeForHtml.setTitle("shapeForHtml");

		
		// Connect label and image
		
        ConnectionPoint labelConnectionPoint = shapeForLabel.getCPForPosition(4);
    	ConnectionPoint imageConnectionPoint = shapeForImage.getCPForPosition(2);

    	Connector label2image = new Connector
    					(labelConnectionPoint.getAbsoluteLeft(),
    	                 labelConnectionPoint.getAbsoluteTop(),
    	                 imageConnectionPoint.getAbsoluteLeft(),
    	                 imageConnectionPoint.getAbsoluteTop(),
    	                 null,
    	                 new SectionDecoration(SectionDecoration.DECORATE_ARROW));

    	label2image.startEndPoint.glueToConnectionPoint(labelConnectionPoint);
    	label2image.endEndPoint.glueToConnectionPoint(imageConnectionPoint);

    	label2image.showOnDiagram(diagram);
    	
    	
    	// Connect label2 and html
        ConnectionPoint label2ConnectionPoint = shapeForLabel2.getCPForPosition(6);
    	ConnectionPoint htmlConnectionPoint = shapeForHtml.getCPForPosition(2);

    	Connector label22html = new Connector
    					(label2ConnectionPoint.getAbsoluteLeft(),
    	                 label2ConnectionPoint.getAbsoluteTop(),
    	                 htmlConnectionPoint.getAbsoluteLeft(),
    	                 htmlConnectionPoint.getAbsoluteTop(),
    	                 null,
    	                 new SectionDecoration(SectionDecoration.DECORATE_ARROW));

    	label22html.startEndPoint.glueToConnectionPoint(label2ConnectionPoint);
    	label22html.endEndPoint.glueToConnectionPoint(htmlConnectionPoint);

    	label22html.showOnDiagram(diagram);
    	
    	Connector connector = new Connector(1000, 120, 1100, 80);
		connector.showOnDiagram(diagram);
		connector = new Connector(1200, 120, 1300, 80);
		connector.showOnDiagram(diagram);
		connector = new Connector(700, 600, 800, 700);
		connector.showOnDiagram(diagram);
		connector = new Connector(820, 650, 920, 780);
		connector.showOnDiagram(diagram);
		connector = new Connector(550, 550, 650, 650);
		connector.showOnDiagram(diagram);
		connector = new Connector(100, 900, 300, 1000);
		connector.showOnDiagram(diagram);
		
		diagram.shapeDragController.makeDraggable(label3);
		diagram.shapeDragController.makeDraggable(image2);
    	
//    	logger = Log.getLogger(DivLogger.class);
//		RootPanel.get().add(logger.getWidget());
//		Log.setCurrentLogLevel(Log.LOG_LEVEL_DEBUG);
    	
//    	image2label.update();
 * 
 */
	}
}
