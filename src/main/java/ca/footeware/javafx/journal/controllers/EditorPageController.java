package ca.footeware.javafx.journal.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;

import ca.footeware.javafx.journal.App;
import ca.footeware.javafx.journal.exceptions.JournalException;
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
 * MVC Controller for the "Editor" page.
 */
public class EditorPageController {

	private CalendarController calendarController;

	@FXML
	private HBox calendarWrapper;

	@FXML
	private TextArea textArea;

	private String displayedText;

	/**
	 * Check if there are unsaved changes in the editor and prompt the user to save.
	 */
	private void checkEditorState() {
		System.err.println("\ncheckEditorState");
		if (isDirty()) {
			System.err.println("\tisDirty=" + isDirty());
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Unsaved Changes");
			alert.setHeaderText("You have unsaved changes.");
			alert.setContentText("Would you like to save them?");
			ButtonType yesButton = new ButtonType("Yes", ButtonType.YES.getButtonData());
			ButtonType noButton = new ButtonType("No", ButtonType.NO.getButtonData());
			alert.getButtonTypes().setAll(yesButton, noButton);
			alert.showAndWait().ifPresent(response -> {
				if (response == yesButton) {
					System.err.println("\tcalling onSaveAction");
					onSaveAction();
				}
			});
		}
	}

	/**
	 * Remove the "• " prefix from the title if exists.
	 */
	private void clearDirtyTitle() {
		String title = ((Stage) App.getPrimaryStage()).getTitle();
		if (title.startsWith("• ")) {
			((Stage) App.getPrimaryStage()).setTitle(title.substring(2));
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

			// Listen for date selection events and update textArea
			calendar.addEventHandler(SelectionEvent.DATE_SELECTED, this::onSelectionEvent);

			Object object = loader.getController();
			if (object instanceof CalendarController cController) {
				calendarController = cController;
				calendarWrapper.getChildren().add(calendar);

				calendarController.selectDayOfMonth(LocalDate.now().getDayOfMonth());
			}

			// mark title dirty on text change
			textArea.textProperty().addListener((_, oldValue, newValue) -> onTextChanged(oldValue, newValue));
		} catch (IOException e) {
			App.notify(e.getMessage());
		}
	}

	private boolean isDirty() {
		String title = ((Stage) App.getPrimaryStage()).getTitle();
		return title.startsWith("• ");
	}

	@FXML
	private void onFirstEntryAction() {
		checkEditorState();
		LocalDate firstEntryDate = JournalManager.getFirstEntryDate();
		if (firstEntryDate != null) {
			YearMonth yearMonth = YearMonth.of(firstEntryDate.getYear(), firstEntryDate.getMonth());
			calendarController.drawMonth(yearMonth);
			calendarController.selectDayOfMonth(firstEntryDate.getDayOfMonth());
		}
		clearDirtyTitle();
		textArea.requestFocus();
	}

	@FXML
	private void onLastEntryAction() {
		checkEditorState();
		LocalDate lastEntryDate = JournalManager.getLastEntryDate();
		if (lastEntryDate != null) {
			YearMonth yearMonth = YearMonth.of(lastEntryDate.getYear(), lastEntryDate.getMonth());
			calendarController.drawMonth(yearMonth);
			calendarController.selectDayOfMonth(lastEntryDate.getDayOfMonth());
		}
		clearDirtyTitle();
		textArea.requestFocus();
	}

	@FXML
	private void onNextEntryAction() {
		checkEditorState();
		LocalDate selectedDate = calendarController.getSelectedDate();
		LocalDate nextEntryDate = JournalManager.getNextEntryDate(selectedDate);
		if (!selectedDate.equals(nextEntryDate)) {
			YearMonth newYearMonth = YearMonth.of(nextEntryDate.getYear(), nextEntryDate.getMonth());
			calendarController.drawMonth(newYearMonth);
			calendarController.selectDayOfMonth(nextEntryDate.getDayOfMonth());
			clearDirtyTitle();
			textArea.requestFocus();
		}
	}

	@FXML
	private void onPreviousEntryAction() {
		checkEditorState();
		LocalDate selectedDate = calendarController.getSelectedDate();
		LocalDate previousEntryDate = JournalManager.getPreviousEntryDate(selectedDate);
		YearMonth newYearMonth = YearMonth.of(previousEntryDate.getYear(), previousEntryDate.getMonth());
		calendarController.drawMonth(newYearMonth);
		calendarController.selectDayOfMonth(previousEntryDate.getDayOfMonth());
		clearDirtyTitle();
		textArea.requestFocus();
	}

	@FXML
	private void onSaveAction() {
		Task<Void> saveTask = new Task<>() {
			@Override
			protected Void call() throws Exception {
				updateProgress(2, 10);
				App.progressBar.setVisible(true);
				LocalDate selectedDate = calendarController.getSelectedDate();
				updateProgress(4, 10);
				String formattedDate = selectedDate.format(App.dateFormatter);
				System.err.println("\nonSaveAction saveTask, adding entry for " + formattedDate + ": " + displayedText);
				updateProgress(6, 10);
				// displayedText should have been set by now
				JournalManager.addEntry(formattedDate, displayedText);
				updateProgress(8, 10);
				JournalManager.saveJournal();
				updateProgress(10, 10);
				return null;
			}

			@Override
			protected void succeeded() {
				calendarController.colorizeEntryDays();
				clearDirtyTitle();
				textArea.requestFocus();
				App.notify("Journal was saved.");
				App.progressBar.progressProperty().unbind();
				App.progressBar.setVisible(false);
			}
		};
		App.progressBar.progressProperty().bind(saveTask.progressProperty());
		var t = new Thread(saveTask);
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Handle a selection event.
	 *
	 * @param Event {@link Event}
	 */
	private void onSelectionEvent(Event event) {
		System.err.println("\nonSelectionEvent");
		if (event instanceof SelectionEvent selectionEvent) {
			LocalDate newDate = selectionEvent.getSelection().newDate();
			displayedText = textArea.getText();
			checkEditorState();

			// old date entry saved, display new entry
			String newEntry = null;
			try {
				if (newDate != null) {
					newEntry = JournalManager.getEntry(newDate);
				}
				System.err.println("\tsetting textArea to newEntry: " + newEntry);
				textArea.setText(newEntry);
				textArea.requestFocus();

				calendarController.colorizeEntryDays();
				clearDirtyTitle();
			} catch (JournalException e) {
				App.notify(e.getMessage());
			}

//			if (isDirty()) {
//				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//				alert.setTitle("Unsaved Changes");
//				alert.setHeaderText("You have unsaved changes.");
//				alert.setContentText("Would you like to save them?");
//				ButtonType yesButton = new ButtonType("Yes", ButtonType.YES.getButtonData());
//				ButtonType noButton = new ButtonType("No", ButtonType.NO.getButtonData());
//				alert.getButtonTypes().setAll(yesButton, noButton);
//				alert.showAndWait().ifPresent(response -> {
//					if (response == yesButton) {
//						DateSelection selection = selectionEvent.getSelection();
//						LocalDate oldSelectedDate = selection.oldDate();
//						String oldEntry = textArea.getText();
//						try {
//							JournalManager.addEntry(oldSelectedDate.format(App.dateFormatter), oldEntry);
//							JournalManager.saveJournal();
//							calendarController.colorizeEntryDays();
//							clearDirtyTitle();
//							// old date entry saved, display new entry
//							String newEntry = JournalManager.getEntry(selection.newDate());
//							textArea.setText(newEntry);
//							textArea.requestFocus();
//						} catch (JournalException e) {
//							App.notify(e.getMessage());
//						}
//					} else {
//						// user pressed no - clear the dirty indicator
//						clearDirtyTitle();
//						textArea.requestFocus();
//					}
//				});
//			} else {
//				LocalDate newDate = selectionEvent.getSelection().newDate();
//				try {
//					calendarController.colorizeEntryDays();
//					clearDirtyTitle();
//					String entry = null;
//					if (newDate != null) {
//						entry = JournalManager.getEntry(newDate);
//					}
//					textArea.setText(entry);
//				} catch (JournalException e) {
//					App.notify(e.getMessage());
//				}
//			}
		}
	}

	private void onTextChanged(String oldValue, String newValue) {
		System.err.println("\neditorPage onTextChanged");
		System.err.println("\toldValue: " + oldValue);
		System.err.println("\tnewValue: " + newValue);
		LocalDate currentDate = calendarController.getCurrentSelection().newDate();
		if (currentDate != null) {
			try {
				displayedText = textArea.getText(); // used in #onSaveAction TODO streamline
				System.err.println("\tdisplayedText=" + displayedText);
				String entry = JournalManager.getEntry(currentDate);
				System.err.println("\tentry: " + entry);
				if ((newValue != null && !newValue.equals(entry)) || (entry != null && newValue == null)) {
					System.err.println("\tdirty");
					setDirtyTitle();
				}
			} catch (JournalException e) {
				App.notify(e.getMessage());
			}
		}
	}

	@FXML
	private void onTodayAction() {
		checkEditorState();
		calendarController.drawMonth(YearMonth.now());
		int today = LocalDate.now().getDayOfMonth();
		calendarController.selectDayOfMonth(today);
		clearDirtyTitle();
		textArea.requestFocus();
	}

	/**
	 * Mark the window title as dirty (unsaved changes).
	 */
	private void setDirtyTitle() {
		String title = ((Stage) App.getPrimaryStage()).getTitle();
		if (!title.startsWith("• ")) {
			((Stage) App.getPrimaryStage()).setTitle("• " + title);
		}
	}
}