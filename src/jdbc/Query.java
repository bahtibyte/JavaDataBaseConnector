package jdbc;

import jdbc.helpers.Login;
import jdbc.helpers.Pair;

import java.sql.*;
import java.util.ArrayList;

public class Query {

    private Connection connection;
    private ResultSet results;
    public String lastSQL;

    private String db;

    private Exception exception;


    public boolean isComplete() {
        return results != null;
    }

    public boolean isExceptionThrown() {
        return exception != null;
    }

    public String getExceptionMessage() {
        return this.exception.getMessage();
    }

    public void fetchTables(Login login, String db) {
        
        try {
            String mysqlUrl = "jdbc:sqlserver://"+login.getAddress()+":"+login.getPort()+";databaseName="+db;

            Connection con = DriverManager.getConnection(mysqlUrl, login.getUsername(), login.getPassword());

            DatabaseMetaData metaData = con.getMetaData();
            String[] types = {"TABLE"};

            results = metaData.getTables(null, null, "%", types);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Pair<String, String>> extractTables(ResultSet set) {

        ArrayList<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();

        try {
            while (set.next()) {
                String schema = set.getString("TABLE_SCHEM");
                String table = set.getString("TABLE_NAME");

                Pair<String, String> pair = new Pair<>(schema, table);
                pairs.add(pair);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        return pairs;
    }

    public void fetchAll(Login login, String db, String schema, String table) {
        try {
            String mysqlUrl = "jdbc:sqlserver://"+login.getAddress()+":"+login.getPort()+";databaseName="+db;

            Connection con = DriverManager.getConnection(mysqlUrl, login.getUsername(), login.getPassword());

            Statement stmt = con.createStatement();

            String SQL = "SELECT * FROM "+schema+".["+table+"]";
            lastSQL = SQL;

            ResultSet rs = stmt.executeQuery(SQL);

            results = rs;
        } catch (Exception e){
            e.printStackTrace();
        }
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
                Statement stmt = connection.createStatement();

                ResultSet rs = stmt.executeQuery(sql);
                lastSQL = sql;

                results = rs;
            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            }
        }).start();
    }

    private String getServerUrl() {
        final Login login = Constants.login;
        return "jdbc:sqlserver://"+ login.getAddress()+":"+ login.getPort()+";"+
                "user="+ login.getUsername()+";password="+ login.getPassword();
    }

    public void runQuery(Login login, String db, String sql) {
        try {
            String mysqlUrl = "jdbc:sqlserver://"+login.getAddress()+":"+login.getPort()+";databaseName="+db;

            Connection con = DriverManager.getConnection(mysqlUrl, login.getUsername(), login.getPassword());

            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(sql);
            lastSQL = sql;

            results = rs;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Pair<String[], ArrayList<String[]>> extraRows(ResultSet set){
        Pair<String[], ArrayList<String[]>> pairs = null;

        try {
            ResultSetMetaData rsMetaData = set.getMetaData();
            int count = rsMetaData.getColumnCount();

            String cols[] = new String[count+1];
            cols[0] = "";
            for (int i = 2; i <= count+1; i++) {
                cols[i - 1] = rsMetaData.getColumnName(i-1);
            }

            ArrayList<String[]> rows = new ArrayList<String[]>();

            pairs = new Pair<String[], ArrayList<String[]>>(cols, rows);
            int n = 1;
            while (set.next()) {
                String row[] = new String[count+1];
                row[0] = n++ + "";
                for (int i = 2; i <= count+1; i++) {
                    row[i-1] = set.getString(i-1);
                }
                rows.add(row);
            }
        }catch(Exception e){
            e.printStackTrace();
            return pairs;
        }

        return pairs;
    }

    public ResultSet getResults(){
        return results;
    }
}
