package jdbc;

import jdbc.helpers.Constants;
import jdbc.helpers.IconHelper;
import jdbc.helpers.Settings;
import jdbc.helpers.Shared;
import jdbc.listeners.TreeMouseListener;
import jdbc.oop.CellRenderer;
import jdbc.oop.Login;
import jdbc.oop.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class JDBC implements ActionListener {

    private static final String TITLE = "Java Database Connector";
    private static final int WIDTH = 900;
    private static final int HEIGHT = 700;

    public JFrame frame;

    private JMenuBar menuBar;
    private JMenu mainMenu, fileMenu, settings;
    private JMenuItem connectItem, refreshItem;
    private JMenuItem newQuery, executeQueryItem, openQueryItem, saveQueryItem;
    private JMenuItem preferencesItem;

    private JTree dbTree;

    private Login login;

    private HashMap<String, DefaultMutableTreeNode> dbLookup;
    private HashMap<String, DefaultMutableTreeNode> schemaLookup;

    public JTabbedPane tabbedPane;
    public ArrayList<QueryPanel> queryPanels;
    public int queryCounter = 0;

    public JDBC(Login login) {
        Constants.jdbc = this;

        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        Constants.login = this.login = login;

        DefaultMutableTreeNode root = createTreeNodes();
        dbTree = new JTree(root);
        dbTree.setCellRenderer(new CellRenderer());
        dbTree.setRowHeight(18);
        dbTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        dbTree.addMouseListener(new TreeMouseListener(dbTree));

        JScrollPane treeView = new JScrollPane(dbTree);
        tabbedPane = new JTabbedPane();
        queryPanels = new ArrayList<QueryPanel>();
        createNewQuery("", null);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, tabbedPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation((int)(WIDTH*0.30));

        this.initializeMenuBar();

        frame.getContentPane().add(splitPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Shared.registerFrame(frame);
    }

    /**
     * Creates the full top menu bar on the frame. Items that have working functionality will have
     * .addActionListener(this) to link the button's click event to this class.
     */
    private void initializeMenuBar() {
        menuBar = new JMenuBar();

        mainMenu = new JMenu("Menu");

        UIManager.getIcon("FileView.directoryIcon");
        UIManager.getIcon("FileView.fileIcon");
        UIManager.getIcon("FileView.computerIcon");
        UIManager.getIcon("FileView.hardDriveIcon");
        UIManager.getIcon("FileView.floppyDriveIcon");

        UIManager.getIcon("FileChooser.newFolderIcon");
        UIManager.getIcon("FileChooser.upFolderIcon");
        UIManager.getIcon("FileChooser.homeFolderIcon");

        connectItem = new JMenuItem("Connect");
        connectItem.addActionListener(this);
        connectItem.setIcon(IconHelper.connectIcon);
        mainMenu.add(connectItem);

        refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(this);
        refreshItem.setIcon(IconHelper.refreshIcon);
        mainMenu.add(refreshItem);

        fileMenu = new JMenu("File");

        newQuery = new JMenuItem("New Query");
        newQuery.addActionListener(this);
        newQuery.setIcon(IconHelper.newQueryIcon);
        fileMenu.add(newQuery);

        executeQueryItem = new JMenuItem("Execute Query");
        executeQueryItem.addActionListener(this);
        executeQueryItem.setIcon(IconHelper.executeQueryIcon);
        fileMenu.add(executeQueryItem);

        openQueryItem = new JMenuItem("Open Query");
        openQueryItem.addActionListener(this);
        openQueryItem.setIcon(IconHelper.openFileIcon);
        fileMenu.add(openQueryItem);

        saveQueryItem = new JMenuItem("Save Query");
        saveQueryItem.addActionListener(this);
        saveQueryItem.setIcon(IconHelper.saveQueryIcon);
        fileMenu.add(saveQueryItem);

        settings = new JMenu("Settings");

        preferencesItem = new JMenuItem("Preferences");
        preferencesItem.setIcon(IconHelper.settingsIcon);
        settings.add(preferencesItem);

        menuBar.add(mainMenu);
        menuBar.add(fileMenu);
        menuBar.add(settings);

        frame.setJMenuBar(menuBar);
        frame.setIconImage(IconHelper.magnifyingImage);
    }

    private DefaultMutableTreeNode createTreeNodes() {
        Pair<String, Icon> value = new Pair<String, Icon>(this.login.getNickname(), IconHelper.serverIcon16);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(value);

        this.dbLookup = new HashMap<>();
        this.schemaLookup = new HashMap<>();

        /* For each user specified databases, fetch and display all available tables in the db */
        for (String db : this.login.getDatabases()) {
            DefaultMutableTreeNode dbNode = new DefaultMutableTreeNode(new Pair<String, Icon>(db, IconHelper.dbIcon16));
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

                Query query = new Query();
                query.fetchTables(db);

                while (currentTime - startTime < timeout) {
                    Thread.sleep(100);
                    currentTime = System.currentTimeMillis();
                    if (query.isExceptionThrown() || query.isFetchTablesReady())
                        break;
                }

                if (!query.isFetchTablesReady()) {
                    System.out.println("Unable to retrieve information about database="+db);
                    dbLookup.get(db).add(new DefaultMutableTreeNode("Unavailable"));
                    frame.repaint();
                    return;
                }

                ArrayList<Pair<String, String>> pairs = query.getSchemaTables();

                for (Pair<String, String> schemaTable : pairs) {
                    String t=null;
                    try {
                        String schema = schemaTable.x;
                        String table = schemaTable.y;

                        String localizedSchema = db + "." + schema;
                        Pair<String, Icon> schemaPair = new Pair<String, Icon>(schema, IconHelper.schemaIcon);
                        DefaultMutableTreeNode schemaNode = new DefaultMutableTreeNode(schemaPair);

                        if (!schemaLookup.containsKey(localizedSchema)) {
                            schemaLookup.put(localizedSchema, schemaNode);
                            dbLookup.get(db).add(schemaNode);
                        } else {
                            schemaNode = schemaLookup.get(localizedSchema);
                        }

                        Pair<String, Icon> tablePair = new Pair<String, Icon>(table, IconHelper.tableIcon);
                        DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(tablePair);

                        t = schema+","+table+","+localizedSchema+","+schemaNode;

                        schemaNode.add(tableNode);
                    }catch (Exception e){
                        if (t!=null)
                            System.out.println(t);
                        e.printStackTrace();
                    }
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
    public void createNewQuery(String sql, String db){
        QueryPanel queryPanel = new QueryPanel(frame, sql, db, QueryPanel.TABBED_PARENT);
        queryPanels.add(queryPanel);
        tabbedPane.addTab("New Query "+ (++queryCounter), null, queryPanel, "unsaved");
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
    }

    /**
    * Invoked when user wants to connect to a new login
    * */
    private void reconnect() {
        this.frame.dispose();
        for (JFrame frame : Constants.activeFrames) {
            frame.dispose();
        }
        Constants.activeFrames.clear();
        Driver.validate();
    }

    private void refreshConnection() {
        this.frame.dispose();
        Driver.connect(login);
    }

    private void executeQuery() {
        this.queryPanels.get(tabbedPane.getSelectedIndex()).executeQuery();
    }

    private void saveQuery() {
        Shared.saveQuery(this.queryPanels.get(tabbedPane.getSelectedIndex()));
    }

    private void openQuery() {
        File selectedFile = Shared.getSelectedFile();

        if (selectedFile == null)
            return;

        String name = selectedFile.getName();
        int len = name.length();
        if (name.indexOf(".sql") != len-4)
            return;

        String sql = getSql(selectedFile);
        String db = null;
        if (sql.indexOf("USE") == 0){
            db = sql.substring(4, sql.indexOf("\n"));
            sql = sql.substring(sql.indexOf("\n")+1);
        }
        createNewQuery(sql, db);
        Constants.jdbc.tabbedPane.setTitleAt(Constants.jdbc.tabbedPane.getSelectedIndex(), name);
    }

    private String getSql(File file){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            reader.close();
            return builder.toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(newQuery))
            createNewQuery("", null);

        if (source.equals(connectItem))
            reconnect();

        if (source.equals(refreshItem))
            refreshConnection();

        if (source.equals(executeQueryItem))
            executeQuery();

        if (source.equals(saveQueryItem))
            saveQuery();

        if (source.equals(openQueryItem))
            openQuery();
    }
}
