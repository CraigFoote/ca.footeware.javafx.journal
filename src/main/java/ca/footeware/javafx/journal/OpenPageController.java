package ca.footeware.javafx.journal;

import java.io.IOException;

import javafx.fxml.FXML;

/**
 * MVC Controller for the "Open" page.
 */
public class OpenPageController {

	@FXML
	private void onSwitchToHomePageAction() throws IOException {
		App.setRoot("homePage");
	}

	@FXML
	private void onBrowseForJournalAction() throws IOException {
		System.out.println("Browse button clicked");
	}

	@FXML
	private void onOpenJournalAction() throws IOException {
		System.out.println("Open button clicked");
	}
}