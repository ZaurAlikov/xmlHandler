package ru.alcotester.pricehandler;

import ru.alcotester.pricehandler.controller.PriceUpdaterController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PriceUpdater.fxml"));
        Parent root = loader.load();
        PriceUpdaterController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Price updater");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
//        Map<String, String> email = GMailService.getEmail("has:attachment label:Поставщики filename:pdf", 10L, false); //Label_4418826869810109691  inbox
    }


}
