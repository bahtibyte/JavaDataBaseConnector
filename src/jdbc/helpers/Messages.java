package jdbc.helpers;

import javax.swing.*;

public class Messages {


    public static void exception(String msg) {
        JOptionPane.showMessageDialog(null,msg, "Exception thrown", JOptionPane.ERROR_MESSAGE);
    }

    public static void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void message(String msg, String title) {
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
    }

}
