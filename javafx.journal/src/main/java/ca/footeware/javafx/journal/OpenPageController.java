package ca.footeware.javafx.journal;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * MVC Controller for the "Open" page.
 */
public class OpenPageController {

	@FXML
	private Button backButton;
	
	@FXML
	private Button browseButton;
	
	@FXML
	private Button openButton;

	@FXML
	private void switchToHomePage() throws IOException {
		App.setRoot("homePage");
	}

	@FXML
	private void browseForJournal() throws IOException {
		System.out.println("Browse button clicked");
	}
	
	@FXML
	private void openJournal() throws IOException {
		System.out.println("Open button clicked");
	}
}