package ru.jackK.repairEAS.client.util.cmd;

import ru.jackK.repairEAS.client.util.threadWorker.StateService;

import java.util.HashMap;

public class Service {
    private HashMap<String, StateService> serviceState = new HashMap<>(); //{ "1  STOPPED", "4  RUNNING", "7  PAUSED" }
    private String type;
    private String state;
    private String name;

    String getState() {
        return state;
    }
    String getType() {
        return type;
    }
    String getName() {
        return name;
    }

    void setState(String state) {
        this.state = state;
    }
    void setType(String type) {
        this.type = type;
    }
    void setName(String name) {
        this.name = name;
    }

    public Service() {}
    public Service(String name) {
        this.name = name;

        serviceState.put("1  STOPPED", StateService.STOP);
        serviceState.put("4  RUNNING", StateService.RUN);
        serviceState.put("7  PAUSED", StateService.PAUSE);
    }
    public Service(String name, String type, String state) {
        this(name);
        this.type = type;
        this.state = state;
    }

    public String getServiceState() {
        if (serviceState.containsKey(state))
            return serviceState.get(state).toString();

        return "Не определенно";
    }

    public boolean isStopped() {
        return serviceState.containsKey(this.state);
    }

    public boolean isRunning() {
        return serviceState.containsKey(this.state);
    }

    public boolean isPaused() {
        return serviceState.containsKey(this.state);
    }
}
