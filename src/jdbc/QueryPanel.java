package jdbc;

import net.miginfocom.swing.MigLayout;
import jdbc.helpers.Pair;

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

    private final int parent;

    private JPanel textPanel;
    private JPanel buttonPanel;

    private JLabel numberLine;
    private JTextArea codeArea;

    private JButton executeButton;
    private JButton formatButton;
    private JButton saveButton;
    private JButton closeButton;

    private int fontSize = 16;

    private String sql;

    public QueryPanel(int parent) {
        this("", parent);
    }

    public QueryPanel(String sql, int parent) {
        super(new MigLayout("fill"));

        this.parent = parent;

        this.sql = sql;

        Font font = new Font("Consolas", Font.PLAIN, fontSize);

        textPanel = new JPanel(new MigLayout());

        codeArea = new JTextArea(sql);
        codeArea.setFont(font);
        codeArea.addKeyListener(this);

        numberLine = new JLabel(getString(getCount()));
        numberLine.setFont(font);
        numberLine.setVerticalAlignment(JLabel.TOP);
        textPanel.add(numberLine, "grow, pushy");

        textPanel.add(codeArea, "grow, pushx");

        JScrollPane scrollPane = new JScrollPane(textPanel);

        add(scrollPane, "grow, pushy");

        buttonPanel = new JPanel(new MigLayout("", "[center, grow]"));

        executeButton = new JButton("Execute");
        executeButton.addActionListener(this);
        buttonPanel.add(executeButton, "left");

        formatButton = new JButton("Format");
        buttonPanel.add(formatButton, "span 2, split 3, right");

        saveButton = new JButton("Save");
        buttonPanel.add(saveButton, "");

        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton, "");

        add(buttonPanel, "dock south");


        textPanel.setBackground(Color.WHITE);
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


    /*
    public static void main(String args[]) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setResizable(true);

        frame.getContentPane().add(new QueryPanel());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }*/

    @Override
    public void keyTyped(KeyEvent e) {
        int count = getCount();
        numberLine.setText(getString(count));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int count = getCount();
        numberLine.setText(getString(count));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int count = getCount();
        numberLine.setText(getString(count));
    }

    private int getCount() {
        String text = codeArea.getText();
        int replace = text.length() - text.replace("\n", "").length();
        int count = replace + 1;
        return count;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(executeButton)) {

            customQuery();

        }

        if (e.getSource().equals(closeButton)) {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
        }
    }

    private void customQuery() {

        String sql = codeArea.getText();

        String db = extractDb(sql);

        new Thread(new Runnable() {
            public void run() {
                try {
                    final long timeout = 1500;
                    long startTime = System.currentTimeMillis();
                    long currentTime = System.currentTimeMillis();

                    Query results = new Query();
                    results.runQuery(JDBC.login, db, sql);

                    while (currentTime - startTime < timeout) {
                        Thread.sleep(100);
                        if (results.getResults() != null) {
                            break;
                        }
                        currentTime = System.currentTimeMillis();
                    }

                    if (results.getResults() != null) {
                        Pair<String[], ArrayList<String[]>> pair = results.extraRows(results.getResults());
                        DisplayResults display = new DisplayResults(pair, db, results.lastSQL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private String extractDb(String sql) {

        if (sql.contains("\n")) {
            for (String line : sql.split("\n")) {
                if (line.contains("USE ")) {
                    int index = line.indexOf("USE ");
                    return line.substring(index+4);
                }
            }
        }
        return null;
    }
}
