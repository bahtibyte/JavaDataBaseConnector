package jdbc;

import jdbc.oop.Login;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

public class Driver {

    public static void connect(Login login) {
        JDBC jdbc = new JDBC(login);
    }

    public static void validate() {
        Validation preValidation = new Validation();
    }

    public static void main(String args[]) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());



        validate();
    }

}