package ca.footeware.javafx.journal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.controlsfx.control.NotificationPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * The Journal application.
 */
public class App extends Application {

	/**
	 * Formatter for dates to provide a {@link String} in format yyyy-MM-dd.
	 */
	public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static FXMLLoader loader;
	private static NotificationPane notificationPane;
	private static Scene scene;
	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	/**
	 * Return the window.
	 *
	 * @return {@link Window}
	 */
	public static Window getPrimaryStage() {
		return scene.getWindow();
	}

	/**
	 * Load the provided FXML file into the scene graph.
	 *
	 * @param fxml {@link String} the name of the .fxml file without the extension.
	 * @return {@link Parent} the root of the FXML file
	 * @throws IOException if worlds collide
	 */
	public static Parent loadFXML(String fxml) throws IOException {
		URL resource = App.class.getResource(fxml + ".fxml");
		loader = new FXMLLoader(resource);
		return loader.load();
	}

	/**
	 * The method to call to launch the application.
	 *
	 * @param args {@link String}[]
	 */
	public static void main(String[] args) {
		launch();
	}

	/**
	 * Presents an in-window notification panel at the bottom of the application
	 * window, containing the provided message.
	 *
	 * @param message {@link String}
	 */
	public static void notify(String message) {
		notificationPane.setText(message);
		notificationPane.show();

		Runnable closer = (() -> Platform.runLater(() -> notificationPane.hide()));
		scheduler.schedule(closer, 5, TimeUnit.SECONDS);
	}

	/**
	 * Loads the provided FXML file and passes its parent root to the notification
	 * pane to be set as child widget.
	 *
	 * @param fxml the name of the .fxml file without the extension.
	 * @throws IOException if some other worlds collide
	 */
	public static void setRoot(String fxml) throws IOException {
		loadFXML(fxml);
		Parent page = loader.getRoot();
		notificationPane.setContent(page);
	}

	@Override
	public void start(Stage stage) throws IOException, URISyntaxException {
		Parent root = loadFXML("/homePage");
		notificationPane = new NotificationPane(root);
		notificationPane.setShowFromTop(false);
		notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
		scene = new Scene(notificationPane, 620, 700);
		URL resource = App.class.getResource("/styles.css");
		scene.getStylesheets().add(resource.toExternalForm());
		Image icon = new Image(getClass().getResourceAsStream("/journal-goldkey.png"));
		stage.getIcons().add(icon);
		stage.setTitle("Journal");
		stage.setScene(scene);
		stage.show();
	}
}