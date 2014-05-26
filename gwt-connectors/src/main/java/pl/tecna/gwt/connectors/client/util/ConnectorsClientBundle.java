package pl.tecna.gwt.connectors.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface ConnectorsClientBundle extends ClientBundle {

  interface ConnectorsCssResource extends CssResource {

    @ClassName("gwt-connectors-line")
    String line();

    @ClassName("gwt-connectors-line-vertical")
    String lineVertical();

    @ClassName("gwt-connectors-line-horizontal")
    String lineHorizontal();

    @ClassName("gwt-connectors-line-selected")
    String lineSelected();

    @ClassName("dragdrop-positioner")
    String positioner();

    @ClassName("dragdrop-selected")
    String dragdropSelected();

    @ClassName("dragdrop-dragging")
    String dragdropDragging();

    @ClassName("dragdrop-proxy")
    String dragdropProxy();

    @ClassName("gwt-connectors-widget-padding-selected")
    String widgetPaddingSelected();

    @ClassName("gwt-connectors-widget-padding-unselected")
    String widgetPaddingUnselected();

    @ClassName("gwt-connectors-shape-selected")
    String shapeSelected();

    @ClassName("gwt-connectors-shape-unselected")
    String shapeUnselected();

    @ClassName("gwt-connectors-selection-panel")
    String selectionPanel();

    @ClassName("x-unselectable")
    String xUnselectable();
    
    @ClassName("gwt-connectors-shape-connector-transparent")
    String shapeConnectorTransparent();

    @ClassName("gwt-connectors-shape-connector")
    String shapeConnector();

    @ClassName("gwt-connectors-shape-connector-inner")
    String shapeConnectorInner();
    
    @ClassName("gwt-connectors-shape-connector-inner-drop-over")
    String shapeConnectorInnerDropOver();
    
    @ClassName("gwt-connectors-image")
    String imageDispBlock();

    @ClassName("gwt-connectors-end-point")
    String endPoint();

    @ClassName("gwt-connectors-end-point-create-connector")
    String endPointConnectorCreate();
    
    @ClassName("gwt-connectors-end-point-transparent")
    String endPointTransparent();
    
    @ClassName("gwt-connectors-vertical-section")
    String verticalSection();
    
    @ClassName("gwt-connectors-horizontal-section")
    String horizontalSection();
  }

  static final ConnectorsClientBundle INSTANCE = GWT.create(ConnectorsClientBundle.class);

  @Source("gwt-connectors.css")
  ConnectorsCssResource css();

}
