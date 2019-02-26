package ru.jackK.repairEAS.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import ru.jackK.repairEAS.client.io.Client;
import ru.jackK.repairEAS.client.util.Config;
import ru.jackK.repairEAS.client.util.fileWorker.FileWorker;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import ru.jackK.repairEAS.client.util.threadWorker.*;

public class MainController {

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
    private StackPane container;

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
                    case "GM_SchedulerSvc":
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
    private Parent repairTransaction;

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
    private EventHandler<ActionEvent> onClickedClearFolders = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handleClickedClearFolders();
        }
    };
    private EventHandler<ActionEvent> onClickedRestartServices = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handleClickedRestartServices();
        }
    };
    private EventHandler<ActionEvent> onClickedDbActions = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (event.getSource() instanceof Button) {
                Button btn = (Button)event.getSource();

                switch (btn.getId()) {
                    case "btnReplicaExport":
                        handleClickedReplicaExport();
                        break;
                    case "btnRepairTransaction":
                        handleClickedRepairTransaction();
                        break;
                }
            }
        }
    };

    private void handleClickedRepairTransaction() {
        this.panelMain.setVisible(false);
        this.repairTransaction.setVisible(true);
    }

    private void handleClickedReplicaExport() {
        new Thread(new DataBaseAction(DbAction.ExecuteProcedure, config.getNameSqlServer(), config.getPortSqlServer(),
                                        config.getUserNameSql(), config.getPasswordSql(), config.getDbName(), writeLog)).start();
    }

    private void handleClickedRestartServices() {
        new Thread(new ServiceAction(config.getGmmqServiceName(), StateService.RESTART, writeLog, setServiceStatus)).start();
        new Thread(new ServiceAction(config.getShedulerServiceName(), StateService.RESTART, writeLog, setServiceStatus)).start();
    }

    private void handleClickedClearFolders() {
        new Thread(new FileAction(config.getPathExportGMMQ(), writeLog)).start();
        new Thread(new FileAction(config.getPathImportGMMQ(), writeLog)).start();
    }

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
                    new Thread(new ServiceAction(serviceName, StateService.DELETE, writeLog, setServiceStatus)).start();
                    break;
                case "btnInstallSheduler":
                    new Thread(new ServiceAction(serviceName, config.getPathShedulerService(), StateService.INSTALL, writeLog, setServiceStatus)).start();
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
                    new Thread(new ServiceAction(serviceName, config.getPathGmmqService() ,StateService.DELETE, writeLog, setServiceStatus)).start();
                    break;
                case "btnInstallGmmq":
                    new Thread(new ServiceAction(serviceName, StateService.INSTALL, writeLog, setServiceStatus)).start();
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

        lblNamePC.setText(getNamePC());

        btnExit.setOnAction(onClickedExit);

        btnRunGmmq.setOnAction(onClickedGmmq);
        btnStopGmmq.setOnAction(onClickedGmmq);

        btnRunSheduler.setOnAction(onClickedSheduler);
        btnStopSheduer.setOnAction(onClickedSheduler);

        btnRunSql.setOnAction(onClickedSql);
        btnStopSql.setOnAction(onClickedSql);

        btnInstallGmmq.setOnAction(onClickedGmmq);
        btnDeleteGmmq.setOnAction(onClickedGmmq);
        btnInstallSheduler.setOnAction(onClickedSheduler);
        btnDeleteSheduler.setOnAction(onClickedSheduler);

        btnClearFolders.setOnAction(onClickedClearFolders);

        btnRestartService.setOnAction(onClickedRestartServices);

        btnReplicaExport.setOnAction(onClickedDbActions);

        btnRepairTransaction.setOnAction(onClickedDbActions);
    }

    public void initializeFXMLForms(FXMLLoader... loaders) throws IOException {

        for (int index = 0; index < loaders.length; index++) {
            FXMLLoader loader = loaders[index];

            Parent form = loader.load();

            switch (form.getId()) {
                case "paneRepairTransaction":
                    initializeRepairTransaction(form, loader.getController());
                    break;
            }
        }

    }

    private void initializeRepairTransaction(Parent form, RepairTransactionController controller) {
        this.repairTransaction = form;
        this.repairTransaction.setVisible(false);

        this.container.getChildren().add(form);

        controller.setPaneMain(panelMain);
        controller.setWriteLog(writeLog);
    }

    private String getNamePC() {
        Map<String, String> env = System.getenv();

        if (env != null && env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");

        return "";
    }

    private Config CreateDefaultConfig() {
        Config config = new Config("super_admin", "super_secret", "http://localhost:3000");

        config.setPathExportGMMQ("c:\\GMMQ\\Export");
        config.setPathImportGMMQ("c:\\GMMQ\\Import");

        config.setGmmqServiceName("GMMQ");
        config.setShedulerServiceName("GM_SchedulerSvc");
        config.setSqlServiceName("MSSQLSERVER");

        /*config.setGmmqServiceName("EPWD");
        config.setShedulerServiceName("Fax");
        config.setSqlServiceName("FontCache");*/

        config.setUserNameSql("sa");
        config.setPasswordSql("Sup93aP6M*");
        config.setOfflineMode(false);

        config.setDateFormate("dd MMM YYYY HH:mm:ss");

        config.setPathGmmqService("\"C:\\Program Files (x86)\\Microsoft Dynamics AX\\60\\PosServices\\GMMQ\\GMMQ.Client.Service.exe\"");
        config.setPathShedulerService("\"C:\\Program Files (x86)\\Microsoft Dynamics AX\\60\\Retail POS\\GM_Scheduler.exe\"  -displayname \"GM_SchedulerSvc\" -servicename \"GM_SchedulerSvc\"");

        String pcName = "R04-649184-N";
        String[] splitName = pcName.split("-");

        if (splitName.length == 3) {
            String dbName = String.format("DB%s", splitName[1]);
            config.setNameSqlServer(pcName);
            config.setPortSqlServer(1433);
            config.setDbName(dbName);
        }

        return config;
    }

    private String getJarPath() throws URISyntaxException {
        CodeSource codeSource = MainController.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI());

        return jarFile.getParent();
    }
}
