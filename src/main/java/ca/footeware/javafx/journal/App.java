package ca.footeware.javafx.journal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.controlsfx.control.NotificationPane;

import atlantafx.base.theme.NordDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * JavaFX App
 */
public class App extends Application {

	private static FXMLLoader loader;
	private static NotificationPane notificationPane;
	private static Scene scene;

	public static Window getPrimaryStage() {
		return scene.getWindow();
	}

	private static Parent loadFXML(String fxml) throws IOException, URISyntaxException {
		URL resource = App.class.getResource(fxml + ".fxml");
		loader = new FXMLLoader(resource);
		return loader.load(new FileInputStream(new File(new URI(resource.toString()))));
	}

	public static void main(String[] args) {
		launch();
	}

	public static void notify(String message) {
		notificationPane.setText(message);
		notificationPane.show();
	}

	public static void sayHello() {
		System.err.println("Hello!");
	}

	public static void setRoot(String fxml) throws IOException, URISyntaxException {
		loadFXML(fxml);
		Parent page = loader.getRoot();
		notificationPane.setContent(page);
	}

	@Override
	public void start(Stage stage) throws IOException, URISyntaxException {
		Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
		Parent root = loadFXML("homePage");
		notificationPane = new NotificationPane(root);
		notificationPane.setShowFromTop(false);
		notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
		scene = new Scene(notificationPane, 550, 700);
		Image icon = new Image(getClass().getResourceAsStream("/journal.png"));
		stage.getIcons().add(icon);
		stage.setTitle("Journal");
		stage.setScene(scene);
		stage.show();
	}
}