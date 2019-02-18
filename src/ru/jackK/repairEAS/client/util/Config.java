package ru.jackK.repairEAS.client.util;

import java.io.Serializable;

public class Config implements Serializable {
    private String userNameIO;
    private String passwordIO;
    private String uriSocketIO;

    private String pathImportGMMQ;
    private String pathExportGMMQ;

    private String gmmqServiceName;
    private String shedulerServiceName;
    private String sqlServiceName;

    private String userNameSql;
    private String passwordSql;

    private boolean offlineMode;

    private String dateFormate;

    //region Геттеры
    public String getUsernameIO() {
        return this.userNameIO;
    }
    public String getPasswordIO() {
        return this.passwordIO;
    }
    public String getPathImportGMMQ() {
        return this.pathImportGMMQ;
    }
    public String getPathExportGMMQ() {
        return this.pathExportGMMQ;
    }
    public String getUriSocketIO() {
        return this.uriSocketIO;
    }
    public String getGmmqServiceName() {
        return this.gmmqServiceName;
    }
    public String getShedulerServiceName() {
        return this.shedulerServiceName;
    }
    public String getSqlServiceName() {
        return this.sqlServiceName;
    }
    public String getUserNameSql() {
        return this.userNameSql;
    }
    public String getPasswordSql() {
        return this.passwordSql;
    }
    public boolean getOfflineMode() {
        return this.offlineMode;
    }
    public String getDateFormate() {
        return this.dateFormate;
    }
    //endregion
    //region Сеттеры
    public void setUsernameIO(String username) {
        this.userNameIO = username;
    }
    public void setPasswordIO(String password) {
        this.passwordIO = password;
    }
    public void setPathImportGMMQ(String pathImportGMMQ) {
        this.pathImportGMMQ = pathImportGMMQ;
    }
    public void setPathExportGMMQ(String pathExportGMMQ) {
        this.pathExportGMMQ = pathExportGMMQ;
    }
    public void setUriSocketIO(String uriSocketIO) {
        this.uriSocketIO = uriSocketIO;
    }
    public void setGmmqServiceName(String gmmqServiceName) {
        this.gmmqServiceName = gmmqServiceName;
    }
    public void setShedulerServiceName(String shedulerServiceName) {
        this.shedulerServiceName = shedulerServiceName;
    }
    public void setSqlServiceName(String sqlServiceName) {
        this.sqlServiceName = sqlServiceName;
    }
    public void setUserNameSql(String userNameSql) {
        this.userNameSql = userNameSql;
    }
    public void setPasswordSql(String passwordSql) {
        this.passwordSql = passwordSql;
    }
    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }
    public void setDateFormate(String dateFormate) {
        this.dateFormate = dateFormate;
    }
    //endregion

    public Config() { }
    public Config(String username, String password) {
        this.userNameIO = username;
        this.passwordIO = password;
    }
    public Config(String username, String password, String uriSocketIO) {
        this(username, password);
        this.uriSocketIO = uriSocketIO;
    }





}
