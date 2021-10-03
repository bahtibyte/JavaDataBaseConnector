package jdbc.oop;

import jdbc.helpers.Settings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class CellRenderer extends DefaultTreeCellRenderer {
    private JLabel label;
    public CellRenderer() {
        label = new JLabel();
        label.setFont(new Font("Tahoma", Font.PLAIN, 13));
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label=(JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        Object o = ((DefaultMutableTreeNode) value).getUserObject();
        if (o instanceof Pair) {
            Pair<String, Icon> pair = (Pair<String, Icon>) o;
            label.setText(pair.x);
            label.setIcon(pair.y);
        } else {
            label.setIcon(null);
            label.setText(value.toString());
        }

        return label;
    }
}