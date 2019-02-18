package ru.jackK.repairEAS.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import ru.jackK.repairEAS.client.io.Client;
import ru.jackK.repairEAS.client.util.Config;
import ru.jackK.repairEAS.client.util.FileWorker.FileWorker;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import ru.jackK.repairEAS.client.util.ThreadWorker.ServiceAction;
import ru.jackK.repairEAS.client.util.ThreadWorker.StateService;

public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label lblStateGmmq;

    @FXML
    private Label lblStateSheduler;

    @FXML
    private Label lblDateExport;

    @FXML
    private Label lblDateImport;

    @FXML
    private Label lblStateSql;

    @FXML
    private TextArea txtLogCommand;

    @FXML
    private GridPane panelMain;

    @FXML
    private Button btnGetStateService;

    @FXML
    private Button btnInstallGmmq;

    @FXML
    private Button btnInstallSheduler;

    @FXML
    private Button btnRunGmmq;

    @FXML
    private Button btnSettingsEas;

    @FXML
    private Button btnRepairTransaction;

    @FXML
    private Button btnReplicaExport;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnRepairTransport;

    @FXML
    private Button btnRestartService;

    @FXML
    private Button btnClearFolders;

    @FXML
    private Button btnRunSql;

    @FXML
    private Button btnStopSql;

    @FXML
    private Button btnGetInfoSystem;

    @FXML
    private Button btnDeleteGmmq;

    @FXML
    private Button btnDeleteSheduler;

    @FXML
    private Button btnStopSheduer;

    @FXML
    private Button btnRunSheduler;

    @FXML
    private Button btnStopGmmq;

    @FXML
    private Button btnSettings;

    @FXML
    private Label lblNamePC;

    @FXML
    private Font x3;

    @FXML
    private Label lblVersion;

    Consumer<String> writeLog = message -> {
        log.info(message);
        txtLogCommand.appendText(message);
    };
    Consumer<String> setServiceStatus = data -> {
        if (data != null && !data.trim().isEmpty()) {
            String[] arrayData = data.split(":");

            if (arrayData.length == 2) {
                String scName = arrayData[0];
                String status = arrayData[1];

                switch (scName) {
                    case "GMMQ":
                    case "EPWD": //GMMQ
                        lblStateGmmq.setText(status);
                        break;
                    case "GM_ShedulerSvc":
                    case "Fax": //GM_ShedulerSvc
                        lblStateSheduler.setText(status);
                        break;
                    case "MSSQLSERVER":
                    case "FontCache": //MSSQLSERVER
                            lblStateSql.setText(status);
                            break;
                }
            }
        }
    };

    private Client client;
    private Config config;
    private String pathConfig, configName;

    private static final Logger log = Logger.getLogger(MainController.class);

    private EventHandler<ActionEvent> onClickedExit = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handleExitClicked(event);
        }
    };
    private EventHandler<ActionEvent> onClickedGmmq = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handleGmmqClicked(config.getGmmqServiceName(), event);
        }
    };
    private EventHandler<ActionEvent> onClickedSheduler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handleShedulerClicked(config.getShedulerServiceName(), event);
        }
    };
    private EventHandler<ActionEvent> onClickedSql = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handleSqlClicked(config.getSqlServiceName(), event);
        }
    };

    private void handleSqlClicked(String serviceName, ActionEvent event) {
        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();

            switch (btn.getId()) {
                case "btnRunSql":
                    new Thread(new ServiceAction(serviceName, StateService.RUN, writeLog, setServiceStatus)).start();
                    break;
                case "btnStopSql":
                    new Thread(new ServiceAction(serviceName, StateService.STOP, writeLog, setServiceStatus)).start();
                    break;
                case "":
                    new Thread(new ServiceAction(serviceName, StateService.RESTART, writeLog, setServiceStatus)).start();
                    break;
            }
        }
    }

    private void handleShedulerClicked(String serviceName, ActionEvent event) {
        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();

            switch (btn.getId()) {
                case "btnRunSheduler":
                    //new ServiceAction(serviceName, StateService.RUN, writeLog, setServiceStatus);
                    new Thread(new ServiceAction(serviceName, StateService.RUN, writeLog, setServiceStatus)).start();
                    break;
                case "btnStopSheduer":
                    //new ServiceAction(serviceName, StateService.STOP, writeLog, setServiceStatus);
                    new Thread(new ServiceAction(serviceName, StateService.STOP, writeLog, setServiceStatus)).start();
                    break;
                case "":
                    new Thread(new ServiceAction(serviceName, StateService.RESTART, writeLog, setServiceStatus)).start();
                    break;
                case "btnDeleteSheduler":
                    break;
                case "btnInstallSheduler":
                    break;
            }
        }
    }

    private void handleGmmqClicked(String serviceName, ActionEvent event) {
        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();

            switch (btn.getId()) {
                case "btnRunGmmq":
                    new Thread(new ServiceAction(serviceName, StateService.RUN, writeLog, setServiceStatus)).start();
                    break;
                case "btnStopGmmq":
                    new Thread(new ServiceAction(serviceName, StateService.STOP, writeLog, setServiceStatus)).start();
                    break;
                case "":
                    new Thread(new ServiceAction(serviceName, StateService.RESTART, writeLog, setServiceStatus)).start();
                    break;
                case "btnDeleteGmmq":
                    break;
                case "btnInstallGmmq":
                    break;
            }
        }
    }

    private void handleExitClicked(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void initialize() {
        log.info("===========================Запуск программы===========================\n");
        configName = "config.dat";

        try {
            log.info("Загружаю файл конфигураций");
            pathConfig = String.join("\\", getJarPath(), configName);
            config = FileWorker.getConfig(pathConfig);

            if (config == null) {
                log.warn("Файл конфигураций не найден. Создаю конфигурацию по-умолчанию");
                config = CreateDefaultConfig();
                FileWorker.saveConfig(pathConfig, config);
            }
        } catch (URISyntaxException uriExp) {
            log.error("Ошибка написания пути к исполняемому файлу.", uriExp);
            System.out.println(uriExp);
        } catch (IOException ioExp) {
            log.error("Ошибка ввода/вывода.", ioExp);
            System.out.println(ioExp);
        } catch (ClassNotFoundException classExp) {
            log.error("Не найден класс.", classExp);
            System.out.println(classExp);
        } catch (Exception ex) {
            log.error("Ошибка при создании файла конфигураций.", ex);
            System.out.println(ex);
        }

        log.info("Создаю объект клиента для подключения к серверу.");
        client = new Client(config.getUriSocketIO(), config.getUsernameIO(), config.getPasswordIO());
        log.info("Объект клиента для подключения к серверу создан. " + client.toString());

        new Thread(new ServiceAction(config.getGmmqServiceName(), StateService.STATE, setServiceStatus)).start();
        new Thread(new ServiceAction(config.getShedulerServiceName(), StateService.STATE, setServiceStatus)).start();
        new Thread(new ServiceAction(config.getSqlServiceName(), StateService.STATE, setServiceStatus)).start();

        lblDateExport.setText(FileWorker.getLastModifedSimpleFormat(config.getPathExportGMMQ(), config.getDateFormate()));
        lblDateImport.setText(FileWorker.getLastModifedSimpleFormat(config.getPathImportGMMQ(), config.getDateFormate()));

        btnExit.setOnAction(onClickedExit);

        btnRunGmmq.setOnAction(onClickedGmmq);
        btnStopGmmq.setOnAction(onClickedGmmq);

        btnRunSheduler.setOnAction(onClickedSheduler);
        btnStopSheduer.setOnAction(onClickedSheduler);

        btnRunSql.setOnAction(onClickedSql);
        btnStopSql.setOnAction(onClickedSql);

        //region старое
        /*
        btnRepairTransaction.setOnAction(event -> {
            //client.Connect();
            ArrayList<String> errorList = new ArrayList<>();
            boolean resultDelete = FileWorker.allFileDelete(config.getPathImportGMMQ(), errorList);

            if (resultDelete && errorList.size() == 0) {
                System.out.println("Удаление файлов прошло успешно.");
            } else if (resultDelete && errorList.size() > 0) {
                System.out.println("Не все файлы бали удалены:");
                for (String fileName : errorList)
                    System.out.println(fileName);
            } else {
                System.out.println("Файлы не удалось удалить. Возможно в метод передали пустую ссылку или указатель на файл.");
            }

        });

        btnRestartService.setOnAction(event -> {
            client.Disconnect();
            CommandService cmd = new CommandService("EPWD");
            try {
                ServiceAction sc = cmd.scState();
                System.out.println("Остановка службы " + cmd.scStop());
                System.out.println("Запуск службы " + cmd.scStart());
                System.out.println("Пауза службы " + cmd.scPause());
                System.out.println("Запуск службы " + cmd.scContinue());
                System.out.println("Перезапуск службы " + cmd.scRestart());

                String query = "SELECT [PROFILEID],[NAME],[SCANNER],[SCANNERDEVICENAME],[SCALE],[SCALEDEVICENAME],[FISCALPRINTER],[FISCALPRINTERDEVICENAME] FROM [RETAILHARDWAREPROFILE] WHERE [NAME] = ?";
                Map<String, String> env = System.getenv();
                DbMsSql msSql = DbMsSql.getInstance("R04-649001-N", "sa", "Sup93aP6M*");

                msSql.connect("DB649001");

                //boolean result = msSql.executeStoredProcedure(null, null);
                //String script = ""

                //boolean result = msSql.executeScript(script);
                // boolean result = msSql.executeScript(script);


               if (result)
                   System.out.println("Выгрузка 0 ой реплики успешно завершена.");
               else*/
                   /*System.out.println("Не удалось выгрузить 0 ую реплику.");
                ArrayList<SqlParametr> params = new ArrayList<SqlParametr>();
                params.add(new SqlParametr("varchar", "Окно 1"));
                ArrayList<ArrayList<RowTable>> resultQuery = msSql.executeQuery(query, params);

                for (ArrayList<RowTable> rows : resultQuery) {
                    for (RowTable item : rows) {
                        System.out.printf("%s\t%s\n", item.column, item.value);
                    }
                    System.out.println("============================");
                }

                msSql.disconnect();

            } catch (Exception ex) {
                System.out.println(ex);
            }
        });*/
                   //endregion
    }

    private Config CreateDefaultConfig() {
        Config config = new Config("super_admin", "super_secret", "http://localhost:3000");

        config.setPathExportGMMQ("c:\\GMMQ\\Export");
        config.setPathImportGMMQ("c:\\GMMQ\\Import");

        /*config.setGmmqServiceName("GMMQ");
        config.setShedulerServiceName("GM_SchedulerSvc");
        config.setSqlServiceName("MSSQLSERVER");*/

        config.setGmmqServiceName("GMMQ");
        config.setShedulerServiceName("Fax");
        config.setSqlServiceName("FontCache");

        config.setUserNameSql("sa");
        config.setPasswordSql("Sup93aP6M*");
        config.setOfflineMode(false);

        config.setDateFormate("dd MMM YYYY HH:mm:ss");

        return config;
    }

    private String getJarPath() throws URISyntaxException {
        CodeSource codeSource = MainController.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI());

        return jarFile.getParent();
    }
}
