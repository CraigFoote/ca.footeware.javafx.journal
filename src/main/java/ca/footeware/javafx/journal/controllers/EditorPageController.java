package ca.footeware.javafx.journal.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;

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
import javafx.scene.Parent;
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

	/**
	 * Check if there are unsaved changes in the editor and prompt the user to save.
	 */
	private void checkEditorState() {
		if (isDirty()) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Unsaved Changes");
			alert.setHeaderText("You have unsaved changes.");
			alert.setContentText("Would you like to save them?");
			ButtonType yesButton = new ButtonType("Yes", ButtonType.YES.getButtonData());
			ButtonType noButton = new ButtonType("No", ButtonType.NO.getButtonData());
			alert.getButtonTypes().setAll(yesButton, noButton);
			alert.showAndWait().ifPresent(response -> {
				if (response == yesButton) {
					onSaveAction();
				}
			});
		}
	}

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
			Object object = loader.getController();
			if (object instanceof CalendarController cController) {
				calendarController = cController;
				calendarWrapper.getChildren().add(calendar);

				// Listen for date selection events and update textArea
				calendar.addEventHandler(SelectionEvent.DATE_SELECTED,
						event -> textArea.setText(event.getSelection().newEntry()));

				// mark title dirty on text change
				textArea.textProperty().addListener((_, oldValue, newValue) -> onTextChanged(oldValue, newValue));

				// handle selection events
				((Parent) loader.getRoot()).addEventHandler(SelectionEvent.DATE_SELECTED, this::onSelectionEvent);
			}
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
			calendarController.selectDayOfMonth(firstEntryDate.getDayOfMonth() - 1);
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
			calendarController.selectDayOfMonth(lastEntryDate.getDayOfMonth() - 1);
		}
		clearDirtyTitle();
		textArea.requestFocus();
	}

	@FXML
	private void onNextEntryAction() {
		checkEditorState();
		LocalDate selectedDate = calendarController.getSelectedDate();
		LocalDate nextEntryDate = JournalManager.getNextEntryDate(selectedDate);
		YearMonth newYearMonth = YearMonth.of(nextEntryDate.getYear(), nextEntryDate.getMonth());
		calendarController.drawMonth(newYearMonth);
		calendarController.selectDayOfMonth(nextEntryDate.getDayOfMonth() - 1);
		clearDirtyTitle();
		textArea.requestFocus();
	}

	@FXML
	private void onPreviousEntryAction() {
		checkEditorState();
		LocalDate selectedDate = calendarController.getSelectedDate();
		LocalDate previousEntryDate = JournalManager.getPreviousEntryDate(selectedDate);
		YearMonth newYearMonth = YearMonth.of(previousEntryDate.getYear(), previousEntryDate.getMonth());
		calendarController.drawMonth(newYearMonth);
		calendarController.selectDayOfMonth(previousEntryDate.getDayOfMonth() - 1);
		clearDirtyTitle();
		textArea.requestFocus();
	}

	@FXML
	private void onSaveAction() {
		Task<Void> saveTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				updateProgress(2, 10);
				calendarController.progressBar.setVisible(true);
				LocalDate selectedDate = calendarController.getSelectedDate();
				updateProgress(4, 10);
				String formattedDate = selectedDate.format(App.dateFormatter);
				updateProgress(6, 10);
				JournalManager.addEntry(formattedDate, textArea.getText());
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
				calendarController.progressBar.setVisible(false);
			}
		};
		calendarController.progressBar.progressProperty().bind(saveTask.progressProperty());
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
		if (event instanceof SelectionEvent selectionEvent && isDirty()) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Unsaved Changes");
			alert.setHeaderText("You have unsaved changes.");
			alert.setContentText("Would you like to save them?");
			ButtonType yesButton = new ButtonType("Yes", ButtonType.YES.getButtonData());
			ButtonType noButton = new ButtonType("No", ButtonType.NO.getButtonData());
			alert.getButtonTypes().setAll(yesButton, noButton);
			alert.showAndWait().ifPresent(response -> {
				if (response == yesButton) {
					DateSelection selection = selectionEvent.getSelection();
					LocalDate oldSelectedDate = selection.oldDate();
					String oldEntry = selection.oldEntry();
					try {
						JournalManager.addEntry(oldSelectedDate.format(App.dateFormatter), oldEntry);
						JournalManager.saveJournal();
						calendarController.colorizeEntryDays();
						clearDirtyTitle();
						textArea.requestFocus();
					} catch (JournalException e) {
						App.notify(e.getMessage());
					}
				}
			});
		}
	}

	private void onTextChanged(String oldValue, String newValue) {
		System.err.println("\n editorPage onTextChanged");
		System.err.println("\toldValue: " + oldValue);
		System.err.println("\tnewValue: " + newValue);
		try {
			DateSelection currentSelection = calendarController.getCurrentSelection();
			String entry = JournalManager.getEntry(currentSelection.newDate());
			if ((newValue != null && !newValue.equals(entry)) || (entry != null && newValue == null)) {
				System.err.println("\tdirty");
				setDirtyTitle();
			}
		} catch (JournalException e) {
			App.notify(e.getMessage());
		}
	}

	@FXML
	private void onTodayAction() {
		checkEditorState();
		calendarController.drawMonth(YearMonth.now());
		int today = LocalDate.now().getDayOfMonth() - 1;
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