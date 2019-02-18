package ru.jackK.repairEAS.client.util.CMD;

import java.io.*;

public class CommandService {
    private String cmdGetServiceState = "cmd /c sc query";
    private String cmdServiceStop = "cmd /c sc stop";
    private String cmdServiceRun = "cmd /c sc start";
    private String cmdServicePause = "cmd /c sc pause";
    private String cmdServiceContinue = "cmd /c sc continue";

    private Service service;

    public CommandService(String serviceName) {
        service = new Service(serviceName);

        this.cmdGetServiceState = String.join(" ", this.cmdGetServiceState, serviceName);
        this.cmdServiceRun = String.join(" ", this.cmdServiceRun, serviceName);
        this.cmdServiceStop = String.join(" ", this.cmdServiceStop, serviceName);
        this.cmdServicePause = String.join(" ", this.cmdServicePause, serviceName);
        this.cmdServiceContinue = String.join(" ", this.cmdServiceContinue, serviceName);
    }

    public int commandExecute(String command) throws InterruptedException, IOException {
        Process cmd = Runtime.getRuntime().exec(command);

        int result = cmd.waitFor();

        if (result == 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream(), "cp866"));
            String strLine = null;

            while ((strLine = reader.readLine()) != null) {
                if (strLine == null || strLine.trim().isEmpty() || !strLine.contains(":")) continue;

                String[] splitLine = strLine.split(":");

                if (splitLine.length > 1) {
                    createService(splitLine[0], splitLine[1]);
                }
            }

            if (cmd.isAlive())
                cmd.destroy();

            reader.close();
        }

        return result;
    }

    public int scStop() throws InterruptedException, IOException {
        int resultCommand = this.commandExecute(this.cmdServiceStop);

        if (resultCommand == 0) {
            this.commandExecute(this.cmdGetServiceState);
            return this.service.isStopped() ? 0 : -1;
        }

        return resultCommand;
    }

    public int scStart() throws InterruptedException, IOException {
        int resultCommand = this.commandExecute(this.cmdServiceRun);

        if (resultCommand == 0) {
            this.commandExecute(this.cmdGetServiceState);
            return this.service.isRunning() ? 0 : -1;
        }

        return resultCommand;
    }

    public int scPause() throws InterruptedException, IOException {
        int resultCommand = this.commandExecute(this.cmdServicePause);

        if (resultCommand == 0) {
            this.commandExecute(this.cmdGetServiceState);
            return this.service.isPaused() ? 0 : -1;
        }

        return resultCommand;
    }

    public int scContinue() throws InterruptedException, IOException {
        int resultCommand = this.commandExecute(this.cmdServiceContinue);

        if (resultCommand == 0) {
            this.commandExecute(this.cmdGetServiceState);
            return this.service.isRunning() ? 0 : -1;
        }

        return resultCommand;
    }

    public int scRestart() throws InterruptedException, IOException {
        int resultCommand = -1;

        this.commandExecute(this.cmdGetServiceState);

        if (this.service.isRunning()) {
            resultCommand = this.commandExecute(this.cmdServiceStop);

            resultCommand = this.commandExecute(this.cmdServiceRun);
        } else {
            resultCommand = this.commandExecute(this.cmdServiceRun);
        }

        return resultCommand;
    }

    public Service scState() throws IOException, InterruptedException {
        int resultCommand = this.commandExecute(this.cmdGetServiceState);

        if (resultCommand == 0)
            return this.service;

        return null;
    }

    private void createService(String property, String value) {
        property = property == null ? "" : property.trim();
        value = value == null ? "" : value.trim();

        if (!property.isEmpty() && !value.isEmpty()) {
            switch (property.toLowerCase()) {
                case "тип":
                case "type":
                    this.service.setType(value);
                    break;
                case "состояние":
                case "state":
                    this.service.setState(value);
                    break;
            }
        }
    }
}
