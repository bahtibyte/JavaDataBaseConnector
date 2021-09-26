import demo.TreeDemo;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

public class JDBC extends JPanel implements TreeSelectionListener, TreeExpansionListener {

    private static final String TITLE = "Java Data Base Connector";
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;

    private JFrame frame;

    private JMenuBar menuBar;
    private JMenu mainMenu, fileMenu;
    private JMenuItem connectItem, openQueryItem, saveQueryItem;

    private JTree tree;

    public JDBC() {

        this.initializeFrame();
        this.initializeMenuBar();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Connection");
        createNodes(root);

        tree = new JTree(root);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        tree.addTreeExpansionListener(this);

        JScrollPane treeView = new JScrollPane(tree);

        JTextArea textField = new JTextArea();
        textField.setLineWrap(false);
        textField.setWrapStyleWord(true);
        JScrollPane textView = new JScrollPane(textField);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, textView);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);

        Dimension minimumSize = new Dimension(100, 50);
        treeView.setMinimumSize(minimumSize);
        textView.setMinimumSize(minimumSize);

        splitPane.setPreferredSize(new Dimension(400, 200));


        frame.getContentPane().add(splitPane);

        frame.pack();
        frame.setVisible(true);
    }

    private void initializeFrame() {
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
    }

    private void initializeMenuBar() {
        menuBar = new JMenuBar();

        mainMenu = new JMenu("Menu");
        connectItem = new JMenuItem("Connection");
        mainMenu.add(connectItem);

        fileMenu = new JMenu("File");
        openQueryItem = new JMenuItem("Open Query");
        saveQueryItem = new JMenuItem("Save Query");
        fileMenu.add(openQueryItem);
        fileMenu.add(saveQueryItem);

        menuBar.add(mainMenu);
        menuBar.add(fileMenu);

        frame.setJMenuBar(menuBar);
    }

    private void createNodes(DefaultMutableTreeNode root){

        DefaultMutableTreeNode db1 = new DefaultMutableTreeNode("First Database");
        root.add(db1);

        //original Tutorial
        DefaultMutableTreeNode t1 = new DefaultMutableTreeNode("Table 1");
        db1.add(t1);

        //original Tutorial
        DefaultMutableTreeNode t2 = new DefaultMutableTreeNode("Table 2");
        db1.add(t2);

        //original Tutorial
        DefaultMutableTreeNode t3 = new DefaultMutableTreeNode("Table 3");
        db1.add(t3);

        DefaultMutableTreeNode db2 = new DefaultMutableTreeNode("Second Database");
        root.add(db2);

        //original Tutorial
        DefaultMutableTreeNode t1a = new DefaultMutableTreeNode("Table 1");
        db2.add(t1a);

        //original Tutorial
        DefaultMutableTreeNode t2a = new DefaultMutableTreeNode("Table 2");
        db2.add(t2a);

        //original Tutorial
        DefaultMutableTreeNode t3a = new DefaultMutableTreeNode("Table 3");
        db2.add(t3a);

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
}
