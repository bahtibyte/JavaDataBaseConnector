package testing;

import net.miginfocom.swing.MigLayout;
import oop.Login;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Validation implements ActionListener {

    private JFrame frame;

    private JLabel nicknameLabel, serverLabel, portLabel, userLabel, passwordLabel, databasesLabel;
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
        frame = new JFrame("Connect to Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300, 300));
        frame.setResizable(true);

        frame.setLayout(new MigLayout("ins 15"));

        nicknameLabel = new JLabel("Server Nickname:");
        frame.add(nicknameLabel);
        nicknameField = new JTextField();
        frame.add(nicknameField, "pushx, growx, wrap");

        serverLabel = new JLabel("Server Address:");
        frame.add(serverLabel);
        serverField = new JTextField();
        frame.add(serverField, "pushx, growx, wrap");

        portLabel = new JLabel("Server Port:");
        frame.add(portLabel);
        portField = new JTextField();
        frame.add(portField, "pushx, growx, wrap");

        userLabel = new JLabel("Username:");
        frame.add(userLabel);
        userField = new JTextField();
        frame.add(userField, "pushx, growx, wrap");

        passwordLabel = new JLabel("Password");
        frame.add(passwordLabel);
        passwordField = new JPasswordField();
        frame.add(passwordField, "growx, wrap");

        databasesLabel = new JLabel("Databases:");
        frame.add(databasesLabel);
        databasesArea = new JTextArea();
        (databasesArea).setBorder(new JTextField().getBorder());
        frame.add(databasesArea, "pushx, growx, wrap");

        connect = new JButton("Connect");
        connect.addActionListener(this);
        frame.add(connect);

        save = new JButton("Save");
        save.addActionListener(this);
        frame.add(save, "span 2, split 2, right, gaptop 15");

        delete = new JButton("Delete");
        delete.addActionListener(this);
        frame.add(delete);

        menuBar = new JMenuBar();
        menu = new JMenu("Saved");

        menuItems = new ArrayList<JMenuItem>();
        updateMenuBar();

        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

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
    }

    private void saveLogin() {
        String nickname = nicknameField.getText();
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
        }
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

            }
        }

        if (source instanceof JMenuItem) {
            for (JMenuItem menuItem : menuItems) {
                if (source.equals(menuItem)) {
                    menuItemAction((JMenuItem) menuItem);
                }
            }
        }
    }

    public static void main(String[] args){
        new Validation();
    }
}
