package ca.footeware.javafx.journal.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import ca.footeware.javafx.journal.App;
import ca.footeware.javafx.journal.exceptions.JournalException;
import ca.footeware.javafx.journal.model.JournalManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

/**
 * MVC Controller for the "Open" page.
 */
public class OpenPageController {

	@FXML
	private Button browseButton;

	@FXML
	private TextField passwordField;

	@FXML
	private void onBrowseForJournalAction() {
		FileChooser fileChooser = new FileChooser();
		File theChosenOne = fileChooser.showOpenDialog(App.getPrimaryStage());
		if (theChosenOne != null) {
			browseButton.setText(theChosenOne.getAbsolutePath());
		}
	}

	@FXML
	private void onOpenJournalAction() {
		try {
			verifyInputs();
			JournalManager.openJournal(browseButton.getText(), passwordField.getText());
			App.setRoot("editorPage");
		} catch (IOException | JournalException | URISyntaxException e) {
			App.notify(e.getMessage());
		}
	}

	@FXML
	private void onSwitchToHomePageAction() throws IOException, URISyntaxException {
		App.setRoot("homePage");
	}

	private void verifyInputs() {
		if (browseButton.getText().equals("Browse")) {
			throw new IllegalArgumentException("No location selected");
		}
		if (passwordField.getText().isBlank()) {
			throw new IllegalArgumentException("The password field is blank");
		}
	}
}