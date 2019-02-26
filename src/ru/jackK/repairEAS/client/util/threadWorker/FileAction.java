package ru.jackK.repairEAS.client.util.threadWorker;

import javafx.application.Platform;
import ru.jackK.repairEAS.client.util.fileWorker.FileWorker;

import java.util.ArrayList;
import java.util.function.Consumer;

public class FileAction implements Runnable {

    private String pathFile;
    Consumer<String> writeLog;

    public FileAction(String pathFile, Consumer<String> writeLog) {
        this.pathFile = pathFile;
        this.writeLog = writeLog;
    }

    @Override
    public void run() {
        clearFolder();
    }

    private void clearFolder() {
        ArrayList<String> errDeleteFile = new ArrayList<>();

        Platform.runLater(() -> writeLog.accept("=======Удаление файлов в папке " + this.pathFile + "=======\n"));
        FileWorker.deleteAllFile(pathFile, errDeleteFile);

        if (errDeleteFile.size() == 0)
            Platform.runLater(() -> writeLog.accept("Удаление файлов успешно завершено.\n"));
        else
            Platform.runLater(() -> writeLog.accept("Не все файлы были удалены.\n"));
    }
}
