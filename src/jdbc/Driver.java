package jdbc;

import jdbc.helpers.IconHelper;
import jdbc.oop.Login;

import javax.swing.*;

public class Driver {

    public static void connect(Login login) {
        JDBC jdbc = new JDBC(login);
    }

    public static void validate() {
        Validation preValidation = new Validation();
    }

    public static void main(String args[]) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        IconHelper.initialize();


        validate();
    }//main class

}