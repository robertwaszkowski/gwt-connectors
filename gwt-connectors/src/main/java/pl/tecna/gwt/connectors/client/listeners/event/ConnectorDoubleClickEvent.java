package pl.tecna.gwt.connectors.client.listeners.event;

import pl.tecna.gwt.connectors.client.elements.Connector;
import pl.tecna.gwt.connectors.client.elements.Section;

public class ConnectorDoubleClickEvent implements ConnectorEvent {

  private Connector sourceConnector;
  private Section sourceSection;
  
  public ConnectorDoubleClickEvent(Connector sourceConnector, Section sourceSection) {
    this.sourceConnector = sourceConnector;
    this.sourceSection = sourceSection;
  }
  
  public Connector getSourceConnector() {
    return sourceConnector;
  }
  
  public Section getSourceSection() {
    return sourceSection;
  }
  
}
