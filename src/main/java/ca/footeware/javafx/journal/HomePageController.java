package ca.footeware.javafx.journal;

import java.io.IOException;
import java.net.URISyntaxException;

import javafx.fxml.FXML;

public class HomePageController {

	@FXML
	private void onSwitchToNewPageAction() throws IOException, URISyntaxException {
		App.setRoot("newPage");
	}

	@FXML
	private void onSwitchToOpenPageAction() throws IOException, URISyntaxException {
		App.setRoot("openPage");
	}
}
