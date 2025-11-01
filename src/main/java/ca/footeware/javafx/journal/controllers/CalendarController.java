package ca.footeware.javafx.journal.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;

import ca.footeware.javafx.journal.App;
import ca.footeware.javafx.journal.model.DateSelection;
import ca.footeware.javafx.journal.model.JournalManager;
import ca.footeware.javafx.journal.model.SelectionEvent;
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
 * Controls the calendar component.
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
				Node node = dateGrid.getChildren().get(dayNum); // get(idx) is 0-based
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
				label.setFont(Font.font(label.getFont().getFamily(), FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14.0));
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
			dayLabel.setFont(Font.font(dayLabel.getFont().getFamily(), FontWeight.NORMAL, FontPosture.REGULAR, 14.0));
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
	}

	/**
	 * Creates and fires a selection event.
	 * 
	 * @param label {@link Label} the originating control
	 */
	private void fireSelectionEvent(Label label) {
		// last event - may be used to save previous selection with text in textArea
		LocalDate oldSelectedDate = null;
		// currentSelection has the last selection which put the sought date in newDate
		if (currentSelection != null && currentSelection.newDate() != null) {
			// what was new is now old
			oldSelectedDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(),
					currentSelection.newDate().getDayOfMonth());
		}

		// new event
		LocalDate newSelectedDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(),
				Integer.parseInt(label.getText()));

		// selection object
		currentSelection = new DateSelection(oldSelectedDate, newSelectedDate);

		// fire event
		SelectionEvent selectionEvent = new SelectionEvent(SelectionEvent.DATE_SELECTED, currentSelection);
		System.err.println("\nCalendarController firing event, newDate=" + currentSelection.newDate());
		label.fireEvent(selectionEvent);
		setBorder(label);
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

	@FXML
	private void initialize() {
		createDateGrid();
		colorizeEntryDays();
		colorizeToday();

		yearLabel.setText(String.valueOf(currentYearMonth.getYear()));
		monthLabel.setText(currentYearMonth.getMonth().toString());
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
		Node node = dateGrid.getChildrenUnmodifiable().get(day - 1);
		if (node instanceof Label label) {
			fireSelectionEvent(label);
		}
	}

	/**
	 * Set a horseshit brown border around the provided label.
	 *
	 * @param label {@link Label}
	 */
	private void setBorder(Label label) {
		clearBorders();
		label.setBorder(new Border(
				new BorderStroke(Color.BURLYWOOD, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3))));
	}
}
