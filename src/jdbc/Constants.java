package jdbc;

import jdbc.helpers.Login;

import javax.swing.*;
import java.util.ArrayList;

public class Constants {

    /* This frame should not be closed, else the main thread will be killed */
    public static JFrame jdbcFrame;

    /* Only one active login could be present per launch. Re-connect refreshes Login */
    public static Login login;

    /* Sub frames created by the main frame. i.e. query frame, table frame, filter frames */
    public static ArrayList<JFrame> activeFrames = new ArrayList<JFrame>();


}
