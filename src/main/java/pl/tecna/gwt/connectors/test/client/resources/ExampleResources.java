package pl.tecna.gwt.connectors.test.client.resources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface ExampleResources extends ClientBundle {

  public static final ExampleResources INSTANCE =  GWT.create(ExampleResources.class);
  
  @Source("gwt-connectors-test.css")
  TextResource testCss();
}
