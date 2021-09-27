package jdbc;

import net.miginfocom.layout.Grid;
import oop.Pair;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class DisplayResults {

    private JFrame frame;

    private Pair<String[], ArrayList<String[]>> pair;

    private String db, schema, table;

    public DisplayResults(Pair<String[], ArrayList<String[]>> pair, String db, String schema, String table){
        this.pair = pair;

        this.db = db;
        this.schema = schema;
        this.table = table;

        frame = new JFrame(db+" ~ "+schema+"."+table);

        JPanel panel = new JPanel(new GridLayout(1, 0));

        TableModel model = new TableModel();
        model.columnNames = pair.x;
        Object rows[] = pair.y.toArray();
        Object data[][] = new Object[rows.length][];
        for (int i = 0; i < rows.length; i++){
            data[i] = (Object[]) rows[i];
        }
        model.data = data;

        JTable jTable = new JTable(model);
        //jTable.setFillsViewportHeight(true);
        //jTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS);
        int width = resizeColumnWidth(jTable);
        int numRows = data.length > 25 ? 25 : data.length;
        int height = numRows  * 15 + 100;
        //System.out.println("final height: "+height);
        JScrollPane scrollPane = new JScrollPane(jTable);

        panel.add(scrollPane);

        panel.setOpaque(true);
        frame.setPreferredSize(new Dimension(width, height));

        frame.setContentPane(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public int resizeColumnWidth(JTable table) {
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
            //System.out.println(width);
            totalWidth += width;
        }
        //System.out.println("Total width:"+totalWidth);
        return totalWidth;
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

    public static void main(String args[]){
        new DisplayResults(null, "TEst", "test", "ac");
    }
}
