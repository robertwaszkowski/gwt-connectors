package pl.tecna.gwt.connectors.example.client;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;

public class BPMNTask extends FocusPanel {

	private static final int TASK_WIDTH = 151;
	
	public BPMNTask() {
		super();
		
		HTML html = new HTML();
		String htmlText = 
				  "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"shadow\">" +
						    "<tbody>" +
						      "<tr>" +
						        "<td width=\"100\" class=\"\" style=\"padding-top:10px; padding-left:5px; padding-right:5px\">" + "Task name" + "</td>" +
						      "</tr>" +
						        "<tr>" +
						        "<td width=\"" + TASK_WIDTH + "\" align=\"right\" class=\"\"" +
						          "style=\"padding-top:13px; padding-bottom:5px; padding-right:10px;\">" + 
						          "[" +
						          "<i>" + "Role name" + "</i>" + 
						          "]" +
						        "</td>" +
						      "</tr>" +
						    "</tbody>" +
						  "</table>";
		
		html.setHTML(htmlText);
		setWidget(html);
		addStyleName("gwt-connectors-test-task");
		
	}
	
}
