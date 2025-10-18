package ca.footeware.javafx.journal.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;

import ca.footeware.javafx.journal.App;
import ca.footeware.javafx.journal.exceptions.JournalException;
import ca.footeware.javafx.journal.model.JournalManager;
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

	private CalendarController calendarController;

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
			if (object instanceof CalendarController cController) {
				calendarController = cController;
				calendarWrapper.getChildren().add(calendar);
				textArea.textProperty().bindBidirectional(calendarController.getSelectedEntry());
			}
		} catch (IOException e) {
			App.notify(e.getMessage());
		}
	}

	@FXML
	private void onSaveAction() {
		try {
			LocalDate selectedDate = calendarController.getSelectedDate();
			String formattedDate = selectedDate.format(CalendarController.dateFormatter);
			JournalManager.addEntry(formattedDate, textArea.getText());
			JournalManager.saveJournal();
			calendarController.colorizeEntryDays();
			App.notify("Journal was saved.");
		} catch (JournalException e) {
			App.notify(e.getMessage());
		}
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
		calendarController.drawMonth(YearMonth.now());
		int today = LocalDate.now().getDayOfMonth() - 1;
		calendarController.selectDayOfMonth(today);
	}
}