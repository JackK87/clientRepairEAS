package ru.jackK.repairEAS.client.util.threadWorker;

import javafx.application.Platform;
import ru.jackK.repairEAS.client.model.SqlParametr;
import ru.jackK.repairEAS.client.util.dataBase.DbMsSql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class DataBaseAction implements Runnable {
    private String server;
    private int port;

    private String user;
    private String password;

    private String dbName;

    private DbAction action;

    private Consumer<String> writeLog;

    public DataBaseAction(DbAction action, String server, int port, String user, String password, String dbName, Consumer<String> writeLog) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.dbName = dbName;

        this.action = action;
        this.writeLog = writeLog;
    }

    public DataBaseAction(DbAction action, String server, String user, String password, String dbName, Consumer<String> writeLog) {
        this(action, server, 1433, user, password, dbName, writeLog);
    }

    @Override
    public void run() {
        switch (this.action) {
            case ExecuteQuery:
                break;
            case ExecuteUpdate:
                break;
            case ExecuteProcedure:
                execReplicaExport();
                break;
            case ExecuteScript:
                break;
        }
    }

    private void execReplicaExport() {
        String storePocedure = "{call ReplicaExport(?)}";
        ArrayList<SqlParametr> params = new ArrayList<>();
        params.add(new SqlParametr("int", "0"));

        Platform.runLater(() -> { writeLog.accept("=======Выгрузка нулевой реплики на сервере " + this.server + "=======\n"); });
        DbMsSql db = DbMsSql.getInstance(server, port, user, password);

        try {
            Platform.runLater(() -> { writeLog.accept("Устанавливаю подключение к базе данных " + this.dbName + "...\n"); });
            db.connect(dbName);
            Platform.runLater(() -> { writeLog.accept("Подключение установлено, выгружаю реплику, ждите...\n"); });
            db.executeStoredProcedure(storePocedure, params);
            Platform.runLater(() -> { writeLog.accept("Выгрузка выполнена.\n"); });
        } catch (SQLException sqlEx) {
            Platform.runLater(() -> { writeLog.accept( "SQLException: " + sqlEx.getMessage() + "\n"); });
        } finally {
            try {
                //Platform.runLater(() -> { writeLog.accept("Отключаюсь от сервера " + this.server + "\n"); });
                db.disconnect();
            } catch (SQLException sqlEx) {
                Platform.runLater(() -> { writeLog.accept("Ошибка при отключении от сервера " + this.server + ": " + sqlEx.getMessage()); });
            }
        }

    }
}
