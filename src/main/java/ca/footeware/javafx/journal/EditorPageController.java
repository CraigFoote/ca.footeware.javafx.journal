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
	private GridPane dateGrid;
	private int lengthOfMonth;

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
		YearMonth ym = YearMonth.now();
		lengthOfMonth = ym.lengthOfMonth();

		dateGrid.getChildren().clear();

		// find the column index of the 1st day with Sunday=0
		LocalDate firstOfMonth = ym.atDay(1);
		int firstColumn = firstOfMonth.getDayOfWeek().getValue() % 7;

		// fill days (weeks run Sunday..Saturday), start at row 0
		for (int day = 1; day <= lengthOfMonth; day++) {
			int index = firstColumn + (day - 1);
			int row = index / 7;
			int col = index % 7;

			Label dayLabel = new Label(Integer.toString(day));
			GridPane.setHalignment(dayLabel, HPos.CENTER);
			dateGrid.add(dayLabel, col, row);
		}
	}

	@FXML
	public void onNextEntryAction() {
		App.sayHello();
	}

	@FXML
	public void onLastEntryAction() {
		App.sayHello();
	}
}