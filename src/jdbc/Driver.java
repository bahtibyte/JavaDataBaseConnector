package jdbc;

import com.jtattoo.plaf.aero.AeroLookAndFeel;
import com.jtattoo.plaf.bernstein.BernsteinLookAndFeel;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import com.jtattoo.plaf.noire.NoireLookAndFeel;
import com.jtattoo.plaf.smart.SmartLookAndFeel;
import com.jtattoo.plaf.texture.TextureLookAndFeel;
import jdbc.helpers.Constants;
import jdbc.helpers.IconHelper;
import jdbc.oop.Login;

import javax.swing.*;
import java.sql.Connection;
import java.util.Properties;

public class Driver {

    public static void connect(Login login) {
        JDBC jdbc = new JDBC(login);
    }

    public static void validate() {
        Validation preValidation = new Validation();
    }

    public static void main(String args[]) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        //Properties props = new Properties();
        //props.put("logoString", "");
        //SmartLookAndFeel.setCurrentTheme(props);
        //UIManager.setLookAndFeel("com.jtattoo.plaf.aero.SmartLookAndFeel");

        IconHelper.initialize();
        validate();
    }//main class

}