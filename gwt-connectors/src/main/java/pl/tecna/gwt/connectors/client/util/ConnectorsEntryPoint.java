package pl.tecna.gwt.connectors.client.util;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.StyleInjector;

public class ConnectorsEntryPoint implements EntryPoint{

  public void onModuleLoad() {
    StyleInjector.injectAtStart(ConnectorsClientBundle.INSTANCE.css().getText());
  }
}
