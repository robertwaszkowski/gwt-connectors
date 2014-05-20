package pl.tecna.gwt.connectors.client.elements;

import pl.tecna.gwt.connectors.client.Diagram;

public abstract class BaseDiagramElement implements DiagramElement {

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
