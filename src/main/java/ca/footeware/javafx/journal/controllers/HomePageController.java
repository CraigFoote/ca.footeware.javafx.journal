package ca.footeware.javafx.journal.controllers;

import java.io.IOException;
import java.net.URISyntaxException;

import ca.footeware.javafx.journal.App;
import javafx.fxml.FXML;

public class HomePageController {

	@FXML
	private void onSwitchToNewPageAction() throws IOException, URISyntaxException {
		App.setRoot("/newPage");
	}

	@FXML
	private void onSwitchToOpenPageAction() throws IOException, URISyntaxException {
		App.setRoot("/openPage");
	}
}
