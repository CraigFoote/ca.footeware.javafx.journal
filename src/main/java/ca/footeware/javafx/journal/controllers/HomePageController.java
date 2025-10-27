package ca.footeware.javafx.journal.controllers;

import java.io.IOException;

import ca.footeware.javafx.journal.App;
import javafx.fxml.FXML;

public class HomePageController {

	@FXML
	private void onSwitchToNewPageAction() throws IOException {
		App.setRoot("/newPage");
	}

	@FXML
	private void onSwitchToOpenPageAction() throws IOException {
		App.setRoot("/openPage");
	}
}
