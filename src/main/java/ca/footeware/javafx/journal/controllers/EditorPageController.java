package ca.footeware.javafx.journal.controllers;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import ca.footeware.javafx.journal.App;
import ca.footeware.javafx.journal.exceptions.JournalException;
import ca.footeware.javafx.journal.model.JournalManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * MVC Controller for the "Editor" page.
 */
public class EditorPageController {

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyy-MM-dd");

	private int currentDayOfMonth;

	private Label currentSelection;

	private YearMonth currentYearMonth;

	@FXML
	private GridPane dateGrid;

	@FXML
	private Label monthLabel;

	@FXML
	private TextArea textArea;

	@FXML
	private Label yearLabel;

	/**
	 * Set a border around the provided label if it represents the currently
	 * selected day.
	 * 
	 * @param label {@link Label}
	 * @return boolean true if label now has a border
	 */
	private boolean borderizeCurrentSelection(Label label) {
		if (currentSelection != null && label.getText().equals(currentSelection.getText())) {
			label.setBorder(
					new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));
			return true;
		}
		return false;
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
	 * Color label background blue if there's an entry for that day.
	 * 
	 * @param label {@link Label}
	 */
	private void colorizeDayWithEntry(Label label) {
		int day = Integer.parseInt(label.getText());
		LocalDate date = currentYearMonth.atDay(day);
		String formatted = date.format(dateFormatter);
		try {
			if (JournalManager.getEntry(formatted) != null) {
				label.setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
			}
		} catch (JournalException e) {
			App.notify(e.getMessage());
		}
	}

	/**
	 * Colorize today's date in the calendar. Add border to selected day.
	 * 
	 * @param label      {@link Label}
	 * @param todayIndex int
	 * @return boolean true if today was found and colorized
	 */
	private boolean colorizeToday(Label label, int todayIndex) {
		if (label.getText().equals(String.valueOf(todayIndex))) {
			Font currentFont = label.getFont();
			label.setFont(Font.font(currentFont.getFamily(), FontWeight.NORMAL, FontPosture.REGULAR, currentFont.getSize()));
			label.setTextFill(Color.RED);
			if (currentSelection != null && currentSelection.getText().equals(String.valueOf(todayIndex))) {
				label.setBorder(
						new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));
			}
			return true;
		}
		return false;
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

			dayLabel.setOnMouseClicked(_ -> onDayLabelClicked(dayLabel));
			dayLabel.setCursor(javafx.scene.Cursor.HAND);

			dateGrid.add(dayLabel, col, row);
		}
	}

	/**
	 * Draw the calendar to show the provided {@link YearMonth}.
	 * 
	 * @param ym {@link YearMonth}
	 */
	private void drawMonth(YearMonth ym) {
		currentYearMonth = ym;
		yearLabel.setText(String.valueOf(currentYearMonth.getYear()));
		monthLabel.setText(currentYearMonth.getMonth().toString());

		createDateGrid();
		clearBorders();
		paintshop();
	}

	/**
	 * Called after injection of widgets.
	 */
	@FXML
	private void initialize() {
		currentDayOfMonth = LocalDate.now().getDayOfMonth() - 1;
		drawMonth(YearMonth.now());
		// select today
		Node node = dateGrid.getChildrenUnmodifiable().get(currentDayOfMonth);
		if (node instanceof Label label) {
			onDayLabelClicked(label);
		}
		Platform.runLater(() -> textArea.requestFocus());
	}

	/**
	 * Respond to a mouse-click on an individual day.
	 * 
	 * @param label {@link Label} the clicked day's label
	 */
	private void onDayLabelClicked(Label label) {
		currentSelection = label;
		clearBorders();
		paintshop();
		label.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null, new BorderWidths(3))));
		currentDayOfMonth = Integer.valueOf(label.getText());
		LocalDate date = currentYearMonth.atDay(currentDayOfMonth);
		String formatted = date.format(dateFormatter);
		try {
			String entry = JournalManager.getEntry(formatted);
			textArea.setText(entry);
			textArea.requestFocus();
			String text = textArea.getText();
			textArea.positionCaret(text != null ? text.length() : 0);
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
	private void onPreviousEntryAction() {
		App.sayHello();
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

	@FXML
	private void onSaveAction() {
		try {
			LocalDate date = currentYearMonth.atDay(currentDayOfMonth);
			String formatted = date.format(dateFormatter);
			JournalManager.addEntry(formatted, textArea.getText());
			JournalManager.saveJournal();
			paintshop();
			App.notify("Journal was saved.");
		} catch (JournalException e) {
			App.notify(e.getMessage());
		}
	}

	@FXML
	private void onTodayAction() {
		drawMonth(YearMonth.now());
		currentDayOfMonth = LocalDate.now().getDayOfMonth();
		Node node = dateGrid.getChildrenUnmodifiable().get(currentDayOfMonth - 1);
		if (node instanceof Label label) {
			onDayLabelClicked(label);
		}
	}

	/**
	 * Set background color of days with entries and, if {@link #currentYearMonth}
	 * is in current year and month, highlight today.
	 */
	private void paintshop() {
		LocalDate now = LocalDate.now();
		boolean todayFound = false;
		boolean selectionBorderized = false;
		clearBorders();
		if (now.getYear() == currentYearMonth.getYear() && now.getMonthValue() == currentYearMonth.getMonthValue()) {
			for (Node node : dateGrid.getChildren()) {
				if (node instanceof Label label) {
					// blue background for days with entries
					colorizeDayWithEntry(label);
					// find and colorize today's label font red
					if (!todayFound) {
						todayFound = colorizeToday(label, now.getDayOfMonth());
					}
					// set a border on currently selected day
					if (!selectionBorderized) {
						selectionBorderized = borderizeCurrentSelection(label);
					}
				}
			}
		}
	}
}