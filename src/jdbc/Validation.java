package jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import net.miginfocom.swing.MigLayout;
import jdbc.oop.Login;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class Validation implements ActionListener, KeyListener {

    private JFrame frame;
    private JPanel panel;

    private JLabel messageLabel, nicknameLabel, serverLabel, portLabel, userLabel, passwordLabel, databasesLabel;
    private JTextField nicknameField, serverField, portField, userField, passwordField;
    private JTextArea databasesArea;

    private JButton connect, save, delete;

    private JMenuBar menuBar;
    private JMenu menu;
    private ArrayList<JMenuItem> menuItems;

    private ArrayList<Login> logins;
    private Login lastLogin;

    private File jsonFile;

    public Validation() {

        jsonFile = getJsonFile();
        extractLogins();

        initialize();

        if (lastLogin != null) {
            loadLogin(lastLogin);
        }
    }

    private File getJsonFile() {
        File file = new File(System.getProperty("user.home")+"/.jdbc");

        if (!file.exists()) {
            file.mkdir();
        }

        File jsonFile = new File(file, "logins.json");

        if (!jsonFile.exists()){
            try {
                jsonFile.createNewFile();

                FileWriter jsonWriter = new FileWriter(jsonFile);
                jsonWriter.write(new JSONObject().toJSONString());
                jsonWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Unable to create logins.json file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return jsonFile;
    }

    private void extractLogins() {
        logins = new ArrayList<Login>();

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(jsonFile)) {

            JSONObject obj = (JSONObject) jsonParser.parse(reader);
            JSONArray loginsObject = (JSONArray) obj.get("saved");
            String last = (String) obj.get("last");

            if (last != null && logins != null){

                Iterator<JSONObject> iterator = loginsObject.iterator();

                while (iterator.hasNext()){
                    JSONObject item = iterator.next();

                    String nickname = (String) item.get("nickname");
                    String address = (String) item.get("address");
                    String port = (String) item.get("port");
                    String username = (String) item.get("username");
                    String password = (String) item.get("password");

                    JSONArray databasesObj = (JSONArray) item.get("databases");
                    Iterator<String> databaseIterator = databasesObj.iterator();

                    ArrayList<String> databases = new ArrayList<>();

                    while (databaseIterator.hasNext()) {
                        databases.add(databaseIterator.next());
                    }

                    Login login = new Login(nickname, address, port, username, password, databases);
                    logins.add(login);

                    if (nickname.equals(last)) {
                        lastLogin = login;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void writeLogins(){
        JSONObject root = new JSONObject();

        JSONArray loginArray = new JSONArray();
        for (int i = 0; i < logins.size(); i++) {
            Login login = logins.get(i);

            JSONObject loginObj = new JSONObject();
            loginObj.put("nickname", login.getNickname());
            loginObj.put("address", login.getAddress());
            loginObj.put("port", login.getPort());
            loginObj.put("username", login.getUsername());
            loginObj.put("password", login.getPassword());

            JSONArray dbArray = new JSONArray();
            for (String db : login.getDatabases()) {
                dbArray.add(db);
            }

            loginObj.put("databases", dbArray);

            loginArray.add(loginObj);
        }

        root.put("saved", loginArray);

        if (lastLogin != null) {
            root.put("last", lastLogin.getNickname());
        }else{
            root.put("last", "null");
        }

        try (FileWriter file = new FileWriter(jsonFile)) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(root.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        frame = new JFrame("JDBC");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);


        panel = new JPanel(new MigLayout("ins 15"));

        messageLabel = new JLabel("Connect to Server");
        panel.add(messageLabel, "span, center, gapbottom 15");

        nicknameLabel = new JLabel("Server Nickname:");
        panel.add(nicknameLabel);
        nicknameField = new JTextField();
        panel.add(nicknameField, "pushx, growx, wrap");

        serverLabel = new JLabel("Server Address:");
        panel.add(serverLabel);
        serverField = new JTextField();
        panel.add(serverField, "pushx, growx, wrap");

        portLabel = new JLabel("Server Port:");
        panel.add(portLabel);
        portField = new JTextField();
        panel.add(portField, "pushx, growx, wrap");

        userLabel = new JLabel("Username:");
        panel.add(userLabel);
        userField = new JTextField();
        panel.add(userField, "pushx, growx, wrap");

        passwordLabel = new JLabel("Password");
        panel.add(passwordLabel);
        passwordField = new JPasswordField();
        panel.add(passwordField, "growx, wrap");

        databasesLabel = new JLabel("Databases:");
        panel.add(databasesLabel);
        databasesArea = new JTextArea();
        databasesArea.addKeyListener(this);
        (databasesArea).setBorder(new JTextField().getBorder());
        panel.add(databasesArea, "pushx, growx, wrap");

        connect = new JButton("Connect");
        connect.addActionListener(this);
        panel.add(connect);

        save = new JButton("Save");
        save.addActionListener(this);
        panel.add(save, "span 2, split 2, right, gaptop 15");

        delete = new JButton("Delete");
        delete.addActionListener(this);
        panel.add(delete);

        menuBar = new JMenuBar();
        menu = new JMenu("Saved");

        menuItems = new ArrayList<JMenuItem>();
        updateMenuBar();

        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        frame.getContentPane().add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updateMenuBar() {
        menu.removeAll();
        menuItems.clear();
        for (Login login : logins){
            JMenuItem menuItem = new JMenuItem(login.getNickname());
            menuItem.addActionListener(this);
            menuItems.add(menuItem);
            menu.add(menuItem);
        }
    }

    private void loadLogin(Login login) {
        nicknameField.setText(login.getNickname());
        serverField.setText(login.getAddress());
        portField.setText(login.getPort());
        userField.setText(login.getUsername());
        passwordField.setText(login.getPassword());

        String names = login.getDatabases().toString();
        String dbs = names.substring(1, names.length()-1).replaceAll(", ", "\n");
        databasesArea.setText(dbs);

        System.out.println("Keep this print statement right here");
        this.frame.pack();
        this.frame.repaint();
    }

    private void saveLogin() {
        String nickname = nicknameField.getText();

        if (nickname.length() == 0) {
            return;
        }

        String address = serverField.getText();
        String port = portField.getText();
        String username = userField.getText();
        String password = passwordField.getText();
        ArrayList<String> dbs = new ArrayList<String>();
        for (String db : databasesArea.getText().split("\n")){
            dbs.add(db);
        }

        Login match = getMatchingName(nickname);

        Login login = new Login(nickname, address, port, username, password, dbs);

        if (match != null) {
            match.setLogin(login);
        }else{
            logins.add(login);
        }

        refreshLogins();
    }

    private void refreshLogins(){
        writeLogins();
        updateMenuBar();
    }

    private void deleteLogin() {
        String nickname = nicknameField.getText();
        Login matching = getMatchingName(nickname);
        if (matching != null) {
            logins.remove(matching);
        }
        if (lastLogin != null) {
            if (lastLogin.getNickname().equals(nickname)) {
                lastLogin = null;
            }
        }
        loadLogin(Login.EMPTY);
        refreshLogins();
        this.frame.pack();
    }

    private Login getMatchingName(String nickname){
        for (Login login : logins) {
            if (login.getNickname().equals(nickname)) {
                return login;
            }
        }
        return null;
    }

    private void menuItemAction(JMenuItem menuItem) {
        String nickname = menuItem.getText();
        Login matching = getMatchingName(nickname);
        if (matching != null) {
            loadLogin(matching);
            this.messageLabel.setText("Connect to server");
            this.frame.pack();
        }
    }

    private void startConnection() {

        String nickname = nicknameField.getText();
        String address = serverField.getText();
        String port = portField.getText();
        String username = userField.getText();
        String password = passwordField.getText();
        ArrayList<String> dbs = new ArrayList<String>();
        for (String db : databasesArea.getText().split("\n")){
            dbs.add(db);
        }

        Login current = new Login(nickname, address, port, username, password, dbs);
        Login match = getMatchingName(nickname);

        if (address.length() == 0 || port.length() == 0 || username.length() == 0 || password.length() == 0 ||
                databasesArea.getText().length() == 0) {
            messageLabel.setText("1 or more fields are empty");
            return;
        }

        String connectionUrl = "jdbc:sqlserver://"+address+":"+port;

        Connection con = null;

        final JOptionPane jop = new JOptionPane();
        jop.setMessageType(JOptionPane.PLAIN_MESSAGE);
        jop.setMessage("Connecting to the server, please wait");
        final JDialog[] dialog = {null};

        new Thread(new Runnable() {
            public void run() {
                frame.setVisible(false);
                dialog[0] = jop.createDialog(null, "Connecting...");
                dialog[0].setVisible(true);
            }

        }).start();

        try {
            Thread.sleep(150);

            System.out.println("Starting connection to "+connectionUrl);
            con = DriverManager.getConnection(connectionUrl, username, password);
            boolean reachable = con.isValid(3);

            if (reachable) {
                System.out.println("Connected Successfully");
                if (dialog[0] != null) {
                    dialog[0].dispose();
                }

                // If the current info is what we have on file, then update last login
                if (current.equals(match)) {
                    lastLogin = current;
                    writeLogins();
                }

                connectSuccessful(current);
            }

        } catch (SQLServerException se) {
            System.out.println("Failed Login");
            String error = se.toString();
            if (error.contains("The TCP/IP connection to the host ")) {
                messageLabel.setText("ERROR: Unable to connect to the server");
            }else if (error.contains("Login failed for user ")) {
                messageLabel.setText("ERROR: Invalid login details");
            }else if (error.contains("The port number ")) {
                messageLabel.setText("ERROR: The port number is invalid");
            }else{
                messageLabel.setText("Unable to connect, unknown reason");
            }
            se.printStackTrace();
            dialog[0].dispose();
            frame.setVisible(true);
        } catch (Exception e){
            e.printStackTrace();
            dialog[0].dispose();
            frame.setVisible(true);
        } finally {
            try {
                if (con != null) {
                    System.out.println("Closing initial connection attempt");
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectSuccessful(Login current) {
        Driver.connect(current);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getSource());

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(save)) {
                this.saveLogin();
            }

            if (source.equals(delete)) {
                this.deleteLogin();
            }

            if (source.equals(connect)) {
                this.startConnection();
            }
        }

        if (source instanceof JMenuItem) {
            for (JMenuItem menuItem : menuItems) {
                if (source.equals(menuItem)) {
                    menuItemAction((JMenuItem) menuItem);
                }
            }
        }
    }//test

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource().equals(databasesArea)) {
            frame.pack();
        }
    }
}
