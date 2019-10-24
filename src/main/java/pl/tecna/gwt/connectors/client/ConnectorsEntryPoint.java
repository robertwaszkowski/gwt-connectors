package pl.tecna.gwt.connectors.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.StyleInjector;

import pl.tecna.gwt.connectors.client.resources.ConnectorsBundle;

public class ConnectorsEntryPoint implements EntryPoint {

  @Override
  public void onModuleLoad() {
    StyleInjector.injectAtStart(ConnectorsBundle.INSTANCE.css().getText());
  }
}
