package ru.jackK.repairEAS.client.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ru.jackK.repairEAS.client.model.SqlParametr;
import ru.jackK.repairEAS.client.util.Config;
import ru.jackK.repairEAS.client.util.threadWorker.DataBaseAction;
import ru.jackK.repairEAS.client.util.threadWorker.DbAction;

public class RepairTransactionController {

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

    private Config config;

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
        String postingId = txtIdPosting.getText().trim();
        String checkId = txtIdCheck.getText().trim().isEmpty() ? "-1" : txtIdCheck.getText();
        String windowId = txtIdWindow.getText().trim();
        String postIndex = windowId.substring(0, 7);

        if (!isNullOrEmpty(postingId) && !isNullOrEmpty(checkId) && !isNullOrEmpty(windowId)) {

            ArrayList<SqlParametr> params = new ArrayList<>();

            params.add(new SqlParametr("varchar", postIndex));
            params.add(new SqlParametr("varchar", windowId));
            params.add(new SqlParametr("varchar", postingId));
            params.add(new SqlParametr("varchar", checkId ));

            DataBaseAction dbAction = new DataBaseAction(DbAction.ExecuteScript, config.getSqlServiceName(), config.getPortSqlServer(),
                                            config.getUserNameSql(), config.getPasswordSql(), config.getDbName(), writeLog);
            dbAction.setParams(params);

            new Thread(dbAction).start();

        } else {

        }

    }

    private boolean isNullOrEmpty(String text) {
        if (text == null || text.trim().isEmpty())
            return true;

        return false;
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
    public void setConfig(Config config) {
        this.config = config;
    }
}
