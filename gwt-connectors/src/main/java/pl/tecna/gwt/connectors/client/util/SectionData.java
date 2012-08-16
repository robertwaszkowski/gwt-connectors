package pl.tecna.gwt.connectors.client.util;

import pl.tecna.gwt.connectors.client.Point;

public class SectionData {

	public Point startPoint;
	public Point endPoint;
	public boolean vertical;
	
	public SectionData(int startX, int startY, int endX, int endY, boolean vertical) {
		
		this.startPoint = new Point(startX, startY);
		this.endPoint = new Point(endX, endY);
		this.vertical = vertical;
	}
	
	public SectionData(Point start, Point end, boolean vertical) {
		
		this.startPoint = start;
		this.endPoint = end;
		this.vertical = vertical;
	}
}
