package ca.footeware.javafx.journal.controllers;

import java.io.File;
import java.io.IOException;

import ca.footeware.javafx.journal.App;
import ca.footeware.javafx.journal.exceptions.JournalException;
import ca.footeware.javafx.journal.model.JournalManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * MVC Controller for the "Open" page.
 */
public class OpenPageController {

	@FXML
	private Button browseButton;

	@FXML
	private ImageView key;

	private String password;

	@FXML
	private PasswordField passwordField;

	@FXML
	private void onBrowseForJournalAction() {
		FileChooser fileChooser = new FileChooser();
		File theChosenOne = fileChooser.showOpenDialog(App.getPrimaryStage());
		if (theChosenOne != null) {
			browseButton.setText(theChosenOne.getAbsolutePath());
			passwordField.requestFocus();
		}
	}

	@FXML
	private void onOpenJournalAction() {
		try {
			verifyInputs();
			JournalManager.openJournal(browseButton.getText(), passwordField.getText());
			FadeTransition transition = new FadeTransition(Duration.seconds(2), key);
			transition.setFromValue(1.0);
			transition.setToValue(0.0);
			transition.onFinishedProperty().set(_ -> {
				try {
					App.setRoot("/editorPage");
				} catch (IOException e) {
					App.notify(e.getMessage());
				}
			});
			transition.play();
		} catch (IOException | JournalException | IllegalArgumentException e) {
			App.notify(e.getMessage());
		}
	}

	@FXML
	private void onShowPasswordPressed() {
		passwordField.getStyleClass().add("reveal");
		password = passwordField.getText();
		passwordField.setPromptText(password);
		passwordField.setText(null);
		passwordField.getParent().requestFocus();
	}

	@FXML
	private void onShowPasswordReleased() {
		passwordField.setText(password);
		passwordField.setPromptText(null);
		passwordField.getStyleClass().remove("reveal");
	}

	@FXML
	private void onSwitchToHomePageAction() throws IOException {
		App.setRoot("/homePage");
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