package pl.tecna.gwt.connectors.client.elements;

import pl.tecna.gwt.connectors.client.Diagram;

import com.google.gwt.user.client.ui.FocusPanel;

public abstract class WidgetDiagramElement extends FocusPanel implements DiagramElement {

  protected Diagram diagram;
  
  @Override
  public Diagram getDiagram() {
    return diagram;
  }

  @Override
  public void setDiagram(Diagram diagram) {
    this.diagram = diagram;
  }

}
