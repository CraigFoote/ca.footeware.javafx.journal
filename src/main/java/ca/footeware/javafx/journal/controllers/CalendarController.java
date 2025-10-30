package ca.footeware.javafx.journal.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;

import ca.footeware.javafx.journal.App;
import ca.footeware.javafx.journal.exceptions.JournalException;
import ca.footeware.javafx.journal.model.DateSelection;
import ca.footeware.javafx.journal.model.JournalManager;
import ca.footeware.javafx.journal.model.SelectionEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 *
 */
public class CalendarController extends VBox {

	private DateSelection currentSelection;
	private YearMonth currentYearMonth;

	@FXML
	private GridPane dateGrid;

	@FXML
	private Label monthLabel;

	@FXML
	public ProgressBar progressBar;

	private ObjectProperty<DateSelection> selectedEntry;

	@FXML
	private Label yearLabel;

	/**
	 * Constructor.
	 */
	public CalendarController() {
		super();
		currentYearMonth = YearMonth.now();
		setSpacing(10);
	}

	/**
	 * Clear the calendar days' background colors.
	 */
	private void clearBackgrounds() {
		dateGrid.getChildren().forEach(node -> {
			if (node instanceof Label label) {
				label.setBackground(null);
			}
		});
	}

	/**
	 * Clear all {@link #dateGrid} labels of borders.
	 */
	private void clearBorders() {
		for (Node node : dateGrid.getChildren()) {
			if (node instanceof Label label) {
				label.setBorder(null);
			}
		}
	}

	/**
	 * Colorize days that have journal entries in the calendar.
	 */
	public void colorizeEntryDays() {
		clearBackgrounds();
		List<String> entryDates = JournalManager.getEntryDates();
		for (String entryDateStr : entryDates) {
			LocalDate entryDate = LocalDate.parse(entryDateStr, App.dateFormatter);
			// filter to retain only those days in the currently selected YearMonth
			if (entryDate.getYear() == currentYearMonth.getYear()
					&& entryDate.getMonth() == currentYearMonth.getMonth()) {
				String dayNumStr = entryDateStr.substring(entryDateStr.lastIndexOf("-") + 1); // yyyy-MM-dd
				int dayNum = Integer.parseInt(dayNumStr) - 1; // days are 1-based
				Node node = dateGrid.getChildren().get(dayNum);
				if (node instanceof Label label) {
					label.setBackground(new Background(
							new BackgroundFill(Color.color(0.275, 0.51, 0.706), new CornerRadii(5), null))); // blue
				}
			}
		}
	}

	/**
	 * Colorize today's date in the calendar.
	 */
	private void colorizeToday() {
		LocalDateTime now = LocalDateTime.now();
		for (Node node : dateGrid.getChildren()) {
			// if it's this year and month and label matches today's date
			if (currentYearMonth.getYear() == now.getYear() && currentYearMonth.getMonth() == now.getMonth()
					&& node instanceof Label label && label.getText().equals(String.valueOf(now.getDayOfMonth()))) {
				label.setFont(Font.font(label.getFont().getFamily(), FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 12.0));
				label.getStyleClass().add("today");
				return;
			}
		}
	}

	/**
	 * Create this month's calendar days in the {@link #dateGrid}.
	 */
	private void createDateGrid() {
		dateGrid.getChildren().clear();

		int lengthOfMonth = currentYearMonth.lengthOfMonth();

		// find the column index of the 1st day with Sunday=0
		LocalDate firstOfMonth = currentYearMonth.atDay(1);
		int firstColumn = firstOfMonth.getDayOfWeek().getValue() % 7;

		for (int day = 1; day <= lengthOfMonth; day++) {
			int index = firstColumn + (day - 1);
			int row = index / 7;
			int col = index % 7;

			final Label dayLabel = new Label(Integer.toString(day));
			GridPane.setHalignment(dayLabel, HPos.CENTER);

			dayLabel.setOnMouseClicked(_ -> fireSelectionEvent(dayLabel));
			dayLabel.setCursor(Cursor.HAND);
			dayLabel.setTextFill(Color.WHITE);
			dayLabel.setFont(Font.font(dayLabel.getFont().getFamily(), FontWeight.NORMAL, FontPosture.REGULAR, 12.0));
			dayLabel.setPadding(new Insets(1, 10, 1, 10));

			dateGrid.add(dayLabel, col, row);
		}
	}

	/**
	 * Draw the calendar to show the provided {@link YearMonth}.
	 *
	 * @param ym {@link YearMonth}
	 */
	public void drawMonth(YearMonth ym) {
		currentYearMonth = ym;
		yearLabel.setText(String.valueOf(currentYearMonth.getYear()));
		monthLabel.setText(currentYearMonth.getMonth().toString());

		createDateGrid();
		colorizeToday();
		colorizeEntryDays();

		selectedEntry.setValue(null);
	}

	/**
	 * Creates and fires a selection event.
	 * 
	 * @param label {@link Label} the originating control
	 */
	private void fireSelectionEvent(Label label) {
		try {
			// last event - may be used to save previous selection
			LocalDate oldSelectedDate = null;
			String oldEntry = null;
			if (currentSelection != null) {
				oldSelectedDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(),
						currentSelection.oldDate().getDayOfMonth());
				oldEntry = JournalManager.getEntry(oldSelectedDate);
			}

			// trigger state update
			onDayLabelClicked(label);

			// new event
			LocalDate newSelectedDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(),
					currentSelection.newDate().getDayOfMonth());
			String newEntry = JournalManager.getEntry(newSelectedDate);

			// selection object
			DateSelection dateSelection = new DateSelection(oldSelectedDate, newSelectedDate, oldEntry, newEntry,
					label);

			// fire event
			SelectionEvent selectionEvent = new SelectionEvent(SelectionEvent.DATE_SELECTED, dateSelection);
			currentSelection = dateSelection;
			label.fireEvent(selectionEvent);
		} catch (JournalException e) {
			App.notify(e.getMessage());
		}
	}

	/**
	 * Get the currently selected day as a string.
	 *
	 * @return {@link String}
	 */
	public DateSelection getCurrentSelection() {
		return currentSelection;
	}

	/**
	 * Gets the currently selected date.
	 *
	 * @return {@link LocalDate}
	 */
	public LocalDate getSelectedDate() {
		return LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(),
				currentSelection.newDate().getDayOfMonth());
	}

	/**
	 * Get the currently selected day's entry.
	 *
	 * @return {@link StringProperty} the selected day's entry
	 */
	public ObjectProperty<DateSelection> getSelectedEntry() {
		return selectedEntry;
	}

	@FXML
	private void initialize() {
		createDateGrid();
		colorizeEntryDays();
		colorizeToday();

		yearLabel.setText(String.valueOf(currentYearMonth.getYear()));
		monthLabel.setText(currentYearMonth.getMonth().toString());

		// select today by default
		LocalDate now = LocalDate.now();
		int dayOfMonth = now.getDayOfMonth();
		String dayOfMonthStr = String.valueOf(dayOfMonth);
		for (Node node : dateGrid.getChildren()) {
			if (node instanceof Label label && label.getText().equals(dayOfMonthStr)) {
				// we found the day of the month
				setBorder(label);
				selectDayOfMonth(dayOfMonth);

				Task<DateSelection> selectionTask = new Task<DateSelection>() {
					@Override
					protected DateSelection call() throws Exception {
						String entry = JournalManager.getEntry(now);
						updateProgress(3, 10);
						currentSelection = new DateSelection(null, now, null, entry, label);
						updateProgress(6, 10);
						DateSelection dateSelection = new SimpleObjectProperty<DateSelection>(currentSelection)
								.getValue();
						updateProgress(10, 10);
						return dateSelection;
					}

					@Override
					protected void succeeded() {
						currentSelection = getValue();
						System.err.println("calendarController.init, currentSelection=" + currentSelection);
						DateSelection newDateSelection = new DateSelection(null, now, null, currentSelection.newEntry(),
								label);
						selectedEntry = new SimpleObjectProperty<>(newDateSelection);
						onDayLabelClicked(label);
					}
				};
				progressBar.progressProperty().bind(selectionTask.progressProperty());
				var t = new Thread(selectionTask);
				t.setDaemon(true);
				t.start();

				break;
			}
		}
	}

	/**
	 * Respond to a mouse-click on an individual day.
	 *
	 * @param label {@link Label} the clicked day's label
	 */
	void onDayLabelClicked(Label label) {
		clearBorders();
		setBorder(label);
	}

	@FXML
	private void onNextMonthAction() {
		// if current is Dec. wrap to Jan.
		Month nextMonthEnum = currentYearMonth.getMonth() == Month.DECEMBER ? Month.JANUARY
				: Month.of(currentYearMonth.getMonthValue() + 1);
		YearMonth nextMonth = YearMonth.of(currentYearMonth.getYear(), nextMonthEnum);
		drawMonth(nextMonth);
	}

	@FXML
	private void onNextYearAction() {
		YearMonth nextYear = YearMonth.of(currentYearMonth.getYear() + 1, currentYearMonth.getMonth());
		drawMonth(nextYear);
	}

	@FXML
	private void onPreviousMonthAction() {
		// if current is Jan. wrap to Dec.
		Month previousMonthEnum = currentYearMonth.getMonth() == Month.JANUARY ? Month.DECEMBER
				: Month.of(currentYearMonth.getMonthValue() - 1);
		YearMonth previousMonth = YearMonth.of(currentYearMonth.getYear(), previousMonthEnum);
		drawMonth(previousMonth);
	}

	@FXML
	private void onPreviousYearAction() {
		YearMonth yesterYear = YearMonth.of(currentYearMonth.getYear() - 1, currentYearMonth.getMonth());
		drawMonth(yesterYear);
	}

	/**
	 * Select a day of the month programmatically.
	 *
	 * @param today int the day of the month to select
	 */
	public void selectDayOfMonth(int day) {
		Node node = dateGrid.getChildrenUnmodifiable().get(day);
		if (node instanceof Label label) {
			onDayLabelClicked(label);
		}
	}

	/**
	 * Set a horseshit brown border around the provided label.
	 *
	 * @param label {@link Label}
	 */
	private void setBorder(Label label) {
		label.setBorder(new Border(
				new BorderStroke(Color.BURLYWOOD, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3))));
	}
}
