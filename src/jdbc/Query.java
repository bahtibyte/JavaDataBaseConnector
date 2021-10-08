package jdbc;

import jdbc.helpers.Constants;
import jdbc.helpers.Messages;
import jdbc.helpers.QueryResults;
import jdbc.oop.Login;
import jdbc.oop.Pair;

import java.sql.*;
import java.util.ArrayList;

public class Query {

    private ArrayList<Pair<String, String>> schemaTables;

    private Connection connection;
    private Exception exception;
    private QueryResults queryResults;

    public boolean isComplete() {
        return queryResults != null;
    }

    public boolean isFetchTablesReady() {
        return schemaTables != null;
    }

    public boolean isExceptionThrown() {
        return exception != null;
    }

    public String getExceptionMessage() {
        return this.exception.getMessage();
    }

    public ArrayList<Pair<String, String>> getSchemaTables() {
        return schemaTables;
    }

    public QueryResults getResults() {
        return queryResults;
    }

    public void fetchTables(String db) {
        new Thread(() -> {
            try {
                if (connection != null) {
                    Messages.error("Query already invoked. This error should not appear");
                    connection.close();
                    return;
                }

                connection = DriverManager.getConnection(getServerUrl() + ";databaseName=" + db);

                DatabaseMetaData metaData = connection.getMetaData();
                String[] types = {"TABLE"};

                ResultSet resultSet = metaData.getTables(null, null, "%", types);

                schemaTables = extractTables(resultSet);

            } catch (Exception e) {
                exception = e;
                e.printStackTrace();
            }
        }).start();
    }

    private ArrayList<Pair<String, String>> extractTables(ResultSet set) throws SQLException {
        ArrayList<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();

        while (set.next()) {
            String schema = set.getString("TABLE_SCHEM");
            String table = set.getString("TABLE_NAME");

            Pair<String, String> pair = new Pair<>(schema, table);
            pairs.add(pair);
        }

        return pairs;
    }

    public void runCustomQuery(String sql) {
        new Thread(() -> {
            try {
                if (connection != null) {
                    System.out.println("This query has been invoked already. Closing connection");
                    connection.close();
                    return;
                }

                connection = DriverManager.getConnection(this.getServerUrl());
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                ResultSet resultSet = statement.executeQuery(sql);

                queryResults = extractResults(resultSet, sql);
                Constants.queries.add(sql);

            } catch (Exception e) {
                exception = e;
                e.printStackTrace();
            }
        }).start();
    }

    private QueryResults extractResults(ResultSet resultSet, String sql) throws SQLException {

        ResultSetMetaData metaData = resultSet.getMetaData();

        int rows = getRowCount(resultSet);
        int cols = metaData.getColumnCount() + 1;

        String colNames[] = new String[cols];
        colNames[0] = "";

        for (int i = 1; i < cols; i++) {
            colNames[i] = metaData.getColumnName(i);
        }

        Object data[][] = new Object[rows][];

        for (int row = 0; row < rows; row++) {
            resultSet.next();
            data[row] = new Object[cols];
            data[row][0] = Integer.valueOf(row + 1);
            for (int i = 1; i < cols; i++) {
                data[row][i] = resultSet.getObject(i);
            }
        }

        return new QueryResults(data, colNames, sql, rows, cols);
    }

    private static int getRowCount(ResultSet resultSet) {
        if (resultSet == null) {
            return -1;
        }

        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            try {
                resultSet.beforeFirst();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }

        return -1;
    }

    private String getServerUrl() {
        final Login login = Constants.login;
        return "jdbc:sqlserver://" + login.getAddress() + ":" + login.getPort() + ";" +
                "user=" + login.getUsername() + ";password=" + login.getPassword();
    }
}
