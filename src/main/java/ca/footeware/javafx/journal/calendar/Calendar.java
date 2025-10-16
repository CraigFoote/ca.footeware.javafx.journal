package ca.footeware.javafx.journal.calendar;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 
 */
public class Calendar extends VBox {
	
	public Calendar() {
		super();
		createUI();
	}

	private void createUI() {
		HBox yearMonthBox = new HBox();
		this.getChildren().add(yearMonthBox);
		HBox dayNamesBox = new HBox();
		this.getChildren().add(dayNamesBox);
		GridPane dateGrid = new GridPane();
		this.getChildren().add(dateGrid);
	}
}
