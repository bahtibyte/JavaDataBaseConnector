package jdbc.helpers;

import javax.swing.*;

/**
 * JOptionPane stops the current thread and waits till there is a user interaction,
 * therefore the message dialogs are invoked in its own thread such that it doesnt
 * interfere with the main jdbc program.
 */
public class Messages {

    public static void exception(String msg) {
        new Thread(() -> {
            JOptionPane.showMessageDialog(null, msg, "Exception thrown", JOptionPane.ERROR_MESSAGE);
        }).start();
    }

    public static void error(String msg) {
        new Thread(() -> {
            JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }).start();
    }

    public static void message(String msg, String title) {
        new Thread(() -> {
            JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
        }).start();
    }

}
