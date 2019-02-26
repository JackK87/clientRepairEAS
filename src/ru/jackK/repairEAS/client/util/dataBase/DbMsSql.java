package ru.jackK.repairEAS.client.util.dataBase;

import ru.jackK.repairEAS.client.model.ColumnTable;
import ru.jackK.repairEAS.client.model.RowTable;
import ru.jackK.repairEAS.client.model.SqlParametr;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class DbMsSql extends DB {

    private static DbMsSql db;
    private SQLServerDataSource dataSource;


    /**
     * получаем экзэмпляр объекта DbMsSql используя паттерн Singleton.
     *
     * @return возращает объект DbMsSql.
     */
    public static DbMsSql getInstance(String server, int port, String user, String password) {
        if (db == null) {

            db = new DbMsSql(server, port, user, password);

            return db;
        }

        return db;
    }

    public static DbMsSql getInstance(String server, String user, String password) {
        return getInstance(server, 1433, user, password);
    }

    /**
     * конструктор объекта DbMsSql.
     */
    private DbMsSql(String server, int port, String user, String password) {
        this.server = server;
        this.user = user;
        this.password = password;

        this.port = port < 1 ? 1433 : port;
    }

    /**
     * подключение к базе данных.
     *
     * @param nameDb - имя базы данных к которой необходимо подключиться.
     * @throws - выбрасывает SQLException.
     */
    @Override
    public void connect(String nameDb) throws SQLException {
        connect(nameDb, 120);
    }

    /**
     * подключение к базе данных
     *
     * @param nameDb  - имя базы данных к которой необходимо подключиться.
     * @param timeout - время ожидания авторизации и выполнения запроса на SQL сервере.
     * @throws - выбрасывает SQLException.
     */
    @Override
    public void connect(String nameDb, int timeout) throws SQLException {
        this.setNameDb(nameDb);

        dataSource = new SQLServerDataSource();

        dataSource.setUser(this.getUser());
        dataSource.setPassword(this.getPassword());
        dataSource.setServerName(this.getServer());
        dataSource.setPortNumber(this.getPort());
        dataSource.setDatabaseName(this.getNameDb());

        dataSource.setLoginTimeout(timeout);
        dataSource.setQueryTimeout(timeout);

        this.connection = dataSource.getConnection();
    }

    /**
     * отключения от текущей базы данных.
     *
     * @throws - выбрасывает SQLException.
     */
    @Override
    public void disconnect() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }
    }

    /**
     * метод выполняет SQL запрос типа SELECT.
     *
     * @param query  - запрос типа SELECT в базу данных.
     * @param params - параметры запроса с указанием типа данных и значения.
     * @return - возвращает колекцию ArrayList или null если query пустая строка и подключение к базе данных закрыто.
     * @throws - выбрасывает SQLException.
     */
    @Override
    public ArrayList<ArrayList<RowTable>> executeQuery(String query, ArrayList<SqlParametr> params) throws SQLException {
        if (query != null && !query.trim().isEmpty() && this.connection != null && !this.connection.isClosed()) {

            ArrayList<ArrayList<RowTable>> resultCollection = new ArrayList<>();

            this.preparedStatement = this.connection.prepareStatement(query);

            this.setSQLParams(this.preparedStatement, params);

            ResultSet resultQuery = this.preparedStatement.executeQuery();
            ResultSetMetaData resultMetaData = resultQuery.getMetaData();

            ArrayList<ColumnTable> columnName = getColumnName(resultMetaData);

            while (resultQuery.next()) {

                ArrayList<RowTable> tableRows = new ArrayList<>();

                for (ColumnTable col : columnName) {

                    RowTable tableRow = new RowTable();
                    tableRow.setColumn(new ColumnTable(col.getName(), col.getType()));
                    tableRow.setValue(resultQuery.getString(col.getName()));

                    tableRows.add(tableRow);
                }
                resultCollection.add(tableRows);
            }

            if (this.preparedStatement != null && !this.preparedStatement.isClosed())
                this.preparedStatement.close();

            return resultCollection;
        }
        return null;
    }

    /**
     * метод для выполнения хранимой процедуры.
     *
     * @param executeProcedure - текст вызова хранимой процедуры.
     * @param params           - параметры в запросе.
     * @return возвращает false если в ходе выполнения произошла ошибка или нет соединения с базой данных, true в случае успешного выполнения.
     * @throws - выбрасывает SQLException.
     */
    @Override
    public boolean executeStoredProcedure(String executeProcedure, ArrayList<SqlParametr> params) throws SQLException {
        boolean result = false;

        if ((executeProcedure == null || executeProcedure.trim().isEmpty()) && (params == null || params.size() == 0)) {
            executeProcedure = "{call ReplicaExport(?)}";
            params = new ArrayList<>();
            params.add(new SqlParametr("int", String.valueOf(0)));
        }

        if (this.connection != null && !this.connection.isClosed()) {
            this.callableStatement = this.connection.prepareCall(executeProcedure);

            this.setSQLParams(this.callableStatement, params);

            this.callableStatement.execute();
            this.callableStatement.close();

            result = true;
        }

        return result;
    }

    /**
     * метод для выполнения SQL скриптов.
     *
     * @param script - текст скрипта.
     * @param params - параметры в запросе.
     * @return возвращает false если в ходе выполнения произошла ошибка или нет соединения с базой данных, true в случае успешного выполнения.
     * @throws - выбрасывает SQLException.
     */
    @Override
    public boolean executeScript(String script, ArrayList<SqlParametr> params) throws SQLException {
        if ((script != null && !script.trim().isEmpty()) && (this.connection != null && !this.connection.isClosed())) {
            this.preparedStatement = this.connection.prepareStatement(script);

            this.setSQLParams(this.preparedStatement, params);
            this.preparedStatement.execute();

            return true;
        }

        return false;
    }

    /**
     * метод для выполнения запросов тапа UPDATE, INSERT, DELETE, CREATE TABLE, DROP TABLE.
     *
     * @param query  - текст запроса.
     * @param params - параметры в запросе.
     * @return возвращает количество затронутых запросом записей.
     * @throws - выбрасывает SQLException.
     */
    @Override
    public int updateQuery(String query, ArrayList<SqlParametr> params) throws SQLException {
        int result = 0;

        if (query != null && !query.trim().isEmpty() && this.connection != null && !this.connection.isClosed()) {

            this.preparedStatement = this.connection.prepareStatement(query);
            this.setSQLParams(this.preparedStatement, params);

            result = this.preparedStatement.executeUpdate();

        }

        return result;
    }


    /**
     * метод предназначен для установки параметров запроса в объекте PreparedStatement.
     *
     * @param preparedStatement - объект класса PreparedStatement.
     * @param params            - коллекция параметров для объекта PreparedStatement.
     * @throws - выбрасывает SQLException.
     */
    private void setSQLParams(PreparedStatement preparedStatement, ArrayList<SqlParametr> params) throws SQLException {

        if (preparedStatement != null && params != null && params.size() != 0) {

            int index = 1;

            for (SqlParametr param : params) {
                switch (param.getType()) {
                    case "int":
                        preparedStatement.setInt(index, Integer.parseInt(param.getValue()));
                        break;
                    case "varchar":
                        preparedStatement.setString(index, param.getValue());
                        break;
                }

                index++;
            }
        }
    }

    /**
     * метод предназначен для получения мета данных из запроса. (имя и тип полей)
     *
     * @param resultMetaData - объект класса ResultMetaData.
     * @return возвращает коллекцию с типом ColumnTable.
     * @throws - выбрасывает SQLException.
     */
    private ArrayList<ColumnTable> getColumnName(ResultSetMetaData resultMetaData) throws SQLException {
        ArrayList<ColumnTable> columnName = new ArrayList<>();
        int columnCount = resultMetaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            columnName.add(new ColumnTable(resultMetaData.getColumnName(i), resultMetaData.getColumnTypeName(i)));
        }

        return columnName;
    }
}
