package ca.footeware.javafx.journal.controllers;

import java.io.IOException;
import java.net.URL;

import ca.footeware.javafx.journal.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

/**
 * MVC Controller for the "Editor" page.
 */
public class EditorPageController {

	@FXML
	private TextArea textArea;

	@FXML
	private HBox calendarWrapper;

	/**
	 * Called after injection of widgets.
	 */
	@FXML
	private void initialize() {
		URL resource = getClass().getResource("/calendar.fxml");
		Node calendar;
		try {
			FXMLLoader loader = new FXMLLoader(resource);
			calendar = loader.load();
			Object object = loader.getController();
			if (object instanceof CalendarController controller) {
				calendarWrapper.getChildren().add(calendar);
				textArea.textProperty().bind(controller.getSelectedEntry());
			}
		} catch (IOException e) {
			App.notify(e.getMessage());
		}
	}

	@FXML
	private void onSaveAction() {
//		try {
//			LocalDate date = currentYearMonth.atDay(currentDayOfMonth);
//			String formatted = date.format(dateFormatter);
//			JournalManager.addEntry(formatted, textArea.getText());
//			JournalManager.saveJournal();
		////			paintshop();
//			App.notify("Journal was saved.");
//		} catch (JournalException e) {
//			App.notify(e.getMessage());
//		}
	}

	@FXML
	private void onFirstEntryAction() {
		App.sayHello();
	}

	@FXML
	private void onLastEntryAction() {
		App.sayHello();
	}

	@FXML
	private void onNextEntryAction() {
		App.sayHello();
	}

	@FXML
	private void onPreviousEntryAction() {
		App.sayHello();
	}

	@FXML
	private void onTodayAction() {
//		drawMonth(YearMonth.now());
//		currentDayOfMonth = LocalDate.now().getDayOfMonth();
//		Node node = dateGrid.getChildrenUnmodifiable().get(currentDayOfMonth - 1);
//		if (node instanceof Label label) {
//			onDayLabelClicked(label);
//		}
	}
}