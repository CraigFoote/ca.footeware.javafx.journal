package ca.footeware.javafx.journal;

import java.io.IOException;
import javafx.fxml.FXML;

public class HomePageController {

    @FXML
    private void onSwitchToOpenPageAction() throws IOException {
        App.setRoot("openPage");
    }
    
    @FXML
    private void onSitchToNewPageAction() throws IOException {
        App.setRoot("newPage");
    }
}
