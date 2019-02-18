package ru.jackK.repairEAS.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.jackK.repairEAS.client.controller.MainController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/main.fxml"));
        Parent root = loader.load();

        MainController mainController = loader.getController();


        Scene scene = new Scene(root,900, 600);
        scene.getStylesheets().add(getClass().getResource("view/style/main.css").toExternalForm());

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
