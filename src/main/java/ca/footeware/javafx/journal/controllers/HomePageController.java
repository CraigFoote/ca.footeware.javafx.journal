package ca.footeware.javafx.journal.controllers;

import java.io.IOException;

import ca.footeware.javafx.journal.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class HomePageController {

	@FXML
	private void onAboutAction() {
		Alert alert = new Alert(AlertType.NONE);
		Image image = new Image(getClass().getResource("/programmer.jpg").toExternalForm());
		ImageView imageView = new ImageView(image);
		alert.setGraphic(imageView);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(App.getPrimaryStage());
		alert.setContentText("Another fine mess by Footeware.ca");
		alert.setTitle("About Journal");
		alert.initStyle(StageStyle.UNDECORATED);
		alert.setHeaderText(null); // hide
		alert.getButtonTypes().add(ButtonType.CLOSE);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		alert.show();
	}

	@FXML
	private void onSwitchToNewPageAction() throws IOException {
		App.setRoot("/newPage");
	}

	@FXML
	private void onSwitchToOpenPageAction() throws IOException {
		App.setRoot("/openPage");
	}
}
