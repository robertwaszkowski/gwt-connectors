package pl.tecna.gwt.connectors.client.util;

import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;

public interface CustomGwtDndBundle extends DragClientBundle {

  interface CustomGwtDndCss extends DragCssResource {
    
  }
  
  public CustomGwtDndCss css();
  
}
