package ru.jackK.repairEAS.client.util.threadWorker;

import javafx.application.Platform;
import ru.jackK.repairEAS.client.util.cmd.CommandService;
import ru.jackK.repairEAS.client.util.cmd.Service;

import java.io.IOException;
import java.util.function.Consumer;

public class ServiceAction implements Runnable {
    private String nameService;
    private String pathService;
    private StateService action;
    private Consumer<String> writeLog;
    private Consumer<String> setStatus;

    public ServiceAction(String nameService, StateService action, Consumer<String> setStatus) {
        this.nameService = nameService;
        this.action = action;
        this.setStatus = setStatus;
    }

    public ServiceAction(String nameService, StateService action, Consumer<String> writeLog, Consumer<String> setStatus) {
        this.nameService = nameService;
        this.action = action;
        this.writeLog = writeLog;
        this.setStatus = setStatus;
    }

    public ServiceAction(String nameService, String pathService, StateService action, Consumer<String> writeLog, Consumer<String> setStatus) {
        this.nameService = nameService;
        this.pathService = pathService;
        this.action = action;
        this.writeLog = writeLog;
        this.setStatus = setStatus;
    }

    @Override
    public void run() {
        switch (this.action) {
            case RUN:
                runService();
                break;
            case STOP:
                stopService();
                break;
            case PAUSE:
                pauseService();
                break;
            case CONTINUE:
                continueService();
                break;
            case RESTART:
                restartService();
                break;
            case STATE:
                getStateService();
                break;
            case DELETE:
                deleteService();
                break;
            case INSTALL:
                installService(pathService);
                break;

        }
    }

    private void getStateService() {
        CommandService cmd = new CommandService(this.nameService);

        try {
            String resultState = cmd.scState().getServiceState();

            Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));
        } catch (Exception ex) {
            Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, "Не удалось получить состояние.")));
        }
    }

    private void installService(String pathService) {
        CommandService cmd = new CommandService(this.nameService, pathService);
        Platform.runLater(() -> writeLog.accept("=======Установка службы " + this.nameService + "=======\n"));

        try {
            int resultIstall = cmd.scInstall();

            if (resultIstall == 0) {
                Platform.runLater(() -> writeLog.accept("Служба " + this.nameService + " установлена\n"));

                String resultState = cmd.scState().getServiceState();
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));
            } else {
                Platform.runLater(() -> writeLog.accept("Не удалось установить службу " + this.nameService + ", код ошибки " + resultIstall + "\n"));
            }
        } catch (IOException ioEx) {
            Platform.runLater(() -> writeLog.accept("InterruptedException: " + ioEx.getMessage() + "\n"));
        } catch (InterruptedException interEx) {
            Platform.runLater(() -> writeLog.accept("InterruptedException: " + interEx.getMessage() + "\n"));
        }
    }

    private void deleteService() {
        CommandService cmd = new CommandService(this.nameService);
        Platform.runLater(() -> writeLog.accept("=======Удаление службы " + this.nameService + "=======\n"));

        try {
            int resultIstall = cmd.scDelete();

            if (resultIstall == 0) {
                Platform.runLater(() -> writeLog.accept("Служба " + this.nameService + " удалена\n"));

                String resultState = cmd.scState() == null ? "Удалено" : "Не удалено";
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));
            } else {
                Platform.runLater(() -> writeLog.accept("Не удалось удалить службу " + this.nameService + ", код ошибки " + resultIstall + "\n"));
            }
        } catch (IOException ioEx) {
            Platform.runLater(() -> writeLog.accept("InterruptedException: " + ioEx.getMessage() + "\n"));
        } catch (InterruptedException interEx) {
            Platform.runLater(() -> writeLog.accept("InterruptedException: " + interEx.getMessage() + "\n"));
        }
    }

    private void restartService() {
        CommandService cmd = new CommandService(this.nameService);
        Platform.runLater(() -> writeLog.accept("=======Перезапуск службы " + this.nameService + "=======\n"));

        try {

            Platform.runLater(() -> writeLog.accept("Получаю статус службы " + this.nameService + "...\n"));
            Service sc = cmd.scState();
            Platform.runLater(() -> writeLog.accept("Статус службы " + this.nameService + " получен.\n"));

            if (sc != null && sc.isPaused()) {
                Platform.runLater(() -> writeLog.accept("Перезапуск службы " + this.nameService + "...\n"));

                int result = cmd.scRestart();
                String resultState = cmd.scState().getServiceState();

                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));

                if (result == 0)
                    Platform.runLater(() -> writeLog.accept("Служба " + this.nameService + " перезапущена.\n"));
                else
                    Platform.runLater(() -> writeLog.accept("Произошла ошибка, код " + result + ", статус службы " + this.nameService + ": " + resultState + "\n"));

            } else if (sc == null) {
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, "Не установленно в системе")));
                Platform.runLater(() -> writeLog.accept("Не удалось найти установленную службу " + this.nameService + " в системе.\n"));
            } else {
                Platform.runLater(() ->  writeLog.accept("Не удалось перезапустить службу т.к. статус службы " + this.nameService + ": " + sc.getServiceState() + "\n"));
            }

        } catch (InterruptedException interEx) {
            Platform.runLater(() -> writeLog.accept(this.nameService + " InterruptedException: " + interEx.getMessage() + "\n"));
        } catch (IOException ioEx) {
            Platform.runLater(() -> writeLog.accept(this.nameService + " IOException: " + ioEx.getMessage() + "\n"));
        } catch (Exception ex) {
            Platform.runLater(() -> writeLog.accept(this.nameService + " Exception: " + ex.getMessage() + "\n"));
        }
    }

    private void continueService() {
        CommandService cmd = new CommandService(this.nameService);
        Platform.runLater(() -> writeLog.accept("=======Запуск службы " + this.nameService + "=======\n"));

        try {

            Platform.runLater(() -> writeLog.accept("Получаю статус службы...\n"));
            ru.jackK.repairEAS.client.util.cmd.Service sc = cmd.scState();
            Platform.runLater(() -> writeLog.accept("Статус получен.\n"));

            if (sc != null && sc.isPaused()) {
                Platform.runLater(() -> writeLog.accept("Запуск службы...\n"));

                int result = cmd.scContinue();
                String resultState = cmd.scState().getServiceState();

                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));

                if (result == 0)
                    Platform.runLater(() ->  writeLog.accept("Служба запущена.\n"));
                else
                    Platform.runLater(() ->  writeLog.accept("Произошла ошибка, код " + result + ", статус службы: " + resultState + "\n"));

            } else if (sc == null) {
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, "Не установленно в системе")));
                Platform.runLater(() -> writeLog.accept("Не удалось найти установленную службу в системе.\n"));
            } else {
                Platform.runLater(() ->  writeLog.accept("Не удалось запустить службу т.к. статус службы: " + sc.getServiceState() + "\n"));
            }

        } catch (InterruptedException interEx) {
            Platform.runLater(() ->  writeLog.accept("InterruptedException: " + interEx.getMessage() + "\n"));
        } catch (IOException ioEx) {
            Platform.runLater(() -> writeLog.accept("IOException: " + ioEx.getMessage() + "\n"));
        } catch (Exception ex) {
            Platform.runLater(() -> writeLog.accept("Exception: " + ex.getMessage() + "\n"));
        }
    }

    private void pauseService() {
        CommandService cmd = new CommandService(this.nameService);
        Platform.runLater(() -> writeLog.accept("=======Приостановка службы " + this.nameService + "=======\n"));

        try {

            Platform.runLater(() -> writeLog.accept("Получаю статус службы...\n"));
            ru.jackK.repairEAS.client.util.cmd.Service sc = cmd.scState();
            Platform.runLater(() -> writeLog.accept("Статус получен.\n"));

            if (sc != null && sc.isRunning()) {
                Platform.runLater(() -> writeLog.accept("Приостановка службы...\n"));

                int result = cmd.scPause();
                String resultState = cmd.scState().getServiceState();

                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));

                if (result == 0)
                    Platform.runLater(() ->  writeLog.accept("Служба приостановлена.\n"));
                else
                    Platform.runLater(() ->  writeLog.accept("Произошла ошибка, код " + result + ", статус службы: " + resultState + "\n"));
            } else if (sc == null) {
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, "Не установленно в системе")));
                Platform.runLater(() -> writeLog.accept("Не удалось найти установленную службу в системе.\n"));
            } else {
                Platform.runLater(() -> writeLog.accept("Не удалось приостановить службу т.к. статус службы: " + sc.getServiceState() + "\n"));
            }

        } catch (InterruptedException interEx) {
            Platform.runLater(() -> writeLog.accept("InterruptedException: " + interEx.getMessage() + "\n"));
        } catch (IOException ioEx) {
            Platform.runLater(() ->  writeLog.accept("IOException: " + ioEx.getMessage() + "\n"));
        } catch (Exception ex) {
            Platform.runLater(() -> writeLog.accept("Exception: " + ex.getMessage() + "\n"));
        }
    }

    private void stopService() {
        CommandService cmd = new CommandService(this.nameService);
        Platform.runLater(() -> writeLog.accept("=======Остановка службы " + this.nameService + "=======\n"));

        try {

            Platform.runLater(() -> writeLog.accept("Получаю статус службы...\n"));
            ru.jackK.repairEAS.client.util.cmd.Service sc = cmd.scState();
            Platform.runLater(() -> writeLog.accept("Статус получен.\n"));

            if (sc != null && sc.isRunning()) {
                Platform.runLater(() -> writeLog.accept("Остановка службы...\n"));

                int result = cmd.scStop();
                String resultState = cmd.scState().getServiceState();

                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));

                if (result == 0)
                    Platform.runLater(() -> writeLog.accept("Служба остановлена.\n"));
                else
                    Platform.runLater(() -> writeLog.accept("Произошла ошибка, код " + result + ", статус службы: " + resultState + "\n"));

            } else if (sc == null) {
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, "Не установленно в системе")));
                Platform.runLater(() -> writeLog.accept("Не удалось найти установленную службу в системе.\n"));
            } else {
                Platform.runLater(() -> writeLog.accept("Не удалось остановить службу т.к. статус службы: " + sc.getServiceState() + "\n"));
            }

        } catch (InterruptedException interEx) {
            Platform.runLater(() -> writeLog.accept("InterruptedException: " + interEx.getMessage() + "\n"));
        } catch (IOException ioEx) {
            Platform.runLater(() ->  writeLog.accept("IOException: " + ioEx.getMessage() + "\n"));
        } catch (Exception ex) {
            Platform.runLater(() -> writeLog.accept("Exception: " + ex.getMessage() + "\n"));
        }
    }

    private void runService() {
        CommandService cmd = new CommandService(this.nameService);
        Platform.runLater(() -> writeLog.accept("=======Запуск службы "+ this.nameService +"=======\n"));

        try {

            Platform.runLater(() -> writeLog.accept("Получаю статус службы...\n"));
            Service sc = cmd.scState();
            Platform.runLater(() -> writeLog.accept("Статус получен.\n"));

            if (sc != null && sc.isStopped()) {
                Platform.runLater(() ->  writeLog.accept("Запуск службы...\n"));

                int result = cmd.scStart();
                String resultState = cmd.scState().getServiceState();

                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));

                if (result == 0)
                    Platform.runLater(() -> writeLog.accept("Служба запущена.\n"));
                else
                    Platform.runLater(() -> writeLog.accept("Произошла ошибка, код " + result + ", статус службы: " + resultState + "\n"));

            } else if (sc == null) {
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, "Не установленно в системе")));
                Platform.runLater(() -> writeLog.accept("Не удалось найти установленную службу в системе.\n"));
            } else {
                Platform.runLater(() -> writeLog.accept("Не удалось запустить службу т.к. статус службы: " + sc.getServiceState() + "\n"));
            }

        } catch (InterruptedException interEx) {
            Platform.runLater(() -> writeLog.accept("InterruptedException: " + interEx.getMessage() + "\n"));
        } catch (IOException ioEx) {
            Platform.runLater(() -> writeLog.accept("IOException: " + ioEx.getMessage() + "\n"));
        } catch (Exception ex) {
            Platform.runLater(() -> writeLog.accept("Exception: " + ex.getMessage() + "\n"));
        }
    }
}
