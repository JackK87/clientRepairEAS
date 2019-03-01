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

    private ArrayList<SqlParametr> params;

    public void setParams(ArrayList<SqlParametr> params) {
        this.params = params;
    }

    public DataBaseAction(DbAction action, String server, int port, String user, String password, String dbName, Consumer<String> writeLog) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.dbName = dbName;

        this.action = action;
        this.writeLog = writeLog;
        this.params = new ArrayList<>();
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
                execRepairTransaction();
                break;
        }
    }

    private void execRepairTransaction() {
        if (params != null && params.size() == 4) {
            String repairTransactionScript = "DECLARE\n" +
                    " @dataareaid NVARCHAR(4) = 'rp', -- Номер площадки\n" +
                    " @storeid NVARCHAR(6) = ?, -- Номер ОПС\n" +
                    " @terminalid NVARCHAR(10) = ?,-- Номер окна (Терминал)\n" +
                    " @transactionNumber NVARCHAR(10) = ?,--«Кассовый номер проводки» (значение в поле «Проводка» в Сверке транзакций)\n" +
                    " @recieptNumber NVARCHAR(10) = ?, --«Фискальный номер чека»\n" +
                    " @logString nvarchar(max) \n" +
                    "SELECT [DataAreaId], [STOREID], [TERMINALID], [TransactionId], [RetailTransactionId], [NotSavedRetailTransactionId] INTO #transactions_saved FROM dbo.GM_Transaction\n" +
                    "WHERE DataAreaId = @dataareaid AND STOREID = @storeid AND TERMINALID = @terminalid\n" +
                    "\tAND NotSavedRetailTransactionId in\n" +
                    "\t\t(\n" +
                    "\t\t\tSELECT TRANSACTIONID \n" +
                    "\t\t\tFROM dbo.RETAILTRANSACTIONTABLE \n" +
                    "\t\t\tWHERE RECEIPTID =@transactionNumber AND DataAreaId = @dataareaid AND Store = @storeid AND TERMINAL = @terminalid\n" +
                    "\t\t)\n" +
                    "UPDATE t SET t.RetailTransactionId = t.NotSavedRetailTransactionId, t.NotSavedRetailTransactionId = NULL FROM GM_Transaction t INNER JOIN #transactions_saved tt ON (t.[DataAreaId]=tt.[DataAreaId] AND t.[STOREID]=tt.[STOREID] AND t.[TERMINALID]=tt.[TERMINALID] AND t.[TransactionId]=tt.[TransactionId])\n" +
                    "IF @@ROWCOUNT <> 0\n" +
                    "\tSELECT @logString = COALESCE(@logString+';'+ CHAR(13) ,'') + '[GM_Transaction] record with key ([DataAreaId]='''+DataAreaId+''', [STOREID]='''+STOREID+''', [TERMINALID]='''+TERMINALID+''', [TransactionId]='''+TransactionId+''') was updated. Old values ([RetailTransactionId]='''+RetailTransactionId+''', [NotSavedRetailTransactionId]='''+NotSavedRetailTransactionId+''') were changed on new values ([RetailTransactionId]='''+NotSavedRetailTransactionId+''', [NotSavedRetailTransactionId]=NULL)' FROM #transactions_saved\n" +
                    "SELECT [DataAreaId], [STOREID], [TERMINALID], [TransactionId], [Complited] INTO #transactions_complited FROM dbo.GM_Transaction  \n" +
                    "WHERE DataAreaId = @dataareaid AND STOREID = @storeid AND TERMINALID = @terminalid\n" +
                    "\tAND RetailTransactionId in\n" +
                    "\t\t(\n" +
                    "\t\t\t\tSELECT TRANSACTIONID \n" +
                    "\t\t\t\tFROM dbo.RETAILTRANSACTIONTABLE \n" +
                    "\t\t\t\tWHERE RECEIPTID =@transactionNumber AND DataAreaId = @dataareaid AND Store = @storeid AND TERMINAL = @terminalid\n" +
                    "\t\t)\n" +
                    "UPDATE t SET t.Complited = 1 FROM GM_Transaction t INNER JOIN #transactions_complited tt ON (t.[DataAreaId]=tt.[DataAreaId] AND t.[STOREID]=tt.[STOREID] AND t.[TERMINALID]=tt.[TERMINALID] AND t.[TransactionId]=tt.[TransactionId])\n" +
                    "IF @@ROWCOUNT <> 0\n" +
                    "\tSELECT @logString = COALESCE(@logString+';'+ CHAR(13) ,'') + '[GM_Transaction] record with key ([DataAreaId]='''+DataAreaId+''', [STOREID]='''+STOREID+''', [TERMINALID]='''+TERMINALID+''', [TransactionId]='''+TransactionId+''') was updated. Old values (Complited='+LTRIM(RTRIM(STR(Complited)))+') were changed on new values (Complited=1)' FROM #transactions_complited\n" +
                    "SELECT [DataAreaId], [STOREID], [TERMINALID], [TransactionGroupId], [TransactionGroupRecieptId], [FISCALDOCUMENTSERIALNUMBER], [FISCALPRINTERSHIFTID] INTO #transactionGroupReciept FROM dbo.GM_TransactionGroupReciept\n" +
                    "WHERE DataAreaId = @dataareaid AND STOREID = @storeid AND TERMINALID = @terminalid AND (TRY_CAST(FISCALDOCUMENTSERIALNUMBER AS INT) = 0 OR FISCALDOCUMENTSERIALNUMBER IS NULL)\n" +
                    "\tAND TransactionGroupId in\n" +
                    "\t\t( \n" +
                    "\t\t\tSELECT TransactionGroupId \n" +
                    "\t\t\tFROM dbo.GM_TransactionGroup tg\n" +
                    "\t\t\t\tLEFT JOIN dbo.GM_Transaction t ON t.DataAreaId = tg.DataAreaId AND t.STOREID = tg.STOREID AND t.TERMINALID = tg.TERMINALID AND t.TransactionId = tg.TransactionId\n" +
                    "\t\t\t\tLEFT JOIN dbo.RETAILTRANSACTIONTABLE rtt ON rtt.DATAAREAID = t.DataAreaId AND rtt.STORE = t.STOREID AND rtt.TERMINAL = t.TERMINALID AND (rtt.TRANSACTIONID = t.RetailTransactionId OR rtt.TRANSACTIONID= t.NotSavedRetailTransactionId) \n" +
                    "\t\t\tWHERE rtt.RECEIPTID = @transactionNumber AND rtt.DataAreaId = @dataareaid AND rtt.Store = @storeid AND rtt.TERMINAL = @terminalid\n" +
                    "\t\t)\n" +
                    "UPDATE t SET t.FISCALDOCUMENTSERIALNUMBER = @recieptNumber FROM GM_TransactionGroupReciept t INNER JOIN #transactionGroupReciept tt ON (t.[DataAreaId]=tt.[DataAreaId] AND t.[STOREID]=tt.[STOREID] AND t.[TERMINALID]=tt.[TERMINALID] AND t.[TransactionGroupId]=tt.[TransactionGroupId] AND t.[TransactionGroupRecieptId]=tt.[TransactionGroupRecieptId])\n" +
                    "IF @@ROWCOUNT <> 0\n" +
                    "\tSELECT @logString = COALESCE(@logString+';'+ CHAR(13) ,'') + '[GM_TransactionGroupReciept] record with key ([DataAreaId]='''+DataAreaId+''', [STOREID]='''+STOREID+''', [TERMINALID]='''+TERMINALID+''', [TransactionGroupId]='''+TransactionGroupId+''', [TransactionGroupRecieptId]='''+TransactionGroupRecieptId+''') was updated. Old values ([FISCALDOCUMENTSERIALNUMBER]='''+FISCALDOCUMENTSERIALNUMBER+''', [FISCALPRINTERSHIFTID]='''+FISCALPRINTERSHIFTID+''') were changed on new values ([FISCALDOCUMENTSERIALNUMBER]='''+@recieptNumber+''')' FROM #transactionGroupReciept\n" +
                    "IF (@logString IS NOT NULL) \n" +
                    "BEGIN\n" +
                    "\tINSERT INTO [RETAILLOG] ([LOGDATE],[CODEUNIT],[LOGLEVEL],[LOGSTRING],[STOREID],[TERMINALID],[DURATIONINMILLISEC],[DATAAREAID],[REPLICATIONCOUNTER],[APPLICATIONID]) VALUES(getDate(),'RepairTransactionScript',0,@logString,@storeid,@terminalid,0,@dataareaid,0,1)\n" +
                    "END\n" +
                    "drop table #transactions_complited\n" +
                    "drop table #transactions_saved\n" +
                    "drop table #transactionGroupReciept";

            Platform.runLater(() -> { writeLog.accept("=======Исправление зависшей транзакций " + this.server + "=======\n"); });
            DbMsSql db = DbMsSql.getInstance(server, port, user, password);

            try {
                Platform.runLater(() -> { writeLog.accept("Устанавливаю подключение к базе данных " + this.dbName + "...\n"); });
                db.connect(dbName);
                Platform.runLater(() -> { writeLog.accept("Подключение установлено, выполняю скрипт, ждите...\n"); });
                db.executeScript(repairTransactionScript, this.params);
                Platform.runLater(() -> { writeLog.accept("Скрипт выполнен успешно.\n"); });
            } catch (SQLException sqlEx) {
                Platform.runLater(() -> { writeLog.accept( "SQLException: " + sqlEx.getMessage() + "\n"); });
            } catch (Exception ex) {
                Platform.runLater(() -> { writeLog.accept( "Exception: " + ex.getMessage() + "\n"); });
            } finally {
                try {
                    db.disconnect();
                } catch (SQLException sqlEx) {
                    Platform.runLater(() -> { writeLog.accept("Ошибка при отключении от сервера " + this.server + ": " + sqlEx.getMessage()); });
                }
            }
        }
    }

    private void execReplicaExport() {
        if (this.params != null && this.params.size() == 1) {
            String storePocedure = "{call ReplicaExport(?)}";

            Platform.runLater(() -> {
                writeLog.accept("=======Выгрузка нулевой реплики на сервере " + this.server + "=======\n");
            });
            DbMsSql db = DbMsSql.getInstance(server, port, user, password);

            try {
                Platform.runLater(() -> {
                    writeLog.accept("Устанавливаю подключение к базе данных " + this.dbName + "...\n");
                });
                db.connect(dbName);
                Platform.runLater(() -> {
                    writeLog.accept("Подключение установлено, выгружаю реплику, ждите...\n");
                });
                db.executeStoredProcedure(storePocedure, this.params);
                Platform.runLater(() -> {
                    writeLog.accept("Выгрузка выполнена.\n");
                });
            } catch (SQLException sqlEx) {
                Platform.runLater(() -> {
                    writeLog.accept("SQLException: " + sqlEx.getMessage() + "\n");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    writeLog.accept("Exception: " + ex.getMessage() + "\n");
                });
            } finally {
                try {
                    db.disconnect();
                } catch (SQLException sqlEx) {
                    Platform.runLater(() -> {
                        writeLog.accept("Ошибка при отключении от сервера " + this.server + ": " + sqlEx.getMessage());
                    });
                }
            }
        }

    }
}
