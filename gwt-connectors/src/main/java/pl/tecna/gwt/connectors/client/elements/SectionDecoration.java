package pl.tecna.gwt.connectors.client.elements;

import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.images.ConnectorsBundle;
import pl.tecna.gwt.connectors.client.util.WidgetUtils;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;

public class SectionDecoration extends FocusPanel {
  
  private final Logger LOG = Logger.getLogger("SectionDecoration");

  public enum DecorationType {
    ARROW_SOLID, ARROW_LINE, USER
  }

  public enum DecorationDirection {

    VERTICAL_UP(0), HORIZONTAL_RIGHT(1), VERTICAL_DOWN(2), HORIZONTAL_LEFT(3);

    private int index;

    private DecorationDirection(int index) {
      this.index = index;
    }

    public int getIndex() {
      return index;
    }

  }

  private boolean selected = false;
  private DecorationDirection direction;

  private Image[] decorationDirectedImages;
  private Image[] decorationDirectedSelectedImages;

  public Connector connector;

  /**
   * If true, then decoration image center is on end point
   */
  public boolean center = false;

  private SectionDecoration() {
    super();
    decorationDirectedImages = new Image[4];
    decorationDirectedSelectedImages = new Image[4];
  }

  public SectionDecoration(Image img, Image selImg) {
    this();
    for (int i = 0; i < 4; i++) {
      decorationDirectedImages[i] = img;
      decorationDirectedSelectedImages[i] = selImg;
    }
    initStyles();
  }

  public SectionDecoration(Image[] decorations, Image[] decorationsSelected) {
    this();
    decorationDirectedImages = decorations;
    decorationDirectedSelectedImages = decorationsSelected;
    initStyles();
  }

  public SectionDecoration(DecorationType type) {

    this();
    switch (type) {
      case ARROW_SOLID: {
        decorationDirectedImages[DecorationDirection.VERTICAL_UP.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_up()).createImage();
        decorationDirectedImages[DecorationDirection.HORIZONTAL_RIGHT.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_right()).createImage();
        decorationDirectedImages[DecorationDirection.VERTICAL_DOWN.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_down()).createImage();
        decorationDirectedImages[DecorationDirection.HORIZONTAL_LEFT.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_left()).createImage();

        decorationDirectedSelectedImages[DecorationDirection.VERTICAL_UP.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_up_selected()).createImage();
        decorationDirectedSelectedImages[DecorationDirection.HORIZONTAL_RIGHT.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_right_selected()).createImage();
        decorationDirectedSelectedImages[DecorationDirection.VERTICAL_DOWN.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_down_selected()).createImage();
        decorationDirectedSelectedImages[DecorationDirection.HORIZONTAL_LEFT.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_left_selected()).createImage();
      }
        break;
      case ARROW_LINE: {
        decorationDirectedImages[DecorationDirection.VERTICAL_UP.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.line_arrow_up()).createImage();
        decorationDirectedImages[DecorationDirection.HORIZONTAL_RIGHT.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.line_arrow_right()).createImage();
        decorationDirectedImages[DecorationDirection.VERTICAL_DOWN.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.line_arrow_down()).createImage();
        decorationDirectedImages[DecorationDirection.HORIZONTAL_LEFT.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.line_arrow_left()).createImage();

        decorationDirectedSelectedImages[DecorationDirection.VERTICAL_UP.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.line_arrow_up_selected()).createImage();
        decorationDirectedSelectedImages[DecorationDirection.HORIZONTAL_RIGHT.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.line_arrow_right_selected()).createImage();
        decorationDirectedSelectedImages[DecorationDirection.VERTICAL_DOWN.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.line_arrow_down_selected()).createImage();
        decorationDirectedSelectedImages[DecorationDirection.HORIZONTAL_LEFT.getIndex()] =
            AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.line_arrow_left_selected()).createImage();
      }
        break;
      default:
        break;
    }
    initStyles();

  }

  private void initStyles() {
    for (Image img : decorationDirectedImages) {
      img.addStyleName("gwt-connectors-image");
    }

    for (Image img : decorationDirectedSelectedImages) {
      img.addStyleName("gwt-connectors-image");
    }
  }

  public void update(DecorationDirection direction, int left, int top) {
    // Remember direction
    this.direction = direction;

    // Set needed decoration
    setDecoration(isSelected(), direction);

    // Set element position
    switch (direction) {
      case VERTICAL_UP:
        left = left - (this.getWidget().getOffsetWidth() / 2) + 1;
        if (center) {
          top -= this.getWidget().getOffsetHeight() / 2;
        }
        break;
      case VERTICAL_DOWN:
        left = left - (this.getWidget().getOffsetWidth() / 2) + 1;
        top = top - this.getWidget().getOffsetHeight() + 2;
        if (center) {
          top += this.getWidget().getOffsetHeight() / 2;
        }
        break;
      case HORIZONTAL_LEFT:
        top = top - (this.getWidget().getOffsetHeight() / 2) + 1;
        // left = left;
        if (center) {
          left -= this.getWidget().getOffsetWidth() / 2;
        }
        break;
      case HORIZONTAL_RIGHT:
        top = top - (this.getWidget().getOffsetHeight() / 2) + 1;
        left = left - this.getWidget().getOffsetWidth();
        if (center) {
          left += this.getWidget().getOffsetWidth() / 2;
        }
        break;
    }
    WidgetUtils.setWidgetPosition(((AbsolutePanel) this.getParent()), this, left, top);
  }

  private void setDecoration(boolean sel, DecorationDirection direction) {
    // Set decoration depending on direction and selection
    if (sel) {
      this.setWidget(decorationDirectedSelectedImages[direction.getIndex()]);
    } else {
      this.setWidget(decorationDirectedImages[direction.getIndex()]);
    }
  }

  public void showOnDiagram(AbsolutePanel panel, DecorationDirection direction, int left, int top) {
    // Add decoration to given panel
    if (this.isAttached()) {
      if (this.getParent().equals(panel)) {
        WidgetUtils.setWidgetPosition(panel, this, left, top);
      } else {
        this.removeFromParent();
        WidgetUtils.addWidget(panel, this, left, top);
      }
    } else {
      WidgetUtils.addWidget(panel, this, left, top);
    }
    // Update decoration's position and picture
    update(direction, left, top);
  }

  public void select() {
    // select section decoration
    setSelected(true);
    setDecoration(isSelected(), this.direction);
  }

  public void deselect() {
    // deselect section decoration
    setSelected(false);
    setDecoration(isSelected(), this.direction);
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

}
