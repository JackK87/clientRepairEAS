package ru.jackK.repairEAS.client.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class RepairTransactionController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private GridPane paneRepairTransaction;

    @FXML
    private TextField txtIdCheck;

    @FXML
    private TextField txtIdPosting;

    @FXML
    private Button btnExecuteRepairTransaction;

    @FXML
    private Button btnBackToMain;

    @FXML
    private TextField txtIdWindow;

    private Parent paneMain;
    private Consumer<String> writeLog;

    private EventHandler<ActionEvent> onClickedButton = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (event.getSource() instanceof Button) {
                Button btn = (Button)event.getSource();

                switch (btn.getId()) {
                    case "btnBackToMain":
                        handlerClickedBackToMain();
                        break;
                    case "btnExecuteRepairTransaction":
                        handlerClickedRepairTransport();
                        break;
                }
            }
        }
    };

    private void handlerClickedRepairTransport() {

    }

    private void handlerClickedBackToMain() {
        paneRepairTransaction.setVisible(false);
        paneMain.setVisible(true);
    }

    @FXML
    void initialize() {
        btnBackToMain.setOnAction(onClickedButton);
    }

    public void setPaneMain(Parent paneMain) {
        this.paneMain = paneMain;
    }

    public void setWriteLog(Consumer<String> writeLog) {
        this.writeLog = writeLog;
    }
}
