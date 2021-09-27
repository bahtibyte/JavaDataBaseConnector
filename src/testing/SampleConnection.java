package testing;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SampleConnection {

    public static void main(String args[]) {


        String connectionUrl = "jdbc:sqlserver://192.168.1.156:12002";

        Connection con = null;

        final JOptionPane jop = new JOptionPane();
        jop.setMessageType(JOptionPane.WARNING_MESSAGE);
        jop.setMessage("Connecting to the server, please wait");
        final JDialog[] dialog = {null};

        new Thread(new Runnable() {
            public void run() {
                dialog[0] = jop.createDialog(null, "Connecting...");
                dialog[0].setVisible(true);
            }

        }).start();


        try {

            System.out.println("Starting");

            con = DriverManager.getConnection(connectionUrl, "SA", "PH@123456789");

            boolean reachable = con.isValid(3);

            if (reachable) {
                // Connected Successfully
                System.out.println("Finished");
                dialog[0].dispose();

            }

        } catch (SQLServerException se) {
            System.out.println("Failed Login");
            se.printStackTrace();
            dialog[0].dispose();
        } catch (SQLException e){
            e.printStackTrace();
            dialog[0].dispose();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

}
