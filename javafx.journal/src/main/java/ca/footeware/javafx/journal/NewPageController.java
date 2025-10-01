package ca.footeware.javafx.journal;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

/**
 * MVC Controller for the "Open" page.
 */
public class NewPageController {

	@FXML
	private Button backButton;

	@FXML
	private Button browseButton;

	@FXML
	private Button createButton;

	@FXML
	private TextField nameField;

	@FXML
	private TextField passwordField1;

	@FXML
	private TextField passwordField2;

	@FXML
	private HBox notificationBox;

	@FXML
	private void switchToHomePage() throws IOException {
		App.setRoot("homePage");
	}

	@FXML
	private void browseForJournalFolder() throws IOException {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(App.getPrimaryStage());
		if (selectedDirectory != null) {
			browseButton.setText(selectedDirectory.getAbsolutePath());
		}
	}

	@FXML
	private void createJournal() {
		try {
			verifyInputs();
			JournalManager.createNewJournal(browseButton.getText(), nameField.getText().trim(), passwordField1.getText());
		} catch (IllegalArgumentException | IOException e) {
			App.getNotificationPane().setText(e.getMessage());
			App.getNotificationPane().show();
		}
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