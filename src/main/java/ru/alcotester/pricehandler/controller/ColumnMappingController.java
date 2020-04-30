package ru.alcotester.pricehandler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ColumnMappingController implements Initializable {

    private Stage dialog;

    public TextField skuFld;
    public TextField nameFld;
    public TextField retailPrcFld;
    public TextField tradePrcFld;
    public TextField unitFld;
    public TextField availabilityFld;
    public Button okBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        skuFld.setOnKeyReleased(event -> {
            if (!skuFld.getText().matches("^[0-9]*$")) {
                okBtn.setDisable(true);
            } else {
                okBtn.setDisable(false);
            }
        });
        nameFld.setOnKeyReleased(event -> {
            if (!nameFld.getText().matches("^[0-9]*$")) {
                okBtn.setDisable(true);
            } else {
                okBtn.setDisable(false);
            }
        });
        retailPrcFld.setOnKeyReleased(event -> {
            if (!retailPrcFld.getText().matches("^[0-9]*$")) {
                okBtn.setDisable(true);
            } else {
                okBtn.setDisable(false);
            }
        });
        tradePrcFld.setOnKeyReleased(event -> {
            if (!tradePrcFld.getText().matches("^[0-9]*$")) {
                okBtn.setDisable(true);
            } else {
                okBtn.setDisable(false);
            }
        });
        unitFld.setOnKeyReleased(event -> {
            if (!unitFld.getText().matches("^[0-9]*$")) {
                okBtn.setDisable(true);
            } else {
                okBtn.setDisable(false);
            }
        });
        availabilityFld.setOnKeyReleased(event -> {
            if (!availabilityFld.getText().matches("^[0-9]*$")) {
                okBtn.setDisable(true);
            } else {
                okBtn.setDisable(false);
            }
        });
    }

    public void columnMappingWindow(Parent columnMappingModal) {
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        Scene secondScene = new Scene(columnMappingModal);
        dialog.setScene(secondScene);
        dialog.setTitle("Маппинг полей");
        dialog.setResizable(false);
        dialog.centerOnScreen();
        dialog.showAndWait();
    }

    public void columnMapping(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button) {
            Button btn = (Button) actionEvent.getSource();
            if (btn.getId().equals("okBtn")) {
                dialog.close();
            }
        }
    }
}
