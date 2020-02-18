package ru.alcotester.pricehandler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.collections.CollectionUtils;
import ru.alcotester.pricehandler.service.MainReader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PriceUpdaterController implements Initializable {

    public Button berivdoroguBtn;
    public TextField berivdoroguTxt;
    public Label statusLbl;
    public Button atuningBtn;
    public TextField atuningTxt;
    public Button eurodetalBtn;
    public TextField eurodetalTxt;
    public Button esautoBtn;
    public TextArea esAutoTxtArea;
    public Button goBtn;
    public Button emailBtn;

    private Stage primaryStage;
    private String bdPricePath;
    private String atPricePath;
    private String edPricePath;
    private List<String> esaPricePath = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void fileChooser(ActionEvent event) throws IOException {
        final FileChooser fileChooser = new FileChooser();
        String status = "";
        statusLbl.setText(status);
        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();
            if (btn.getId().equals("berivdoroguBtn")) {
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    bdPricePath = file.getPath();
                    berivdoroguTxt.setText(bdPricePath);
                }
            }
            if (btn.getId().equals("atuningBtn")) {
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    atPricePath = file.getPath();
                    atuningTxt.setText(atPricePath);
                }
            }
            if (btn.getId().equals("eurodetalBtn")) {
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    edPricePath = file.getPath();
                    eurodetalTxt.setText(edPricePath);
                }
            }
            if (btn.getId().equals("esautoBtn")) {
                List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
                if (CollectionUtils.isNotEmpty(files)) {
                    for (File file : files) {
                        esaPricePath.add(file.getPath());
                        esAutoTxtArea.appendText(file.getPath() + "\n");
                    }
                }
            }
            if (btn.getId().equals("goBtn")) {
                MainReader reader = new MainReader(primaryStage);
                try {
                    reader.read(bdPricePath, atPricePath, edPricePath, esaPricePath);
                    status = "Read success!";
                } catch (IOException e) {
                    status = "Возникли проблемы при чтении/записи файлов";
                    System.err.println(status);
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    status = e.getMessage();
                    e.printStackTrace();
                } finally {
                    statusLbl.setText(status);
                }
            }
            if (btn.getId().equals("emailBtn")) {
                EmailWindowController emailWindowController = new EmailWindowController();
                emailWindowController.setPrimaryStage(primaryStage);
                emailWindowController.emailWindow();
            }
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
