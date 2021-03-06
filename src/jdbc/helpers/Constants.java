package jdbc.helpers;

import jdbc.JDBC;
import jdbc.oop.Login;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Constants {

    /* Reference to the main object */
    public static JDBC jdbc;

    /* Only one active login could be present per launch. Re-connect refreshes Login */
    public static Login login;

    /* Sub frames created by the main frame. i.e. query frame, table frame, filter frames */
    public static ArrayList<JFrame> activeFrames = new ArrayList<JFrame>();

    /* Keeps a history of queries ran by the user */
    public static ArrayList<String> queries = new ArrayList<String>();

}
