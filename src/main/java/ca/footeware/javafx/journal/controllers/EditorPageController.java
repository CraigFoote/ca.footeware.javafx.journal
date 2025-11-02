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
	 * Called after injection of widgets.
	 *
	 * @throws JournalException
	 */
	@FXML
	private void initialize() throws JournalException {
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
			textArea.textProperty().addListener((_, oldValue, newValue) -> {
				System.err.println("text listener, oldValue=" + oldValue + ", newValue=" + newValue);
				onTextChanged(oldValue, newValue);
			});
		} catch (IOException e) {
			App.notify(e.getMessage());
		}
	}

	@FXML
	private void onFirstEntryAction() throws JournalException {
		LocalDate firstEntryDate = JournalManager.getFirstEntryDate();
		if (firstEntryDate != null) {
			YearMonth yearMonth = YearMonth.of(firstEntryDate.getYear(), firstEntryDate.getMonth());
			calendarController.drawMonth(yearMonth);
			calendarController.selectDayOfMonth(firstEntryDate.getDayOfMonth());
		}
		setDirtyTitle(false);
		textArea.requestFocus();
	}

	@FXML
	private void onLastEntryAction() throws JournalException {
		LocalDate lastEntryDate = JournalManager.getLastEntryDate();
		if (lastEntryDate != null) {
			YearMonth yearMonth = YearMonth.of(lastEntryDate.getYear(), lastEntryDate.getMonth());
			calendarController.drawMonth(yearMonth);
			calendarController.selectDayOfMonth(lastEntryDate.getDayOfMonth());
		}
		setDirtyTitle(false);
		textArea.requestFocus();
	}

	@FXML
	private void onNextEntryAction() throws JournalException {
		LocalDate selectedDate = calendarController.getSelectedDate();
		LocalDate nextEntryDate = JournalManager.getNextEntryDate(selectedDate);
		if (!selectedDate.equals(nextEntryDate)) {
			YearMonth newYearMonth = YearMonth.of(nextEntryDate.getYear(), nextEntryDate.getMonth());
			calendarController.drawMonth(newYearMonth);
			calendarController.selectDayOfMonth(nextEntryDate.getDayOfMonth());
			setDirtyTitle(false);
			textArea.requestFocus();
		}
	}

	@FXML
	private void onPreviousEntryAction() throws JournalException {
		LocalDate selectedDate = calendarController.getSelectedDate();
		LocalDate previousEntryDate = JournalManager.getPreviousEntryDate(selectedDate);
		if (!selectedDate.equals(previousEntryDate)) {
			YearMonth newYearMonth = YearMonth.of(previousEntryDate.getYear(), previousEntryDate.getMonth());
			calendarController.drawMonth(newYearMonth);
			calendarController.selectDayOfMonth(previousEntryDate.getDayOfMonth());
			setDirtyTitle(false);
			textArea.requestFocus();
		}
	}

	@FXML
	private void onSaveAction() {
		System.err.println("onSaveAction");
		Task<Void> saveTask = new Task<>() {
			@Override
			protected Void call() throws Exception {
				updateProgress(2, 10);
				App.progressBar.setVisible(true);
				LocalDate selectedDate = calendarController.getSelectedDate();
				updateProgress(4, 10);
				JournalManager.addEntry(selectedDate, textArea.getText());
				updateProgress(8, 10);
				JournalManager.saveJournal();
				updateProgress(10, 10);
				return null;
			}

			@Override
			protected void succeeded() {
				setDirtyTitle(false);
				calendarController.colorizeEntryDays();
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
		if (event instanceof SelectionEvent selectionEvent) {
			LocalDate newDate = selectionEvent.getSelection().newDate();
			String oldEntry = textArea.getText();

			// old date entry saved, display new entry
			try {
				if (newDate != null) {
					String newEntry = JournalManager.getEntry(newDate);
					textArea.setText(newEntry);
					textArea.requestFocus();
				}

				calendarController.colorizeEntryDays();
				setDirtyTitle(false);
			} catch (JournalException e) {
				App.notify(e.getMessage());
			}
		}
	}

	private void onTextChanged(String oldValue, String newValue) {
		DateSelection currentSelection = calendarController.getCurrentSelection();
		if (currentSelection != null) {
			LocalDate currentDate = currentSelection.newDate();
			if (currentDate != null) {
				try {
					String entry = JournalManager.getEntry(currentDate);
					if ((newValue != null && newValue.equals(entry)) || (entry != null && newValue == null)) {
						setDirtyTitle(true);
					}
				} catch (JournalException e) {
					App.notify(e.getMessage());
				}
			}
		} else if (currentSelection == null && newValue != null) {
			setDirtyTitle(true);
		}
	}

	@FXML
	private void onTodayAction() {
		String text = textArea.getText();
		LocalDate now = LocalDate.now();
		try {
			if (!calendarController.isSaved(now, text)) {
				save(now, text);
			}
			calendarController.drawMonth(YearMonth.now());
			calendarController.selectDayOfMonth(LocalDate.now().getDayOfMonth());
			textArea.requestFocus();
			setDirtyTitle(false);
		} catch (JournalException e) {
			App.notify(e.getMessage());
		}
	}

	private void save(LocalDate date, String text) {
		Task<Void> saveTask = new Task<>() {
			@Override
			protected Void call() throws Exception {
				App.progressBar.setVisible(true);
				updateProgress(2, 10);
				JournalManager.addEntry(date, text);
				updateProgress(6, 10);
				JournalManager.saveJournal();
				updateProgress(10, 10);
				return null;
			}

			@Override
			protected void succeeded() {
				setDirtyTitle(false);
				calendarController.colorizeEntryDays();
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
	 * Toggle the window title as dirty (unsaved changes).
	 *
	 * @param makeDirty boolean
	 */
	private void setDirtyTitle(boolean makeDirty) {
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