package pl.tecna.gwt.connectors.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;

public interface ConnectorsClientBundle extends ClientBundle {
  
  interface ConnectorsCssResource extends CssResource {
    
    @ClassName("gwt-connectors-line")
    public String line();
    
    @ClassName("gwt-connectors-line-vertical")
    public String lineVertical();
    
    @ClassName("gwt-connectors-line-horizontal")
    public String lineHorizontal();
    
    @ClassName("gwt-connectors-line-selected")
    public String lineSelected();
    
    @ClassName("dragdrop-positioner")
    public String positioner();    
    
    @ClassName("gwt-connectors-widget-padding-selected")
    public String widgetPaddingSelected();
    
    @ClassName("gwt-connectors-widget-padding-unselected")
    public String widgetPaddingUnselected(); 
    
    @ClassName("gwt-connectors-shape-selected")
    public String shapeSelected();  
    
    @ClassName("gwt-connectors-shape-unselected")
    public String shapeUnselected();  

    @ClassName("gwt-connectors-selection-panel")
    public String selectionPanel();  

    @ClassName("dragdrop-selected")
    public String dragdropSelected(); 

    @ClassName("dragdrop-dragging")
    public String dragdropDragging(); 

    @ClassName("dragdrop-proxy")
    public String dragdropProxy (); 

    @ClassName("x-unselectable")
    public String xUnselectable(); 

  }
  
  static final ConnectorsClientBundle INSTANCE = GWT.create(ConnectorsClientBundle.class);
  
  @NotStrict
  @Source("gwt-connectors.css")
  ConnectorsCssResource css();
  
}
