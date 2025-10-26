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
			String formattedDate = selectedDate.format(App.dateFormatter);
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
		LocalDate firstEntryDate = JournalManager.getFirstEntryDate();
		YearMonth yearMonth = YearMonth.of(firstEntryDate.getYear(), firstEntryDate.getMonth());
		calendarController.drawMonth(yearMonth);
		calendarController.selectDayOfMonth(firstEntryDate.getDayOfMonth() - 1);
	}

	@FXML
	private void onLastEntryAction() {
		LocalDate lastEntryDate = JournalManager.getLastEntryDate();
		YearMonth yearMonth = YearMonth.of(lastEntryDate.getYear(), lastEntryDate.getMonth());
		calendarController.drawMonth(yearMonth);
		calendarController.selectDayOfMonth(lastEntryDate.getDayOfMonth() - 1);
	}

	@FXML
	private void onNextEntryAction() {
		LocalDate selectedDate = calendarController.getSelectedDate();
		LocalDate nextEntryDate = JournalManager.getNextEntryDate(selectedDate);
		YearMonth newYearMonth = YearMonth.of(nextEntryDate.getYear(), nextEntryDate.getMonth());
		calendarController.drawMonth(newYearMonth);
		calendarController.selectDayOfMonth(nextEntryDate.getDayOfMonth() - 1);
	}

	@FXML
	private void onPreviousEntryAction() {
		LocalDate selectedDate = calendarController.getSelectedDate();
		LocalDate previousEntryDate = JournalManager.getPreviousEntryDate(selectedDate);
		YearMonth newYearMonth = YearMonth.of(previousEntryDate.getYear(), previousEntryDate.getMonth());
		calendarController.drawMonth(newYearMonth);
		calendarController.selectDayOfMonth(previousEntryDate.getDayOfMonth() - 1);
	}

	@FXML
	private void onTodayAction() {
		calendarController.drawMonth(YearMonth.now());
		int today = LocalDate.now().getDayOfMonth() - 1;
		calendarController.selectDayOfMonth(today);
	}
}