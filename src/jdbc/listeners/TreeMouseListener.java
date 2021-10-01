package jdbc.listeners;

import jdbc.DisplayResults;
import jdbc.Query;
import jdbc.helpers.Messages;
import jdbc.helpers.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class TreeMouseListener implements MouseListener {

    private JTree tree;

    public TreeMouseListener(JTree tree) {
        this.tree = tree;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getClickCount() != 2)
            return;

        if (tree.getRowForLocation(e.getX(), e.getY()) == -1)
            return;

        TreePath cell = tree.getPathForLocation(e.getX(), e.getY());
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) cell.getLastPathComponent();

        if (!node.isLeaf())
            return;

        display(node.getPath());
    }

    public void display(TreeNode nodes[]) {
        if (nodes.length != 4)
            return;

        String db = nodes[1].toString();
        String schema = nodes[2].toString();
        String table = nodes[3].toString();

        new Thread(() -> {
            try {
                final long timeout = 1500;
                final long startTime = System.currentTimeMillis();

                Query query = new Query();
                query.runCustomQuery(selectAllSQL(db, schema, table));

                long currentTime = System.currentTimeMillis();
                while (currentTime - startTime < timeout) {
                    Thread.sleep(100);
                    currentTime = System.currentTimeMillis();
                    if (query.isExceptionThrown() || query.isComplete())
                        break;
                }

                if (query.isExceptionThrown()) {
                    Messages.exception(query.getExceptionMessage());
                }

                else if (!query.isComplete()) {
                    Messages.error("Unable to complete Query.\n@TreeMouseListener");
                }

                else{
                    Pair<String[], ArrayList<String[]>> pair = query.extraRows(query.getResults());
                    DisplayResults display = new DisplayResults(pair, db, schema, table, query.lastSQL);
                }

            } catch (Exception e) {
                Messages.exception("Unable to open table.\n@TreeMouseListener");
                e.printStackTrace();
            }
        }).start();
    }

    private String selectAllSQL(String db, String schema, String table) {
        String sql = "USE " + db + "\n\n" +
                     "SELECT *\n" +
                     "FROM " + schema + ".[" + table + "]";
        return sql;
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) {}
}
