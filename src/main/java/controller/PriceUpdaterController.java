package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PriceUpdaterController implements Initializable {

    public Button fileChooserBtn;

    public TextArea fileChooserTxtArea;

    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void fileChooser(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
        for (File file : files) {
            fileChooserTxtArea.appendText(file.getName() + "\n");
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

}
