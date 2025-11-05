package ca.footeware.javafx.journal.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import ca.footeware.javafx.journal.App;
import ca.footeware.javafx.journal.exceptions.JournalException;
import ca.footeware.javafx.journal.model.DateSelection;
import ca.footeware.javafx.journal.model.JournalManager;
import ca.footeware.javafx.journal.model.SelectionEvent;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * MVC pattern Controller for the "Editor" page.
 */
public class EditorPageController {

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private CalendarController calendarController;

	@FXML
	private HBox calendarWrapper;

	@FXML
	private TextArea textArea;

	/**
	 * Determine if the textArea has altered text and, if so, prompt the user to
	 * save.
	 *
	 * @param oldDate {@link LocalDate}
	 * @param newDate {@link LocalDate}
	 */
	private void checkDirty(LocalDate oldDate, LocalDate newDate) {
		if (oldDate != null) {
			String displayedText = textArea.getText();
			try {
				String oldEntry = JournalManager.getEntry(oldDate);
				boolean datesEqual = oldDate.equals(newDate); // selected same day
				boolean textsNull = displayedText == null && oldEntry == null;
				boolean textsMatch = oldEntry != null && oldEntry.equals(displayedText);
				boolean isDirty = !datesEqual && !textsNull && !textsMatch;
				if (isDirty) {
					// edits made, prompt to save then show newly selected entry
					promptToSave(oldDate, displayedText);
				}
			} catch (JournalException e) {
				App.notify(e.getMessage());
			}
		}
	}

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

				// Listen for date selection events and update textArea
				calendarWrapper.addEventHandler(SelectionEvent.DATE_SELECTED, this::onSelectionEvent);

				// select today
				calendarController.fireSelectionEvent(LocalDate.now());
			}

			// mark title dirty on text change
			textArea.textProperty().addListener((_, _, newValue) -> onTextChanged(newValue));
		} catch (IOException e) {
			App.notify(e.getMessage());
		}
	}

	@FXML
	private void onFirstEntryAction() {
		LocalDate firstEntryDate = JournalManager.getFirstEntryDate();
		if (firstEntryDate != null) {
			YearMonth yearMonth = YearMonth.of(firstEntryDate.getYear(), firstEntryDate.getMonth());
			calendarController.drawMonth(yearMonth);
			calendarController.selectDayOfMonth(firstEntryDate.getDayOfMonth());
		}
	}

	@FXML
	private void onLastEntryAction() {
		LocalDate lastEntryDate = JournalManager.getLastEntryDate();
		if (lastEntryDate != null) {
			YearMonth yearMonth = YearMonth.of(lastEntryDate.getYear(), lastEntryDate.getMonth());
			calendarController.drawMonth(yearMonth);
			calendarController.selectDayOfMonth(lastEntryDate.getDayOfMonth());
		}
	}

	@FXML
	private void onNextEntryAction() {
		LocalDate selectedDate = calendarController.getSelectedDate();
		LocalDate nextEntryDate = JournalManager.getNextEntryDate(selectedDate);
		if (!selectedDate.equals(nextEntryDate)) {
			YearMonth newYearMonth = YearMonth.of(nextEntryDate.getYear(), nextEntryDate.getMonth());
			calendarController.drawMonth(newYearMonth);
			calendarController.selectDayOfMonth(nextEntryDate.getDayOfMonth());
		}
	}

	@FXML
	private void onPreviousEntryAction() {
		LocalDate selectedDate = calendarController.getSelectedDate();
		LocalDate previousEntryDate = JournalManager.getPreviousEntryDate(selectedDate);
		if (!selectedDate.equals(previousEntryDate)) {
			YearMonth newYearMonth = YearMonth.of(previousEntryDate.getYear(), previousEntryDate.getMonth());
			calendarController.drawMonth(newYearMonth);
			calendarController.selectDayOfMonth(previousEntryDate.getDayOfMonth());
		}
	}

	@FXML
	private void onSaveAction() {
		LocalDate selectedDate = calendarController.getSelectedDate();
		String text = textArea.getText();
		save(selectedDate, text);
	}

	/**
	 * Handle a selection event.
	 *
	 * @param Event {@link Event}
	 */
	private void onSelectionEvent(Event event) {
		if (event instanceof SelectionEvent selectionEvent) {
			LocalDate oldDate = selectionEvent.getSelection().oldDate();
			LocalDate newDate = selectionEvent.getSelection().newDate();

			checkDirty(oldDate, newDate);

			// old date's entry edits saved or abandoned, display new entry
			if (newDate != null && JournalManager.hasDate(newDate)) {
				try {
					String newEntry = JournalManager.getEntry(newDate);
					textArea.setText(newEntry);
					textArea.requestFocus();

					calendarController.colorizeEntryDays();
					setDirty(false);
				} catch (JournalException e) {
					App.notify(e.getMessage());
				}
			} else {
				textArea.setText(null);
				textArea.requestFocus();

				calendarController.colorizeEntryDays();
				setDirty(false);
			}
		}
		event.consume();
	}

	/**
	 * Respond to textual changes in the journal editor.
	 *
	 * @param newValue {@link String}
	 */
	private void onTextChanged(String newValue) {
		DateSelection currentSelection = calendarController.getCurrentSelection();
		if (currentSelection != null && currentSelection.newDate() != null) {
			try {
				String entry = JournalManager.getEntry(currentSelection.newDate());
				if ((newValue != null && newValue.equals(entry)) || (entry != null && newValue == null)
						|| (newValue != null && entry != null)) {
					setDirty(true);
				}
			} catch (JournalException e) {
				App.notify(e.getMessage());
			}
		} else if (newValue != null) {
			setDirty(true);
		}
	}

	@FXML
	private void onTodayAction() {
		LocalDate now = LocalDate.now();
		calendarController.drawMonth(YearMonth.now());
		calendarController.selectDayOfMonth(now.getDayOfMonth());
	}

	/**
	 * Prompt the user to save the previous but unsaved edit using an {@link Alert}
	 * dialog.
	 *
	 * @param date {@link LocalDate}
	 * @param text {@link String}
	 */
	private void promptToSave(LocalDate date, String text) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Unsaved Changes");
		String formatted = date.format(dateFormatter);
		alert.setHeaderText("You have unsaved changes on " + formatted + ".");
		alert.setContentText("Would you like to save them?");
		ButtonType yesButton = new ButtonType("Yes", ButtonType.YES.getButtonData());
		ButtonType noButton = new ButtonType("No", ButtonType.NO.getButtonData());
		alert.getButtonTypes().setAll(yesButton, noButton);
		alert.showAndWait().ifPresent(response -> {
			if (response == yesButton) {
				save(date, text);
			}
		});
	}

	/**
	 * Save the provided date and associated text in the journal.
	 *
	 * @param date {@link LocalDate}
	 * @param text {@link String}
	 */
	private void save(LocalDate date, String text) {
		// run in a new Thread then update UI
		Task<Void> saveTask = new Task<>() {
			@Override
			protected Void call() throws Exception {
				updateProgress(2, 10);
				App.getProgressBar().setVisible(true);
				updateProgress(4, 10);
				JournalManager.addEntry(date, text);
				updateProgress(6, 10);
				JournalManager.saveJournal();
				updateProgress(10, 10);
				return null;
			}

			@Override
			protected void succeeded() {
				setDirty(false);
				calendarController.colorizeEntryDays();
				textArea.requestFocus();
				App.notify("Journal was saved.");
				App.getProgressBar().progressProperty().unbind();
				App.getProgressBar().setVisible(false);
			}
		};
		App.getProgressBar().progressProperty().bind(saveTask.progressProperty());
		var thread = new Thread(saveTask);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Toggle the editor as dirty (unsaved changes).
	 *
	 * @param makeDirty boolean
	 */
	private void setDirty(boolean makeDirty) {
		String title = ((Stage) App.getPrimaryStage()).getTitle();
		if (makeDirty) {
			if (!title.startsWith("• ")) {
				((Stage) App.getPrimaryStage()).setTitle("• " + title);
			}
		} else {
			if (title.startsWith("• ")) {
				((Stage) App.getPrimaryStage()).setTitle(title.substring(2));
			}
		}
	}
}