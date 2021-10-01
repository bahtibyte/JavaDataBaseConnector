package jdbc;

import jdbc.helpers.Constants;
import jdbc.helpers.Messages;
import jdbc.helpers.Shared;
import net.miginfocom.swing.MigLayout;
import jdbc.oop.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class QueryPanel extends JPanel implements KeyListener, ActionListener {

    public static final int TABBED_PARENT = 1;
    public static final int JFRAME_PARENT = 2;

    private static final int fontSize = 16;
    public static final Font font = new Font("Consolas", Font.PLAIN, fontSize);

    private final int parent;

    private JPanel textPanel;
    private JPanel buttonPanel;

    private JLabel numberLine;
    private JTextArea codeArea;

    private JButton executeButton;
    private JButton formatButton;
    private JButton saveButton;
    private JButton closeButton;

    public QueryPanel(String initialSql, int parent) {
        super(new MigLayout("fill"));

        codeArea = new JTextArea(initialSql);
        codeArea.setFont(font);
        codeArea.addKeyListener(this);

        numberLine = new JLabel();
        numberLine.setFont(font);
        numberLine.setVerticalAlignment(JLabel.TOP);
        updateNumberLine();

        textPanel = new JPanel(new MigLayout());
        textPanel.setBackground(Color.WHITE);
        textPanel.add(numberLine, "grow, pushy");
        textPanel.add(codeArea, "grow, pushx");

        add(new JScrollPane(textPanel), "grow, pushy");

        buttonPanel = new JPanel(new MigLayout("", "[center, grow]"));

        executeButton = new JButton("Execute");
        executeButton.addActionListener(this);
        buttonPanel.add(executeButton, "left");

        formatButton = new JButton("Format");
        formatButton.addActionListener(this);
        buttonPanel.add(formatButton, "span 2, split 3, right");

        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        buttonPanel.add(saveButton, "");

        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton, "");

        add(buttonPanel, "dock south");

        this.parent = parent;
    }

    private void updateNumberLine() {
        String text = codeArea.getText();
        int replace = text.length() - text.replace("\n", "").length();
        int count = replace + 1;
        numberLine.setText(getString(count));
    }

    private String getString(int n){
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        for (int i = 1; i <= n; i++) {
            if (i < 10) {
                builder.append("&nbsp;");
            }
            builder.append(i);
            builder.append('.');
            if (i != n) {
                builder.append("<br/>");
            }
        }
        builder.append("</html>");
        return builder.toString();
    }

    private void customQuery() {
        String sql = codeArea.getText();
        Shared.executeQuery(sql, QueryPanel.class);
    }

    /**
     * TODO: Complete this function
     */
    private void formatSql() {

    }

    /**
     * TODO: Complete this function
     */
    private void saveSql() {

    }

    private void closePanel() {

        if (parent == JFRAME_PARENT) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            Constants.activeFrames.remove(parentFrame);
            parentFrame.dispose();
        }

        /* TODO: Implement this */
        else if (parent == TABBED_PARENT) {

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(executeButton))
            customQuery();

        if (source.equals(formatButton))
            formatSql();

        if (source.equals(saveButton))
            saveSql();

        if (source.equals(closeButton))
            closePanel();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getSource().equals(codeArea))
            updateNumberLine();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource().equals(codeArea))
            updateNumberLine();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource().equals(codeArea))
            updateNumberLine();
    }
}
