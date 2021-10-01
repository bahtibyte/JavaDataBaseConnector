package jdbc;

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

        validate();
    }

}

/*
// Dark LAF
try { 71029
    UIManager.setLookAndFeel(new NimbusLookAndFeel());
    UIManager.put("control", new Color(128, 128, 128));
    UIManager.put("info", new Color(128, 128, 128));
    UIManager.put("nimbusBase", new Color(18, 30, 49));
    UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
    UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
    UIManager.put("nimbusFocus", new Color(115, 164, 209));
    UIManager.put("nimbusGreen", new Color(176, 179, 50));
    UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
    UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
    UIManager.put("nimbusOrange", new Color(191, 98, 4));
    UIManager.put("nimbusRed", new Color(169, 46, 34));
    UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
    UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
    UIManager.put("text", new Color(230, 230, 230));
    //SwingUtilities.updateComponentTreeUI(this);
} catch (UnsupportedLookAndFeelException exc) {
    System.err.println("Nimbus: Unsupported Look and feel!");
}
*/