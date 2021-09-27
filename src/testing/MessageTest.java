package testing;

import jdbc.Results;
import oop.Login;

import javax.swing.*;
import java.sql.*;

public class MessageTest {

    public void fetchAll(String db, String schema, String table) {
        try {
            String mysqlUrl = "jdbc:sqlserver://192.168.1.156:12001;databaseName="+db;

            Connection con = DriverManager.getConnection(mysqlUrl, "SA", "PH@123456789");

            Statement stmt = con.createStatement();

            String SQL = "SELECT * FROM "+schema+".["+table+"]";

            ResultSet rs = stmt.executeQuery(SQL);

            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();

            String cols[] = new String[count];

            for (int i = 1; i <= count; i++) {
                cols[i - 1] = rsMetaData.getColumnName(i);
                System.out.println(cols[i-1]);
            }

            while (rs.next()) {

                for (int i = 1; i <= count; i++){
                    System.out.println(i+":"+rs.getString(i));
                }

                break;
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        new MessageTest().fetchAll("AdventureWorks2017", "HumanResources", "Employee");

        boolean x = true;
        if (x)
            return;

        new Thread(new Runnable() {
            public void run() {
                try {
                    final long timeout = 1000;
                    long startTime = System.currentTimeMillis();
                    long currentTime = System.currentTimeMillis();

                    Results results = new Results();
                    //results.fetchTables(login, db);
                    System.out.println("Starting my code");
                    while (currentTime - startTime < timeout) {
                        Thread.sleep(100);
                        //System.out.println("Here I am ");
                        currentTime = System.currentTimeMillis();
                    }
                    System.out.println("I have finished");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public static void main22(String[] args) throws SQLException {


        //Getting the connection
        String mysqlUrl = "jdbc:sqlserver://192.168.1.156:12001;databaseName=AdventureWorks2017";
        Connection con = DriverManager.getConnection(mysqlUrl, "SA", "PH@123456789");

        DatabaseMetaData metaData = con.getMetaData();
        String[] types = {"TABLE"};

        ResultSet tables = metaData.getTables(null, null, "%", types);

        ResultSetMetaData rsMetaData = tables.getMetaData();
        int count = rsMetaData.getColumnCount();
        for(int i = 1; i<=count; i++) {
            System.out.println(rsMetaData.getColumnName(i));
        }

        while (tables.next()) {
            System.out.println(tables.getString("TABLE_SCHEM")+" "+tables.getString("TABLE_NAME"));
        }

    }

    public static void main2(String[] args){
        JOptionPane jop = new JOptionPane();
        jop.setMessageType(JOptionPane.ERROR_MESSAGE);
        jop.setMessage("Unable to Connect to the server");
        JDialog dialog = jop.createDialog(null, "Connection Error");

        // Set a 2 second timer
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                }
                dialog.dispose();
            }

        }).start();



        dialog.setVisible(true);
    }
}
