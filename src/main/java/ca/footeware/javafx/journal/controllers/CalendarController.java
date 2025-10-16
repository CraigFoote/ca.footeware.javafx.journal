package ca.footeware.javafx.journal.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ca.footeware.javafx.journal.App;
import ca.footeware.javafx.journal.exceptions.JournalException;
import ca.footeware.javafx.journal.model.JournalManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
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

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
	private Label currentSelection;
	private YearMonth currentYearMonth;
	public static StringProperty selectedEntry;

	@FXML
	private Label yearLabel;

	@FXML
	private Label monthLabel;

	@FXML
	private GridPane dateGrid;

	public CalendarController() {
		super();
		currentYearMonth = YearMonth.now();
		setSpacing(10);
	}

	@FXML
	private void initialize() {
		createDateGrid();
		colorizeToday();
		colorizeEntryDays();

		// select today by default
		LocalDate now = LocalDate.now();
		for (Node node : dateGrid.getChildren()) {
			if (node instanceof Label label && label.getText().equals(String.valueOf(now.getDayOfMonth()))) {
				setBorder(label);
				currentSelection = label;
				selectedEntry = new SimpleStringProperty(label.getText());
				break;
			}
		}

	}

	private void setBorder(Label label) {
		label.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));
	}

	private void colorizeEntryDays() {
		LocalDate now = LocalDate.now();
		List<String> entryDates = JournalManager.getEntryDates();
		for (String entryDate : entryDates) {
			if (now.getYear() == currentYearMonth.getYear() && now.getMonth() == currentYearMonth.getMonth()) {
				String dayNum = entryDate.substring(entryDate.lastIndexOf("-") + 1);
				for (Node node : dateGrid.getChildren()) {
					if (node instanceof Label label && label.getText().equals(dayNum)) {
						label.setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
						break;
					}
				}
			}
		}
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
	 * Colorize today's date in the calendar.
	 */
	private void colorizeToday() {
		int todayIndex = LocalDateTime.now().getDayOfMonth();
		for (Node node : dateGrid.getChildren()) {
			if (node instanceof Label label && label.getText().equals(String.valueOf(todayIndex))) {
				Font currentFont = label.getFont();
				label.setFont(Font.font(currentFont.getFamily(), FontWeight.NORMAL, FontPosture.REGULAR,
						currentFont.getSize()));
				label.setTextFill(Color.RED);
				return;
			}
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
	}

	/**
	 * Respond to a mouse-click on an individual day.
	 * 
	 * @param label {@link Label} the clicked day's label
	 */
	private void onDayLabelClicked(Label label) {
		currentSelection = label;
		clearBorders();
		setBorder(label);
		LocalDate selectedDate = currentYearMonth.atDay(Integer.parseInt(currentSelection.getText()));
		String formatted = selectedDate.format(dateFormatter);
		try {
			String entry = JournalManager.getEntry(formatted);
			selectedEntry.setValue(entry != null ? entry : "");
		} catch (JournalException e) {
			App.notify(e.getMessage());
		}
	}
}
