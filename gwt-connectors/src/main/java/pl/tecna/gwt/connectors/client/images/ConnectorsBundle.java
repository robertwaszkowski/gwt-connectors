package pl.tecna.gwt.connectors.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ConnectorsBundle extends ClientBundle {

  public static final ConnectorsBundle INSTANCE = GWT.create(ConnectorsBundle.class);
  
  // For VERTICAL_DOWN direction
  @Source("arrow_down.png")
  ImageResource arrow_down();

  // For HORIZONTAL_LEFT direction
  @Source("arrow_left.png")
  ImageResource arrow_left();

  // For HORIZONTAL_RIGHT direction
  @Source("arrow_right.png")
  ImageResource arrow_right();

  // For VERTICAL_UP direction
  @Source("arrow_up.png")
  ImageResource arrow_up();

  // For selected VERTICAL_DOWN direction
  @Source("arrow_down_selected.png")
  ImageResource arrow_down_selected();

  // For selected HORIZONTAL_LEFT direction
  @Source("arrow_left_selected.png")
  ImageResource arrow_left_selected();

  // For selected HORIZONTAL_RIGHT direction
  @Source("arrow_right_selected.png")
  ImageResource arrow_right_selected();

  // For selected VERTICAL_UP direction
  @Source("arrow_up_selected.png")
  ImageResource arrow_up_selected();

  // For VERTICAL_DOWN direction DASHED style
  @Source("line_arrow_down.png")
  ImageResource line_arrow_down();

  // For HORIZONTAL_LEFT direction DASHED style
  @Source("line_arrow_left.png")
  ImageResource line_arrow_left();

  // For HORIZONTAL_RIGHT direction DASHED style
  @Source("line_arrow_right.png")
  ImageResource line_arrow_right();

  // For VERTICAL_UP direction DASHED style
  @Source("line_arrow_up.png")
  ImageResource line_arrow_up();

  // For VERTICAL_DOWN direction DASHED style
  @Source("line_arrow_down_selected.png")
  ImageResource line_arrow_down_selected();

  // For selected HORIZONTAL_LEFT direction DASHED style
  @Source("line_arrow_left_selected.png")
  ImageResource line_arrow_left_selected();

  // For selected HORIZONTAL_RIGHT direction DASHED style
  @Source("line_arrow_right_selected.png")
  ImageResource line_arrow_right_selected();

  // For selected VERTICAL_UP direction DASHED style
  @Source("line_arrow_up_selected.png")
  ImageResource line_arrow_up_selected();

  // For test cases
  @Source("diamond.png")
  ImageResource diamondImg();
  
  @Source("oval.png")
  ImageResource ovalImg();

}
