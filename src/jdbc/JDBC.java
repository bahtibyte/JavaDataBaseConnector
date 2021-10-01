package jdbc;

import jdbc.listeners.TreeMouseListener;
import jdbc.helpers.Login;
import jdbc.helpers.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class JDBC implements ActionListener {

    private static final String TITLE = "Java Database Connector";
    private static final int WIDTH = 900;
    private static final int HEIGHT = 500;

    private JFrame frame;

    private JMenuBar menuBar;
    private JMenu mainMenu, fileMenu, settings;
    private JMenuItem connectItem, refreshItem;
    private JMenuItem newQuery, executeQuery, openQueryItem, saveQueryItem;
    private JMenuItem preferencesItem;

    protected JTree dbTree;

    public static Login login;

    private HashMap<String, DefaultMutableTreeNode> dbLookup;
    private HashMap<String, DefaultMutableTreeNode> schemaLookup;

    private ArrayList<QueryPanel> tabs;

    private JTabbedPane tabbedPane;

    private int queryCounter = 0;

    public JDBC(Login login) {
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        Constants.login = this.login = login;
        Constants.jdbcFrame = frame;

        DefaultMutableTreeNode root = createTreeNodes();
        dbTree = new JTree(root);
        dbTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        dbTree.addMouseListener(new TreeMouseListener(dbTree));

        JScrollPane treeView = new JScrollPane(dbTree);
        tabbedPane = new JTabbedPane();
        tabs = new ArrayList<>();
        createNewQuery("");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, tabbedPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation((int)(WIDTH*0.30));

        this.initializeMenuBar();

        frame.getContentPane().add(splitPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Creates the full top menu bar on the frame. Items that have working functionality will have
     * .addActionListener(this) to link the button's click event to this class.
     */
    private void initializeMenuBar() {
        menuBar = new JMenuBar();

        mainMenu = new JMenu("Menu");

        connectItem = new JMenuItem("Connect");
        connectItem.addActionListener(this);
        mainMenu.add(connectItem);

        refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(this);
        mainMenu.add(refreshItem);

        fileMenu = new JMenu("File");

        newQuery = new JMenuItem("New Query");
        newQuery.addActionListener(this);
        fileMenu.add(newQuery);

        executeQuery = new JMenuItem("Execute Query");
        fileMenu.add(executeQuery);

        openQueryItem = new JMenuItem("Open Query");
        fileMenu.add(openQueryItem);

        saveQueryItem = new JMenuItem("Save Query");
        fileMenu.add(saveQueryItem);

        settings = new JMenu("Settings");

        preferencesItem = new JMenuItem("Preferences");
        settings.add(preferencesItem);

        menuBar.add(mainMenu);
        menuBar.add(fileMenu);
        menuBar.add(settings);

        frame.setJMenuBar(menuBar);
    }

    private DefaultMutableTreeNode createTreeNodes() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this.login.getNickname());

        this.dbLookup = new HashMap<>();
        this.schemaLookup = new HashMap<>();

        /* For each user specified databases, fetch and display all available tables in the db */
        for (String db : this.login.getDatabases()) {
            DefaultMutableTreeNode dbNode = new DefaultMutableTreeNode(db);
            dbLookup.put(db, dbNode);
            root.add(dbNode);

            prepareTable(db);
        }

        return root;
    }

    /**
     * Will fetch the database in the current login to make sure it exists, and extracts all the available
     * schemas and tables. This method is multi threaded and will populate the table tree on the left panel
     * once the tables are ready.
     *
     * @param db
     */
    private void prepareTable(final String db) {
        new Thread(() -> {
            try {
                final long timeout = 1500;
                long startTime = System.currentTimeMillis();
                long currentTime = System.currentTimeMillis();

                Query results = new Query();
                results.fetchTables(login, db);

                while (currentTime - startTime < timeout) {
                    Thread.sleep(100);
                    currentTime = System.currentTimeMillis();
                    if (results.isExceptionThrown() || results.isComplete())
                        break;
                }

                if (!results.isComplete()) {
                    System.out.println("Unable to retrieve information about database="+db);
                    dbLookup.get(db).add(new DefaultMutableTreeNode("Unavailable"));
                    frame.repaint();
                    return;
                }

                ArrayList<Pair<String, String>> pairs = results.extractTables(results.getResults());

                for (Pair<String, String> schemaTable : pairs) {

                    String schema = schemaTable.x;
                    String table = schemaTable.y;

                    String localizedSchema = db + "." + schema;
                    DefaultMutableTreeNode schemaNode = new DefaultMutableTreeNode(schema);

                    if (!schemaLookup.containsKey(localizedSchema)) {
                        schemaLookup.put(localizedSchema, schemaNode);
                        dbLookup.get(db).add(schemaNode);
                    }else{
                        schemaNode = schemaLookup.get(localizedSchema);
                    }

                    DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table);
                    schemaNode.add(tableNode);
                }

                frame.repaint();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Creates a new Query based on
     */
    private void createNewQuery(String db){
        QueryPanel queryPanel = new QueryPanel(db, QueryPanel.TABBED_PARENT);

        tabs.add(queryPanel);
        tabbedPane.addTab("New Query "+ (++queryCounter), null, queryPanel, "unsaved");
        tabbedPane.setSelectedIndex(tabs.size()-1);
    }

    /**
    * Invoked when user wants to connect to a new login
    * */
    private void reconnect() {
        this.frame.dispose();
        Constants.jdbcFrame = null;
        for (JFrame frame : Constants.activeFrames) {
            frame.dispose();
        }
        Constants.activeFrames.clear();
        Driver.validate();
    }

    private void refreshConnection() {
        this.frame.dispose();
        Constants.jdbcFrame = null;
        Driver.connect(login);
    }


    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(newQuery))
            createNewQuery("");

        if (source.equals(connectItem))
            reconnect();

        if (source.equals(refreshItem))
            refreshConnection();
    }
}
