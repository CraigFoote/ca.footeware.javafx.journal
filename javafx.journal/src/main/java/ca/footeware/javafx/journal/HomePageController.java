package ca.footeware.javafx.journal;

import java.io.IOException;
import javafx.fxml.FXML;

public class HomePageController {

    @FXML
    private void switchToOpenPage() throws IOException {
        App.setRoot("openPage");
    }
    
    @FXML
    private void switchToNewPage() throws IOException {
        App.setRoot("newPage");
    }
}
