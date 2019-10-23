package pl.tecna.gwt.connectors.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.StyleInjector;

import pl.tecna.gwt.connectors.client.resources.Resources;

public class ConnectorsEntryPoint implements EntryPoint {

  @Override
  public void onModuleLoad() {
    StyleInjector.injectAtStart(Resources.INSTANCE.css().getText());
  }
}
