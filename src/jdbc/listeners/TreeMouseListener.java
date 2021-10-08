package jdbc.listeners;

import jdbc.helpers.Constants;
import jdbc.helpers.IconHelper;
import jdbc.helpers.Shared;
import jdbc.oop.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TreeMouseListener implements MouseListener {

    private JTree tree;

    public TreeMouseListener(JTree tree) {
        this.tree = tree;
    }

    private void doubleLeftClick(MouseEvent e) {
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

        Shared.executeQuery(selectAllSQL(getValue(nodes[2]), getValue(nodes[3])), getValue(nodes[1]), TreeMouseListener.class);
    }

    private void singleRightClick(MouseEvent e) {
        if (tree.getRowForLocation(e.getX(), e.getY()) == -1)
            return;

        TreePath cell = tree.getPathForLocation(e.getX(), e.getY());
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) cell.getLastPathComponent();
        System.out.println(node.getUserObject());
        TreeNode nodes[] = node.getPath();

        if (nodes.length == 1)
            return;

        tree.setSelectionPath(cell);

        TreeMouseListener self = this;

        final JPopupMenu popup = new JPopupMenu("Edit");
        JMenuItem newQueryItem = new JMenuItem("New Query");
        newQueryItem.setIcon(IconHelper.newQueryIcon);
        newQueryItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                self.createQuery(nodes);
            }
        });
        popup.add(newQueryItem);

        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        SwingUtilities.convertPointFromScreen(b, Constants.jdbc.frame);

        popup.show(Constants.jdbc.frame, (int) b.getX(), (int) b.getY());
    }

    private void createQuery(TreeNode nodes[]) {

        StringBuilder builder = new StringBuilder("\n");

        if (nodes.length >= 3) {
            builder.append("SELECT *\n");
            builder.append("FROM ");
            builder.append(getValue(nodes[2]));
            builder.append(".\"");
            if (nodes.length == 4) {
                builder.append(getValue(nodes[3]));
            }
            builder.append("\"\n");
        }
        String sql = builder.toString();
        Constants.jdbc.createNewQuery(sql, getValue(nodes[1]));
    }

    private String getValue(TreeNode node) {
        return ((Pair<String, Icon>) ((DefaultMutableTreeNode) node).getUserObject()).x;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1 && e.getClickCount() == 2)
            this.doubleLeftClick(e);

        if (e.getButton() == 3 && e.getClickCount() == 1)
            this.singleRightClick(e);
    }


    private String selectAllSQL(Object schema, Object table) {
        return "\nSELECT *\nFROM " + schema + ".[" + table + "]";
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
