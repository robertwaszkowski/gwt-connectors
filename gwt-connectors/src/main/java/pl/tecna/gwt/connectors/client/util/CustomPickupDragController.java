package pl.tecna.gwt.connectors.client.util;

import java.util.ArrayList;
import java.util.List;

import pl.tecna.gwt.connectors.client.elements.Shape;

import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Pickup drag controller with list of draggable widgets. 
 * @author Kamil
 *
 */
public class CustomPickupDragController extends PickupDragController {
	
	public List<Widget> dragableWidgets;

	public CustomPickupDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
		dragableWidgets = new ArrayList<Widget>();
		
		addDragHandler(new DragHandlerAdapter() {
		  
		  @Override
		  public void onPreviewDragStart(DragStartEvent event) {
		    if (event.getContext().draggable instanceof Shape) {
		      Shape shape = (Shape) event.getContext().draggable;
		      shape.hideShapeConnectorStartPionts();
 		    }
		  }  
		});
	}
	
	@Override
	public void makeDraggable(Widget draggable, Widget dragHandle) {

		if (!(draggable instanceof Shape)) {
		  draggable.addStyleName(ConnectorsClientBundle.INSTANCE.css().widgetPaddingUnselected());
		}
		dragableWidgets.add(draggable);
		super.makeDraggable(draggable, dragHandle);
	}
	
	@Override
	public void makeNotDraggable(Widget draggable) {
		
		dragableWidgets.remove(draggable);
		super.makeNotDraggable(draggable);
	}

}
