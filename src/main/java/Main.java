import controller.PriceUpdaterController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.GMailService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PriceUpdater.fxml"));
        Parent root = loader.load();
        PriceUpdaterController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
//        Parent root = FXMLLoader.load(getClass()
//                .getResource("PriceUpdater.fxml"));



        primaryStage.setTitle("My Application");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

//        Button btn = new Button();
//        btn.setText("Say 'Hello World'");
//        btn.setOnAction(event -> System.out.println("Hello World!"));
//
//        StackPane root = new StackPane();
//        root.getChildren().add(btn);
//
//        Scene scene = new Scene(root, 300, 250);
//
//        primaryStage.setTitle("Hello World!");
//        primaryStage.setScene(scene);
//        primaryStage.show();
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException, MessagingException {
        launch(args);
        Map<String, String> email = GMailService.getEmail("has:attachment label:Поставщики filename:pdf", 10L, false); //Label_4418826869810109691  inbox
        MainReader reader = new MainReader();
        reader.read();
    }


}
