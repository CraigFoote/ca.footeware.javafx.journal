package ca.footeware.javafx.journal.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import ca.footeware.javafx.journal.App;
import ca.footeware.javafx.journal.model.JournalManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

/**
 * MVC Controller for the "Open" page.
 */
public class NewPageController {

	@FXML
	private Button browseButton;

	@FXML
	private TextField nameField;

	@FXML
	private TextField passwordField1;

	@FXML
	private TextField passwordField2;

	@FXML
	private void onBrowseForJournalFolderAction() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(App.getPrimaryStage());
		if (selectedDirectory != null) {
			browseButton.setText(selectedDirectory.getAbsolutePath());
		}
	}

	@FXML
	private void onCreateJournalAction() {
		try {
			verifyInputs();
			JournalManager.createNewJournal(browseButton.getText(), nameField.getText().trim(),
					passwordField1.getText());
			App.setRoot("/editorPage");
		} catch (IllegalArgumentException | IOException | URISyntaxException e) {
			App.notify(e.getMessage());
		}
	}

	@FXML
	private void onSwitchToHomePageAction() throws IOException, URISyntaxException {
		App.setRoot("/homePage");
	}

	private void verifyInputs() {
		if (browseButton.getText().equals("Browse")) {
			throw new IllegalArgumentException("No location selected");
		}
		if (nameField.getText().trim().isBlank()) {
			throw new IllegalArgumentException("Name field is blank");
		}
		if (passwordField1.getText().isBlank() || passwordField2.getText().isBlank()) {
			throw new IllegalArgumentException("A password field is blank");
		}
		if (!passwordField1.getText().equals(passwordField2.getText())) {
			throw new IllegalArgumentException("Passwords do not match");
		}
	}
}