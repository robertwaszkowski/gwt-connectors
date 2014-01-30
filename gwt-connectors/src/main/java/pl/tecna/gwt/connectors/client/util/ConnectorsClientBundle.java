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

    @ClassName("gwt-connectors-shape-connector-start")
    String gwtConnectorsShapeConnectorStart();

    @ClassName("gwt-connectors-shape-connector-start-inner")
    String gwtConnectorsShapeConnectorStartInner();

    @ClassName("gwt-connectors-image")
    String imageDispBlock();

  }

  static final ConnectorsClientBundle INSTANCE = GWT.create(ConnectorsClientBundle.class);

  @Source("gwt-connectors.css")
  ConnectorsCssResource css();

}
