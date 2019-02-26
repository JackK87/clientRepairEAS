package ru.jackK.repairEAS.client.util.fileWorker;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.jackK.repairEAS.client.util.Config;

public class FileWorker {

    public static Config getConfig(String pathFile) throws IOException, ClassNotFoundException {

        File file = new File(pathFile);

        if (file != null && file.exists() && file.isFile()) {

            FileInputStream in = new FileInputStream(file);
            ObjectInputStream conf = new ObjectInputStream(in);

            Config config = (Config)conf.readObject();

            conf.close();
            in.close();

            return config;
        }

        return null;
    }
    public static void saveConfig(String pathFile, Config config) throws IOException {
        FileOutputStream out = new FileOutputStream(pathFile);
        ObjectOutputStream conf = new ObjectOutputStream(out);

        conf.writeObject(config);

        conf.close();
        out.close();
    }

    public static boolean deleteAllFile(String pathDir, ArrayList<String> errorList) {
        File dir = new File(pathDir);

        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles();

            for (int index = 0; index < files.length; index++) {
                if (!files[index].delete())
                    errorList.add(files[index].getName());
            }

            return  true;
        }

        return false;
    }

    public static Date getLastModifed(String path) {
        File file = new File(path);

        if (file.exists()) {
            return new Date(file.lastModified());
        }

        return new Date(0);
    }


    public static String getLastModifedSimpleFormat(String path, String format) {
        Date lastModifed = getLastModifed(path);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        return dateFormat.format(lastModifed);
    }
}
