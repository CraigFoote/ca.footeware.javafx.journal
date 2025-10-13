package ca.footeware.javafx.journal;

import java.time.LocalDate;
import java.time.YearMonth;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

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
	public void onPreviousYearAction() {
		App.sayHello();
	}

	@FXML
	public void onNextYearAction() {
		App.sayHello();
	}

	@FXML
	public void onPreviousMonthAction() {
		App.sayHello();
	}

	@FXML
	public void onNextMonthAction() {
		App.sayHello();
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
		App.sayHello();
	}

	@FXML
	public void onNextEntryAction() {
		App.sayHello();
	}

	@FXML
	public void onLastEntryAction() {
		App.sayHello();
	}

	private void drawMonth(YearMonth ym) {
		yearLabel.setText(String.valueOf(ym.getYear()));
		monthLabel.setText(ym.getMonth().toString());

		dateGrid.getChildren().clear();

		int lengthOfMonth = ym.lengthOfMonth();

		// find the column index of the 1st day with Sunday=0
		LocalDate firstOfMonth = ym.atDay(1);
		int firstColumn = firstOfMonth.getDayOfWeek().getValue() % 7;

		for (int day = 1; day <= lengthOfMonth; day++) {
			int index = firstColumn + (day - 1);
			int row = index / 7;
			int col = index % 7;

			Label dayLabel = new Label(Integer.toString(day));
			GridPane.setHalignment(dayLabel, HPos.CENTER);
			dateGrid.add(dayLabel, col, row);
		}
	}

	public void init() {
		drawMonth(YearMonth.now());
	}
}