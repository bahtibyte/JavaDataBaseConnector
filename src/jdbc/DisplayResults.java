package jdbc;

import jdbc.helpers.*;
import jdbc.oop.Pair;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Set;

public class DisplayResults extends AbstractTableModel implements ActionListener {

    private QueryResults queryResults;

    private JFrame frame;

    private JMenuBar menuBar;
    private JMenu exportMenu;
    private JMenu viewMenu;
    private JMenu settingsMenu;
    private JMenuItem openInExcelItem;
    private JMenuItem exportAllItem;
    private JMenuItem exportSelectedItem;
    private JMenuItem viewSQLItem;
    private JMenuItem filterItem;
    private JMenuItem preferencesItem;

    private JTable table;

    private String[] columnNames;
    private Object[][] data;

    private int colSort[];
    private int lastCol;

    public DisplayResults(QueryResults queryResults) {
        this.queryResults = queryResults;
        this.columnNames = queryResults.getColNames();
        this.data = queryResults.getData();

        this.colSort = new int[queryResults.getCols()];
        this.colSort[0] = 1;
        this.lastCol = 0;

        table = new JTable(this);
        this.re_renderTable();

        final DisplayResults self = this;
        JTableHeader header = table.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                self.columnClick(table.columnAtPoint(e.getPoint()));
            }
        });

        JPanel panel = new JPanel(new GridLayout(1, 0));
        panel.add(new JScrollPane(table));
        panel.setOpaque(true);

        this.initializeMenuBar();

        int width = resizeColumnWidth();
        int numRows = data.length > 25 ? 25 : data.length;
        int height = numRows  * 15 + 100;

        frame = new JFrame();
        frame.setPreferredSize(new Dimension(width, height));
        frame.setJMenuBar(menuBar);
        frame.setContentPane(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Shared.registerFrame(frame);
    }

    private void initializeMenuBar() {
        menuBar = new JMenuBar();

        exportMenu = new JMenu("Export");

        openInExcelItem = new JMenuItem("Open in Excel");
        openInExcelItem.addActionListener(this);
        exportMenu.add(openInExcelItem);

        exportAllItem = new JMenuItem("Export All");
        exportAllItem.addActionListener(this);
        exportMenu.add(exportAllItem);

        exportSelectedItem = new JMenuItem("Export Selected");
        exportSelectedItem.addActionListener(this);
        exportMenu.add(exportSelectedItem);

        viewMenu = new JMenu("View");

        viewSQLItem = new JMenuItem("View SQL");
        viewSQLItem.addActionListener(this);
        viewMenu.add(viewSQLItem);

        filterItem = new JMenuItem("Filter");
        filterItem.addActionListener(this);
        viewMenu.add(filterItem);

        settingsMenu = new JMenu("Settings");

        preferencesItem = new JMenuItem("Preferences");
        preferencesItem.addActionListener(this);
        settingsMenu.add(preferencesItem);

        menuBar.add(exportMenu);
        menuBar.add(viewMenu);
        menuBar.add(settingsMenu);
    }

    public void re_renderTable() {
        table.setFont(Settings.tableCellFont);
        table.setRowHeight(Settings.tableCellHeight);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        table.setDefaultRenderer(String.class, centerRenderer);
    }

    private void columnClick(final int col) {

        if (lastCol == col) {
            colSort[lastCol] = (colSort[lastCol] + 1) % 2;
        }else{
            colSort[lastCol] = 0;
            colSort[col]++;
        }

        lastCol = col;

        Comparator sorter = new Sorter(col);
        if (colSort[col] == 1)
            sorter = sorter.reversed();

        PriorityQueue<Object[]> pq = new PriorityQueue<>(sorter);

        for (Object[] obj : data)
            pq.add(obj);

        for (int i = 0; i < data.length; i++)
           data[i] = pq.remove();
    }

    /**
     * TODO: Modify this so it is based on column size, not data size
     * */
    public int resizeColumnWidth() {
        final TableColumnModel columnModel = table.getColumnModel();
        int totalWidth = 0;
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width +1 , width);
            }
            if(width > 300)
                width=300;
            width += 35;

            columnModel.getColumn(column).setPreferredWidth(width);

            totalWidth += width;
        }
        Dimension size
                = Toolkit.getDefaultToolkit().getScreenSize();

        // width will store the width of the screen
        int width = (int) (size.getWidth() * 0.90);

        return Math.min(width, totalWidth);
    }

    private void createSQLView(String db){
        JFrame sqlView = new JFrame();
        sqlView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        sqlView.setPreferredSize(new Dimension(500, 300));
        sqlView.setResizable(true);

        String sql = trim(queryResults.getSql(), db);
        sqlView.getContentPane().add(new QueryPanel(sqlView, sql, db, QueryPanel.JFRAME_PARENT));

        sqlView.pack();
        sqlView.setLocationRelativeTo(null);
        sqlView.setVisible(true);

        Shared.registerFrame(sqlView);
    }

    public String trim(String sql, String db) {
        if (sql.indexOf("USE ") == 0 && sql.indexOf(db) == 4)
            return sql.substring(db.length()+5);
        return sql;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(viewSQLItem)) {
            createSQLView(this.queryResults.getDB());
        }
    }
}
