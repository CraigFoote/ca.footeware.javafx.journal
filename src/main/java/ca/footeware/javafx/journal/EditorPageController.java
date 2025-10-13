package ca.footeware.javafx.journal;

import java.time.LocalDate;
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

	@FXML
	private void initialize() {
		YearMonth thisMonth = YearMonth.now();
		drawMonth(thisMonth);

		Platform.runLater(() -> {
			textArea.requestFocus();
		});
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

			final int finalDay = day;
			dayLabel.setOnMouseClicked(e -> onDayLabelClicked(finalDay));
			dayLabel.setCursor(javafx.scene.Cursor.HAND);

			dateGrid.add(dayLabel, col, row);
		}

		int today = LocalDate.now().getDayOfMonth();
		for (Node node : dateGrid.getChildren()) {
			if (node instanceof Label label) {
				if (label.getText().equals(String.valueOf(today))) {
					Font current = label.getFont();
					label.setFont(Font.font(current.getFamily(), FontWeight.EXTRA_BOLD, current.getSize() + 1.0));
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