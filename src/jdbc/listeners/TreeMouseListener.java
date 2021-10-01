package jdbc.listeners;

import jdbc.helpers.Shared;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

        String sql = selectAllSQL(nodes[1].toString(), nodes[2].toString(), nodes[3].toString());
        Shared.executeQuery(sql, TreeMouseListener.class);
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
