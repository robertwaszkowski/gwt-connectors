package pl.tecna.gwt.connectors.client.resources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {

  public static final Resources INSTANCE =  GWT.create(Resources.class);
  
  @Source("gwt-connectors.css")
  TextResource css();
}
