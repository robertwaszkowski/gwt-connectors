package pl.tecna.gwt.connectors.client.elements;

import java.util.logging.Logger;

import pl.tecna.gwt.connectors.client.images.ConnectorsBundle;
import pl.tecna.gwt.connectors.client.util.ConnectorsClientBundle;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;

public class SectionDecoration extends FocusPanel {
	
	// decorationDirections
	public final static int VERTICAL_UP       = 0;
	public final static int HORIZONTAL_RIGHT  = 1;
	public final static int VERTICAL_DOWN     = 2;
	public final static int HORIZONTAL_LEFT   = 3;
	
	public final static int DECORATE_ARROW = 101;

	private boolean selected = false;
	private int direction;
	
	private Image[] decorationDirectedImages;
	private Image[] decorationDirectedSelectedImages;
	
	public Connector connector;
	
	public SectionDecoration(int decorationType) {
		super();
				
		decorationDirectedImages = new Image[4];
		decorationDirectedSelectedImages = new Image[4];

		switch (decorationType) {
		case DECORATE_ARROW:
			decorationDirectedImages[VERTICAL_UP]      = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_up()).createImage();
			decorationDirectedImages[HORIZONTAL_RIGHT] = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_right()).createImage();
			decorationDirectedImages[VERTICAL_DOWN]    = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_down()).createImage();
			decorationDirectedImages[HORIZONTAL_LEFT]  = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_left()).createImage();

			decorationDirectedSelectedImages[VERTICAL_UP]      = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_up_selected()).createImage();
			decorationDirectedSelectedImages[HORIZONTAL_RIGHT] = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_right_selected()).createImage();
			decorationDirectedSelectedImages[VERTICAL_DOWN]    = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_down_selected()).createImage();
			decorationDirectedSelectedImages[HORIZONTAL_LEFT]  = AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.arrow_left_selected()).createImage();
			break;

		default:
			break;
		}
		
		for (Image img : decorationDirectedImages) {
	    img.addStyleName(ConnectorsClientBundle.INSTANCE.css().imageDispBlock());
		}
		
		for (Image img : decorationDirectedSelectedImages) {
      img.addStyleName(ConnectorsClientBundle.INSTANCE.css().imageDispBlock());
    }
		
	}
	
	public void update(int direction, int left, int top) {
		// Remember direction
		this.direction = direction;
		
		// Set needed decoration
		setDecoration(isSelected(), direction);

		// Set element position
		switch (direction) {
		case VERTICAL_UP:
			left = left - (this.getWidget().getOffsetWidth() / 2) + 1;
			top = top - 1;
			break;
		case VERTICAL_DOWN:
			left = left - (this.getWidget().getOffsetWidth() / 2) + 1;
			top = top - this.getWidget().getOffsetHeight() + 1;
			break;
		case HORIZONTAL_LEFT:
			top = top - (this.getWidget().getOffsetHeight() / 2) + 1;
			left = left;
			break;
		case HORIZONTAL_RIGHT:
			top = top - (this.getWidget().getOffsetHeight() / 2) + 1;
			left = left - this.getWidget().getOffsetWidth();
			break;
		}
		((AbsolutePanel)this.getParent()).setWidgetPosition(this, left, top); 
	}
	
	private void setDecoration(boolean sel, int direction) {
		// Set decoration depending on direction and selection
		if (sel) {
			this.setWidget(decorationDirectedSelectedImages[direction]);
		} else {
			this.setWidget(decorationDirectedImages[direction]);
		}
	}

	public void showOnDiagram(AbsolutePanel panel, int direction, int left, int top) {
		// Add decoration to given panel
		panel.add(this, left, top);
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
