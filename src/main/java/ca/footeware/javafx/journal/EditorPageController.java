package ca.footeware.javafx.journal;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * MVC Controller for the "Editor" page.
 */
public class EditorPageController {

	@FXML
	private Label yearLabel;

	@FXML
	private Label monthLabel;

	@FXML
	private GridPane dateGrid;

	@FXML
	private TextArea textArea;

	private YearMonth currentYearMonth;

	@FXML
	public void onPreviousYearAction() {
		YearMonth yesterYear = YearMonth.of(currentYearMonth.getYear() - 1, currentYearMonth.getMonth());
		drawMonth(yesterYear);
	}

	@FXML
	public void onNextYearAction() {
		YearMonth nextYear = YearMonth.of(currentYearMonth.getYear() + 1, currentYearMonth.getMonth());
		drawMonth(nextYear);
	}

	@FXML
	public void onPreviousMonthAction() {
		// if current is Jan. wrap to Dec.
		Month previousMonthEnum = currentYearMonth.getMonth() == Month.JANUARY ? Month.DECEMBER
				: Month.of(currentYearMonth.getMonthValue() - 1);

		YearMonth previousMonth = YearMonth.of(currentYearMonth.getYear(), previousMonthEnum);
		drawMonth(previousMonth);
	}

	@FXML
	public void onNextMonthAction() {
		// if current is Dec. wrap to Jan.
		Month nextMonthEnum = currentYearMonth.getMonth() == Month.DECEMBER ? Month.JANUARY
				: Month.of(currentYearMonth.getMonthValue() + 1);

		YearMonth nextMonth = YearMonth.of(currentYearMonth.getYear(), nextMonthEnum);
		drawMonth(nextMonth);
	}

	@FXML
	public void onFirstEntryAction() {
		App.sayHello();
	}

	@FXML
	public void onPreviousEntryAction() {
		App.sayHello();
	}

	@FXML
	public void onTodayAction() {
		drawMonth(YearMonth.now());
	}

	@FXML
	public void onNextEntryAction() {
		App.sayHello();
	}

	@FXML
	public void onLastEntryAction() {
		App.sayHello();
	}

	@FXML
	private void initialize() {
		drawMonth(YearMonth.now());
		Platform.runLater(() -> textArea.requestFocus());
	}

	private void drawMonth(YearMonth ym) {
		currentYearMonth = ym;
		yearLabel.setText(String.valueOf(currentYearMonth.getYear()));
		monthLabel.setText(currentYearMonth.getMonth().toString());

		dateGrid.getChildren().clear();

		int lengthOfMonth = currentYearMonth.lengthOfMonth();

		// find the column index of the 1st day with Sunday=0
		LocalDate firstOfMonth = currentYearMonth.atDay(1);
		int firstColumn = firstOfMonth.getDayOfWeek().getValue() % 7;

		for (int day = 1; day <= lengthOfMonth; day++) {
			int index = firstColumn + (day - 1);
			int row = index / 7;
			int col = index % 7;

			Label dayLabel = new Label(Integer.toString(day));
			GridPane.setHalignment(dayLabel, HPos.CENTER);

			final int finalDay = day;
			dayLabel.setOnMouseClicked(_ -> onDayLabelClicked(finalDay));
			dayLabel.setCursor(javafx.scene.Cursor.HAND);

			dateGrid.add(dayLabel, col, row);
		}

		// if #currentYearMonth is in current year and month, highlight today
		LocalDate now = LocalDate.now();
		if (now.getYear() == currentYearMonth.getYear() && now.getMonthValue() == currentYearMonth.getMonthValue()) {
			int todayIndex = now.getDayOfMonth(); // 1-based
			for (Node node : dateGrid.getChildren()) {
				if (node instanceof Label label && label.getText().equals(String.valueOf(todayIndex))) {
					Font currentFont = label.getFont();
					label.setFont(
							Font.font(currentFont.getFamily(), FontWeight.EXTRA_BOLD, currentFont.getSize() + 1.0));
					label.setTextFill(Color.RED);
					break;
				}
			}
		}
	}

	private void onDayLabelClicked(int day) {
		System.out.println("Clicked day: " + day);
		textArea.setText("Entry for " + day);
		Platform.runLater(() -> {
			textArea.requestFocus();
			textArea.positionCaret(textArea.getText().length());
		});
	}
}