package jdbc;

import oop.Login;

import javax.swing.*;

public class Driver {

    public static void connect(Login login) {
        JDBC jdbc = new JDBC(login);
    }

    public static void main(String args[]) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Validation preValidation = new Validation();
    }

}
