package jdbc;

import jdbc.helpers.Constants;
import jdbc.helpers.Shared;
import jdbc.listeners.CodeAreaMouseListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class QueryPanel extends JPanel implements KeyListener, ActionListener {

    public static final int TABBED_PARENT = 1;
    public static final int JFRAME_PARENT = 2;

    private static final int fontSize = 14;
    public static final Font font = new Font("Consolas", Font.PLAIN, fontSize);

    private JFrame parentFrame;
    public final int parent;

    private JPanel topRow;
    private JPanel textPanel;
    private JPanel botRow;

    private JLabel useLabel;
    private JComboBox dropDown;
    private JLabel numberLine;
    public JTextArea codeArea;
    private JButton clearButton;
    private JButton executeButton;
    private JButton formatButton;
    private JButton saveButton;
    private JButton closeButton;

    public QueryPanel(JFrame parentFrame, String initialSql, String db, int source) {
        super(new MigLayout("fill"));

        this.parentFrame  = parentFrame;

        topRow = new JPanel(new MigLayout("", "[center, grow]"));
        //topRow.setBackground(Color.WHITE);

        useLabel = new JLabel("USE Database:");
        useLabel.setFont(font);
        topRow.add(useLabel, "span 2, split 2, left");

        String[] dbs = Constants.login.getDatabases().toArray(new String[Constants.login.getDatabases().size()]);
        dropDown = new JComboBox(dbs);
        if (db != null)
            dropDown.setSelectedItem(db);
        dropDown.setFont(font);
        topRow.add(dropDown, "");

        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        topRow.add(clearButton, "right");

        add(topRow, "dock north");

        codeArea = new JTextArea(initialSql);
        codeArea.setFont(font);
        codeArea.addKeyListener(this);
        codeArea.addMouseListener(new CodeAreaMouseListener(parentFrame, this));

        numberLine = new JLabel();
        numberLine.setFont(font);
        numberLine.setVerticalAlignment(JLabel.TOP);
        updateNumberLine();

        textPanel = new JPanel(new MigLayout());
        //textPanel.setBackground(Color.WHITE);
        textPanel.add(numberLine, "grow, pushy");
        textPanel.add(codeArea, "grow, pushx");

        add(new JScrollPane(textPanel), "grow, pushy");

        botRow = new JPanel(new MigLayout("", "[center, grow]"));
        //botRow.setBackground(Color.WHITE);

        executeButton = new JButton("Execute");
        executeButton.addActionListener(this);
        botRow.add(executeButton, "left");

        formatButton = new JButton("Format");
        formatButton.addActionListener(this);
        botRow.add(formatButton, "span 2, split 3, right");

        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        botRow.add(saveButton, "");

        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        botRow.add(closeButton, "");

        add(botRow, "dock south");

        this.parent = source;
    }

    public void updateNumberLine() {
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

    public String getSelectedDB() {
        return this.dropDown.getSelectedItem().toString();
    }

    public void executeQuery() {
        Shared.executeQuery(codeArea.getText(), getSelectedDB(), QueryPanel.class);
    }

    private void clearSql() {
        this.codeArea.setText("");
        this.codeArea.setCaretPosition(0);
        this.codeArea.requestFocus();
        this.updateNumberLine();
    }

    private void formatSql() {
        Shared.formatQuery(this);
    }

    private void saveSql() {
        Shared.saveQuery(this);
    }

    private void closePanel() {

        if (parent == JFRAME_PARENT) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            Constants.activeFrames.remove(parentFrame);
            parentFrame.dispose();
        }

        else if (parent == TABBED_PARENT) {
            JTabbedPane tabbedPane = Constants.jdbc.tabbedPane;
            int index = tabbedPane.getSelectedIndex();
            tabbedPane.remove(index);
            Constants.jdbc.queryPanels.remove(index);
            if (tabbedPane.getTabCount() == 0) {
                Constants.jdbc.queryCounter = 0;
                Constants.jdbc.createNewQuery("", null);
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(clearButton))
            clearSql();

        if (source.equals(executeButton))
            executeQuery();

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
