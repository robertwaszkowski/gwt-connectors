package pl.tecna.gwt.connectors.client.util;

public class Possition {

	private int left;
	private int top;
	
	public Possition() {
		
		this.left = -1;
		this.top = -1;
	}
	
	public Possition(int left, int top) {
		
		this.left = left;
		this.top = top;
	}
	
	public void setLeft(int left) {
		this.left = left;
	}
	public int getLeft() {
		return left;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public int getTop() {
		return top;
	}

}
