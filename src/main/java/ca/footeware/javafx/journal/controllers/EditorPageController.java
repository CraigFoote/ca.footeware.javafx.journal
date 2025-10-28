package ca.footeware.javafx.journal.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;

import ca.footeware.javafx.journal.App;
import ca.footeware.javafx.journal.exceptions.JournalException;
import ca.footeware.javafx.journal.model.JournalManager;
import javafx.concurrent.Task;
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

	/**
	 * Check if there are unsaved changes in the editor and prompt the user to save.
	 */
	private void checkEditorState() {
		if (((Stage) App.getPrimaryStage()).getTitle().startsWith("• ")) {
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
				// the textArea will display the selected entry from the calendar
				textArea.textProperty().bindBidirectional(calendarController.getSelectedEntry());
				// mark title dirty on text change
				textArea.textProperty().addListener((_, _, newValue) -> {
					try {
						String entry = JournalManager.getEntry(calendarController.getCurrentSelection());
						if (newValue != null && !newValue.equals(entry) || (entry == null && newValue != null)
								|| (entry != null && newValue == null)) {
							setDirtyTitle();
						}
					} catch (JournalException e) {
						App.notify(e.getMessage());
					}
				});
			}
		} catch (

		IOException e) {
			App.notify(e.getMessage());
		}
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
		Task<Void> progressTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				LocalDate selectedDate = calendarController.getSelectedDate();
				updateProgress(2, 10);
				String formattedDate = selectedDate.format(App.dateFormatter);
				updateProgress(4, 10);
				JournalManager.addEntry(formattedDate, textArea.getText());
				updateProgress(6, 10);
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
		calendarController.progressBar.setVisible(true);
		calendarController.progressBar.progressProperty().bind(progressTask.progressProperty());
		var t = new Thread(progressTask);
		t.setDaemon(true);
		t.start();
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