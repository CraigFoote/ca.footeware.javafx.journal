package ca.footeware.javafx.journal;

import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import datechooser.view.CalendarPane;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * MVC Controller for the "Editor" page.
 */
public class EditorPageController {

	@FXML
	private HBox calendarBox;

	@FXML
	private Button firstButton;

	@FXML
	private Button previousButton;

	@FXML
	private Button todayButton;

	@FXML
	private Button nextButton;

	@FXML
	private Button lastButton;

	void createCalendar() {
		SwingNode swingNode = new SwingNode();
		createSwingContent(swingNode);
		calendarBox.setMinWidth(220);
		calendarBox.getChildren().add(swingNode);
	}

	private void createSwingContent(final SwingNode swingNode) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				CalendarPane datePanel = new CalendarPane();
				datePanel.setBounds(new Rectangle(10, 10, 200, 200));
				swingNode.setContent(datePanel);
			}
		});
	}

}