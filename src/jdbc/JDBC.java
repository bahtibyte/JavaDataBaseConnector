package jdbc;

import demo.TreeDemo;
import oop.Login;
import oop.Pair;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class JDBC extends JPanel implements TreeSelectionListener, TreeExpansionListener, ActionListener {

    private static final String TITLE = "Java Data Base Connector";
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;

    private JFrame frame;

    private JMenuBar menuBar;
    private JMenu mainMenu, fileMenu;
    private JMenuItem connectItem, refreshItem;
    private JMenuItem newQuery, openQueryItem, saveQueryItem;

    private JTree tree;

    private final Login login;

    private HashMap<String, DefaultMutableTreeNode> dbLookup;
    private HashMap<String, DefaultMutableTreeNode> schemaLookup;
    private HashMap<String, DefaultMutableTreeNode> tableLookup;

    private ArrayList<Pair<JPanel, JTextArea>> tabs;

    private JTabbedPane tabbedPane;

    private int queryCounter = 0;

    public JDBC(Login login) {

        this.login = login;
        this.dbLookup = new HashMap<>();
        this.schemaLookup = new HashMap<>();
        this.tableLookup = new HashMap<>();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(login.getNickname());
        createNodes(root);

        this.initializeFrame();
        this.initializeMenuBar();


        tree = new JTree(root);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        tree.addTreeExpansionListener(this);

        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (e.getClickCount() == 2) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                        if (!node.isLeaf()) {
                            return;
                        }
                        display(node.getPath());
                    }
                }
            }
        };
        tree.addMouseListener(ml);

        JScrollPane treeView = new JScrollPane(tree);

        tabbedPane = new JTabbedPane();

        tabs = new ArrayList<>();

        createNewQuery();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, tabbedPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(250);

        splitPane.setPreferredSize(new Dimension(400, 200));


        frame.getContentPane().add(splitPane);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void initializeFrame() {
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.setResizable(true);
    }

    private void initializeMenuBar() {
        menuBar = new JMenuBar();

        mainMenu = new JMenu("Menu");
        connectItem = new JMenuItem("Connection");
        refreshItem = new JMenuItem("Refresh");
        mainMenu.add(connectItem);
        mainMenu.add(refreshItem);

        fileMenu = new JMenu("File");
        newQuery = new JMenuItem("New Query");
        newQuery.addActionListener(this);
        openQueryItem = new JMenuItem("Open Query");
        saveQueryItem = new JMenuItem("Save Query");
        fileMenu.add(newQuery);
        fileMenu.add(openQueryItem);
        fileMenu.add(saveQueryItem);

        menuBar.add(mainMenu);
        menuBar.add(fileMenu);

        frame.setJMenuBar(menuBar);
    }

    private void createNodes(DefaultMutableTreeNode root) {

        for (String db : this.login.getDatabases()) {
            dbLookup.put(db, new DefaultMutableTreeNode(db));
            root.add(dbLookup.get(db));
            prepareTable(db);
        }
    }

    private void prepareTable(final String db) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    final long timeout = 1500;
                    long startTime = System.currentTimeMillis();
                    long currentTime = System.currentTimeMillis();

                    Results results = new Results();
                    results.fetchTables(login, db);

                    while (currentTime - startTime < timeout) {
                        Thread.sleep(100);
                        if (results.getResults() != null) {
                            break;
                        }
                        currentTime = System.currentTimeMillis();
                    }

                    if (results.getResults() != null) {
                        ArrayList<Pair<String, String>> pairs = results.extractTables(results.getResults());

                        for (Pair<String, String> pair : pairs) {

                            String schema = pair.x;
                            String table = pair.y;

                            String localizedSchema = db + "." + schema;
                            String localizedTable = localizedSchema + "." + table;

                            if (!schemaLookup.containsKey(localizedSchema)) {
                                DefaultMutableTreeNode schemaNode = new DefaultMutableTreeNode(schema);
                                schemaLookup.put(localizedSchema, schemaNode);
                                dbLookup.get(db).add(schemaNode);
                            }

                            DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table);
                            tableLookup.put(localizedTable, tableNode);
                            schemaLookup.get(localizedSchema).add(tableNode);
                        }

                        frame.repaint();

                    } else {
                        dbLookup.get(db).add(new DefaultMutableTreeNode("Unavailable"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void display(TreeNode nodes[]) {

        if (nodes.length != 4) {
            return;
        }

        String db = nodes[1].toString();
        String schema = nodes[2].toString();
        String table = nodes[3].toString();

        System.out.println("db:" + db + " schema:" + schema + " table:" + table);

        new Thread(new Runnable() {
            public void run() {
                try {
                    final long timeout = 1500;
                    long startTime = System.currentTimeMillis();
                    long currentTime = System.currentTimeMillis();

                    Results results = new Results();
                    results.fetchAll(login, db, schema, table);

                    while (currentTime - startTime < timeout) {
                        Thread.sleep(100);
                        if (results.getResults() != null) {
                            break;
                        }
                        currentTime = System.currentTimeMillis();
                    }

                    if (results.getResults() != null) {
                        Pair<String[], ArrayList<String[]>> pair = results.extraRows(results.getResults());
                        DisplayResults display = new DisplayResults(pair, db, schema, table);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {

    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {

    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {

    }

    private void createNewQuery(){
        JPanel queryPanel = new JPanel();
        queryPanel.setLayout(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Ariel", Font.PLAIN, 15));
        queryPanel.add(textArea);

        tabbedPane.addTab("New Query "+ (++queryCounter), null, queryPanel, "unsaved");

        Pair<JPanel, JTextArea> pair = new Pair<>(queryPanel, textArea);
        tabs.add(pair);

        tabbedPane.setSelectedIndex(tabs.size()-1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(newQuery)) {
            createNewQuery();
        }
    }

    class TreePopup extends JPopupMenu {
        public TreePopup(JTree tree) {
            JMenuItem delete = new JMenuItem("Delete");
            JMenuItem add = new JMenuItem("Add");
            delete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    System.out.println("Delete child");
                }
            });
            add.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    System.out.println("Add child");
                }
            });
            add(delete);
            add(new JSeparator());
            add(add);
        }
    }
}
