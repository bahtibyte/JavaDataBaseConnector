package jdbc;

import jdbc.helpers.Pair;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class DisplayResults implements ActionListener {

    private JFrame frame;

    private Pair<String[], ArrayList<String[]>> pair;

    private String db, schema, dbTable;

    private JMenuBar menuBar;
    private JMenu exportMenu, viewMenu;
    private JMenuItem exportAllItem, exportSelectedItem, viewSQLItem, filterItem;

    private JTable table;
    private TableModel model;

    private int colSort[];
    private int lastCol;

    private String sql;

    public DisplayResults(Pair<String[], ArrayList<String[]>> pair, String db, String sql) {
        this(pair, db, "Custom","Query", sql);
    }

    public DisplayResults(Pair<String[], ArrayList<String[]>> pair, String db, String schema, String dbTable, String sql){
        this.pair = pair;

        this.db = db;
        this.schema = schema;
        this.dbTable = dbTable;

        if (!sql.contains("USE")) {
            sql = "USE " + db + "\n\n" + sql;
        }

        this.sql = sql;

        frame = new JFrame(db+" ~ "+schema+"."+dbTable);

        JPanel panel = new JPanel(new GridLayout(1, 0));

        model = new TableModel();
        model.columnNames = pair.x;
        Object rows[] = pair.y.toArray();
        Object data[][] = new Object[rows.length][];
        for (int i = 0; i < rows.length; i++){
            data[i] = (Object[]) rows[i];
        }
        model.data = data;

        colSort = new int[pair.x.length];
        colSort[0] = 1;
        lastCol = 0;

        table = new JTable(model);
        table.setFont(new Font("Consolas", Font.PLAIN, 15));
        table.setRowHeight(30);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        table.setDefaultRenderer(String.class, centerRenderer);

        int width = resizeColumnWidth();
        int numRows = data.length > 25 ? 25 : data.length;
        int height = numRows  * 15 + 100;

        final DisplayResults self = this;

        JTableHeader header = table.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                int column = table.columnAtPoint(point);
                self.columnClick(column);
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane);

        panel.setOpaque(true);
        frame.setPreferredSize(new Dimension(width, height));

        menuBar = new JMenuBar();

        exportMenu = new JMenu("Export");
        exportAllItem = new JMenuItem("Export All");
        exportSelectedItem = new JMenuItem("Export Selected");
        exportMenu.add(exportAllItem);
        exportMenu.add(exportSelectedItem);
        menuBar.add(exportMenu);

        viewMenu = new JMenu("View");
        viewSQLItem = new JMenuItem("View SQL");
        viewSQLItem.addActionListener(this);
        filterItem = new JMenuItem("Filter");
        viewMenu.add(viewSQLItem);
        viewMenu.add(filterItem);
        menuBar.add(viewMenu);

        frame.setJMenuBar(menuBar);


        frame.setContentPane(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }



    private void columnClick(final int col) {

        System.out.println("COl is: "+col);

        if (lastCol == col) {
            colSort[lastCol] = (colSort[lastCol] + 1) % 2;
        }else{
            colSort[lastCol] = 0;
            colSort[col]++;
        }

        lastCol = col;

        Comparator<Object[]> comparator = new Comparator<Object[]>() {
            @Override
            public int compare(Object[] x, Object[] y) {
                int comp;
                if (colSort[col] == 1) {
                    comp = x[col].toString().compareTo(y[col].toString());
                }else{
                    comp = y[col].toString().compareTo(x[col].toString());
                }

                if (comp != 0)
                    return comp;

                if (colSort[col] == 1) {
                    return Integer.parseInt(x[0].toString()) - Integer.parseInt(y[0].toString());
                }

                return Integer.parseInt(y[0].toString()) - Integer.parseInt(x[0].toString());
            }
        };

        PriorityQueue<Object[]> pq = new PriorityQueue<>(comparator);

        for (Object[] obj : model.data) {
            pq.add(obj);
        }

        Object[][] newData = new Object[model.data.length][];

        for (int i = 0; i < newData.length; i++){
           newData[i] = pq.remove();
        }

        model.data = newData;
    }

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

        return totalWidth;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(viewSQLItem)) {
            createSQLView();
        }
    }

    private void createSQLView(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 300));
        frame.setResizable(true);

        frame.getContentPane().add(new QueryPanel(sql, QueryPanel.JFRAME_PARENT));

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    class TableModel extends AbstractTableModel {
        private String[] columnNames;
        private Object[][] data;

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
    }
}
