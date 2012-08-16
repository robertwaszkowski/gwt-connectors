package pl.tecna.gwt.connectors.client;

import com.google.gwt.user.client.ui.FocusPanel;

public class Point extends FocusPanel implements Comparable<Point>{
	
	private Integer top;
	private Integer left;

	public Point(Integer left, Integer top) {
		super();
		this.left = left;
		this.top = top;
	}

	public void showOnDiagram() {
		
	}

	public Integer getTop() {
		return top;
	}

	public void setTop(Integer top) {
		this.top = top;
	}

	public Integer getLeft() {
		return left;
	}

	public void setLeft(Integer left) {
		this.left = left;
	}
	
	public String toDebugString() {
		return "top:" + top + " left:" + left; 
	}

	/**
	 * Compares two points using their left and top values. <br>
	 * If values are equal 0 is returned. If values are not eqal, 
	 * sumary of absolute differences between points is returned.
	 */
	public int compareTo(Point o) {
		int leftCompare = o.getLeft().compareTo(this.getLeft());
		int topCompare = o.getTop().compareTo(this.getTop());
		if (leftCompare == 0 && topCompare == 0) {
			return 0;
		} else {
			return Math.abs(leftCompare) + Math.abs(topCompare);
		}
	}
	
}
