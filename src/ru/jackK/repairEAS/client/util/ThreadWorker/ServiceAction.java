package ru.jackK.repairEAS.client.util.ThreadWorker;

import javafx.application.Platform;
import ru.jackK.repairEAS.client.util.CMD.CommandService;
import ru.jackK.repairEAS.client.util.CMD.Service;

import java.io.IOException;
import java.util.function.Consumer;

public class ServiceAction implements Runnable {
    private String nameService;
    private StateService action;
    private Consumer<String> updateUI;
    private Consumer<String> setStatus;

    public ServiceAction(String nameService, StateService action, Consumer<String> setStatus) {
        this.nameService = nameService;
        this.action = action;
        this.setStatus = setStatus;
    }

    public ServiceAction(String nameService, StateService action, Consumer<String> updateUI, Consumer<String> setStatus) {
        this.nameService = nameService;
        this.action = action;
        this.updateUI = updateUI;
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
                installService();
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

    private void installService() {

    }

    private void deleteService() {

    }

    private void restartService() {
        CommandService cmd = new CommandService(this.nameService);
        Platform.runLater(() -> updateUI.accept("=======Перезапуск службы " + this.nameService + "=======\n"));

        try {

            Platform.runLater(() -> updateUI.accept("Получаю статус службы...\n"));
            Service sc = cmd.scState();
            Platform.runLater(() -> updateUI.accept("Статус получен.\n"));

            if (sc != null && sc.isPaused()) {
                Platform.runLater(() -> updateUI.accept("Перезапуск службы...\n"));

                int result = cmd.scRestart();
                String resultState = cmd.scState().getServiceState();

                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));

                if (result == 0)
                    Platform.runLater(() -> updateUI.accept("Служба перезапущена.\n"));
                else
                    Platform.runLater(() -> updateUI.accept("Произошла ошибка, код " + result + ", статус службы: " + resultState + "\n"));

            } else if (sc == null) {
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, "Не установленно в системе")));
                Platform.runLater(() -> updateUI.accept("Не удалось найти установленную службу в системе.\n"));
            } else {
                Platform.runLater(() ->  updateUI.accept("Не удалось перезапустить службу т.к. статус службы: " + sc.getServiceState() + "\n"));
            }

        } catch (InterruptedException interEx) {
            Platform.runLater(() -> updateUI.accept("InterruptedException: " + interEx.getMessage() + "\n"));
        } catch (IOException ioEx) {
            Platform.runLater(() -> updateUI.accept("IOException: " + ioEx.getMessage() + "\n"));
        } catch (Exception ex) {
            Platform.runLater(() -> updateUI.accept("Exception: " + ex.getMessage() + "\n"));
        }
    }

    private void continueService() {
        CommandService cmd = new CommandService(this.nameService);
        Platform.runLater(() -> updateUI.accept("=======Запуск службы " + this.nameService + "=======\n"));

        try {

            Platform.runLater(() -> updateUI.accept("Получаю статус службы...\n"));
            ru.jackK.repairEAS.client.util.CMD.Service sc = cmd.scState();
            Platform.runLater(() -> updateUI.accept("Статус получен.\n"));

            if (sc != null && sc.isPaused()) {
                Platform.runLater(() -> updateUI.accept("Запуск службы...\n"));

                int result = cmd.scContinue();
                String resultState = cmd.scState().getServiceState();

                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));

                if (result == 0)
                    Platform.runLater(() ->  updateUI.accept("Служба запущена.\n"));
                else
                    Platform.runLater(() ->  updateUI.accept("Произошла ошибка, код " + result + ", статус службы: " + resultState + "\n"));

            } else if (sc == null) {
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, "Не установленно в системе")));
                Platform.runLater(() -> updateUI.accept("Не удалось найти установленную службу в системе.\n"));
            } else {
                Platform.runLater(() ->  updateUI.accept("Не удалось запустить службу т.к. статус службы: " + sc.getServiceState() + "\n"));
            }

        } catch (InterruptedException interEx) {
            Platform.runLater(() ->  updateUI.accept("InterruptedException: " + interEx.getMessage() + "\n"));
        } catch (IOException ioEx) {
            Platform.runLater(() -> updateUI.accept("IOException: " + ioEx.getMessage() + "\n"));
        } catch (Exception ex) {
            Platform.runLater(() -> updateUI.accept("Exception: " + ex.getMessage() + "\n"));
        }
    }

    private void pauseService() {
        CommandService cmd = new CommandService(this.nameService);
        Platform.runLater(() -> updateUI.accept("=======Приостановка службы " + this.nameService + "=======\n"));

        try {

            Platform.runLater(() -> updateUI.accept("Получаю статус службы...\n"));
            ru.jackK.repairEAS.client.util.CMD.Service sc = cmd.scState();
            Platform.runLater(() -> updateUI.accept("Статус получен.\n"));

            if (sc != null && sc.isRunning()) {
                Platform.runLater(() -> updateUI.accept("Приостановка службы...\n"));

                int result = cmd.scPause();
                String resultState = cmd.scState().getServiceState();

                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));

                if (result == 0)
                    Platform.runLater(() ->  updateUI.accept("Служба приостановлена.\n"));
                else
                    Platform.runLater(() ->  updateUI.accept("Произошла ошибка, код " + result + ", статус службы: " + resultState + "\n"));
            } else if (sc == null) {
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, "Не установленно в системе")));
                Platform.runLater(() -> updateUI.accept("Не удалось найти установленную службу в системе.\n"));
            } else {
                Platform.runLater(() -> updateUI.accept("Не удалось приостановить службу т.к. статус службы: " + sc.getServiceState() + "\n"));
            }

        } catch (InterruptedException interEx) {
            Platform.runLater(() -> updateUI.accept("InterruptedException: " + interEx.getMessage() + "\n"));
        } catch (IOException ioEx) {
            Platform.runLater(() ->  updateUI.accept("IOException: " + ioEx.getMessage() + "\n"));
        } catch (Exception ex) {
            Platform.runLater(() -> updateUI.accept("Exception: " + ex.getMessage() + "\n"));
        }
    }

    private void stopService() {
        CommandService cmd = new CommandService(this.nameService);
        Platform.runLater(() -> updateUI.accept("=======Остановка службы " + this.nameService + "=======\n"));

        try {

            Platform.runLater(() -> updateUI.accept("Получаю статус службы...\n"));
            ru.jackK.repairEAS.client.util.CMD.Service sc = cmd.scState();
            Platform.runLater(() -> updateUI.accept("Статус получен.\n"));

            if (sc != null && sc.isRunning()) {
                Platform.runLater(() -> updateUI.accept("Остановка службы...\n"));

                int result = cmd.scStop();
                String resultState = cmd.scState().getServiceState();

                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));

                if (result == 0)
                    Platform.runLater(() -> updateUI.accept("Служба остановлена.\n"));
                else
                    Platform.runLater(() -> updateUI.accept("Произошла ошибка, код " + result + ", статус службы: " + resultState + "\n"));

            } else if (sc == null) {
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, "Не установленно в системе")));
                Platform.runLater(() -> updateUI.accept("Не удалось найти установленную службу в системе.\n"));
            } else {
                Platform.runLater(() -> updateUI.accept("Не удалось остановить службу т.к. статус службы: " + sc.getServiceState() + "\n"));
            }

        } catch (InterruptedException interEx) {
            Platform.runLater(() -> updateUI.accept("InterruptedException: " + interEx.getMessage() + "\n"));
        } catch (IOException ioEx) {
            Platform.runLater(() ->  updateUI.accept("IOException: " + ioEx.getMessage() + "\n"));
        } catch (Exception ex) {
            Platform.runLater(() -> updateUI.accept("Exception: " + ex.getMessage() + "\n"));
        }
    }

    private void runService() {
        CommandService cmd = new CommandService(this.nameService);
        Platform.runLater(() -> updateUI.accept("=======Запуск службы "+ this.nameService +"=======\n"));

        try {

            Platform.runLater(() -> updateUI.accept("Получаю статус службы...\n"));
            Service sc = cmd.scState();
            Platform.runLater(() -> updateUI.accept("Статус получен.\n"));

            if (sc != null && sc.isStopped()) {
                Platform.runLater(() ->  updateUI.accept("Запуск службы...\n"));

                int result = cmd.scStart();
                String resultState = cmd.scState().getServiceState();

                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, resultState)));

                if (result == 0)
                    Platform.runLater(() -> updateUI.accept("Служба запущена.\n"));
                else
                    Platform.runLater(() -> updateUI.accept("Произошла ошибка, код " + result + ", статус службы: " + resultState + "\n"));

            } else if (sc == null) {
                Platform.runLater(() -> setStatus.accept(String.join(":", this.nameService, "Не установленно в системе")));
                Platform.runLater(() -> updateUI.accept("Не удалось найти установленную службу в системе.\n"));
            } else {
                Platform.runLater(() -> updateUI.accept("Не удалось запустить службу т.к. статус службы: " + sc.getServiceState() + "\n"));
            }

        } catch (InterruptedException interEx) {
            Platform.runLater(() -> updateUI.accept("InterruptedException: " + interEx.getMessage() + "\n"));
        } catch (IOException ioEx) {
            Platform.runLater(() -> updateUI.accept("IOException: " + ioEx.getMessage() + "\n"));
        } catch (Exception ex) {
            Platform.runLater(() -> updateUI.accept("Exception: " + ex.getMessage() + "\n"));
        }
    }
}
