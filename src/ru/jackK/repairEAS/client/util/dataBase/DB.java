package ru.jackK.repairEAS.client.util.dataBase;

import ru.jackK.repairEAS.client.model.RowTable;
import ru.jackK.repairEAS.client.model.SqlParametr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class DB {

    protected String server;
    protected int port;

    protected String user;
    protected String password;

    protected String nameDb;

    protected Connection connection;
    protected PreparedStatement preparedStatement;
    protected CallableStatement callableStatement;

    public String getServer() {
        return server;
    }
    public int getPort() {
        return port;
    }
    public String getUser() {
        return user;
    }
    public String getPassword() {
        return password;
    }
    public String getNameDb() {
        return nameDb;
    }

    public void setNameDb(String nameDb) {
        this.nameDb = nameDb;
    }

    public abstract void connect(String nameDb) throws SQLException;
    public abstract void connect(String nameDb, int timeout) throws SQLException;
    public abstract void disconnect() throws SQLException;

    public abstract ArrayList<ArrayList<RowTable>> executeQuery(String query, ArrayList<SqlParametr> params) throws SQLException;
    public abstract boolean executeScript(String script, ArrayList<SqlParametr> params) throws SQLException;

    public abstract boolean executeStoredProcedure(String executeProcedure, ArrayList<SqlParametr> params) throws SQLException;

    public abstract int updateQuery(String query, ArrayList<SqlParametr> params) throws  SQLException;
}
