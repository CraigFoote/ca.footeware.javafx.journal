package ca.footeware.javafx.journal.controllers;

import java.io.File;
import java.io.IOException;

import ca.footeware.javafx.journal.App;
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
public class NewPageController {

	private static final String REVEAL = "reveal";

	@FXML
	private Button browseButton;

	@FXML
	private ImageView key;

	private String password1;

	private String password2;

	@FXML
	private PasswordField passwordField1;
	@FXML
	private PasswordField passwordField2;

	@FXML
	private void onBrowseForJournalAction() {
		FileChooser fileChooser = new FileChooser();
		File selectedFile = fileChooser.showSaveDialog(App.getPrimaryStage());
		if (selectedFile != null) {
			browseButton.setText(selectedFile.getAbsolutePath());
			passwordField1.requestFocus();
		}
	}

	@FXML
	private void onCreateJournalAction() {
		try {
			verifyInputs();
			JournalManager.createNewJournal(browseButton.getText(), passwordField1.getText());
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
		} catch (IllegalArgumentException | IOException e) {
			App.notify(e.getMessage());
		}
	}

	@FXML
	private void onShowPassword1Pressed() {
		passwordField1.getStyleClass().add(REVEAL);
		password1 = passwordField1.getText();
		passwordField1.setPromptText(password1);
		passwordField1.setText(null);
		passwordField1.getParent().requestFocus();
	}

	@FXML
	private void onShowPassword1Released() {
		passwordField1.setText(password1);
		passwordField1.setPromptText(null);
		passwordField1.getStyleClass().remove(REVEAL);
	}

	@FXML
	private void onShowPassword2Pressed() {
		passwordField2.getStyleClass().add(REVEAL);
		password2 = passwordField2.getText();
		passwordField2.setPromptText(password2);
		passwordField2.setText(null);
		passwordField2.getParent().requestFocus();
	}

	@FXML
	private void onShowPassword2Released() {
		passwordField2.setText(password2);
		passwordField2.setPromptText(null);
		passwordField2.getStyleClass().remove(REVEAL);
	}

	@FXML
	private void onSwitchToHomePageAction() throws IOException {
		App.setRoot("/homePage");
	}

	private void verifyInputs() {
		if (browseButton.getText().equals("Browse")) {
			throw new IllegalArgumentException("No location selected");
		}
		if (passwordField1.getText().isBlank() || passwordField2.getText().isBlank()) {
			throw new IllegalArgumentException("A password field is blank");
		}
		if (!passwordField1.getText().equals(passwordField2.getText())) {
			throw new IllegalArgumentException("Passwords do not match");
		}
	}
}