package ru.jackK.repairEAS.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

import ru.jackK.repairEAS.client.controller.MainController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String pathMainLoader = "view/main.fxml";
        String pathRepairLoader = "view/repairTransaction.fxml";

        URL urlMainLoader = getClass().getResource(pathMainLoader);
        URL urlRepairLoader = getClass().getResource(pathRepairLoader);

        FXMLLoader mainLoader = new FXMLLoader(urlMainLoader);
        FXMLLoader repairLoader = new FXMLLoader(urlRepairLoader);

        Parent root = mainLoader.load();
        //Parent repairTransaction = repairLoader.load();

        MainController mainController = mainLoader.getController();
        mainController.initializeFXMLForms(repairLoader);

        Scene scene = new Scene(root,900, 600);
        scene.getStylesheets().add(getClass().getResource("view/style/main.css").toExternalForm());

        primaryStage.setTitle("Ремонт ЕАС ОПС");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(800);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
