package ca.footeware.javafx.journal;

import java.io.IOException;

import org.controlsfx.control.NotificationPane;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * JavaFX App
 */
public class App extends Application {

	private static Scene scene;
	private static FXMLLoader loader;
	private static NotificationPane notificationPane;

	@Override
	public void start(Stage stage) throws IOException {
		Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
		Parent root = loadFXML("editorPage");
		notificationPane = new NotificationPane(root);
		notificationPane.setShowFromTop(false);
		notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
		setRoot("editorPage");
		scene = new Scene(notificationPane, 550, 700);
		stage.setScene(scene);
		stage.show();
	}

	static void setRoot(String fxml) throws IOException {
		Parent page = loader.getRoot();
		Object controller = loader.getController();
		if (controller instanceof EditorPageController editorController) {
			editorController.init();
		}
		notificationPane.setContent(page);
	}

	private static Parent loadFXML(String fxml) throws IOException {
		loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return loader.load();
	}

	public static NotificationPane getNotificationPane() {
		return notificationPane;
	}

	public static Window getPrimaryStage() {
		return scene.getWindow();
	}

	public static void main(String[] args) {
		launch();
	}

	public static void sayHello() {
		System.err.println("Hello!");
	}
}